/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.restcomm.callmanager.mgcp;

import com.vnxtele.oracle.DBCfg;
import com.vnxtele.util.VDate;
import com.vnxtele.util.VNXLog;
import jain.protocol.ip.mgcp.message.parms.ConnectionDescriptor;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.sdp.Connection;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.jvoicexml.callmanager.Terminal;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.mobicents.VAppCfg;
import org.jvoicexml.implementation.mobicents.broadcast.BrcastObjState;
import org.jvoicexml.implementation.mobicents.broadcast.BroadcastObj;
import org.jvoicexml.implementation.mobicents.broadcast.DBMng;

import org.mobicents.servlet.sip.restcomm.FiniteStateMachine;
import org.mobicents.servlet.sip.restcomm.Sid;
import org.mobicents.servlet.sip.restcomm.State;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.ThreadSafe;
import org.mobicents.servlet.sip.restcomm.media.api.Call;
import org.mobicents.servlet.sip.restcomm.media.api.CallException;
import org.mobicents.servlet.sip.restcomm.media.api.CallObserver;
import org.mobicents.servlet.sip.restcomm.util.IPUtils;
import org.mobicents.servlet.sip.restcomm.util.TimeUtils;
import org.util.ExLog;
import org.util.SIPUtil;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe
public final class MgcpCallTerminal extends FiniteStateMachine
        implements Terminal, Call, MgcpConnectionObserver, MgcpIvrEndpointObserver,
        ObservableTelephony {

    private static final Logger LOGGER = Logger.getLogger(MgcpCallTerminal.class);
    // Call states.
    private static final State IDLE = new State(Status.IDLE.toString());
    private static final State QUEUED = new State(Status.QUEUED.toString());
    private static final State RINGING = new State(Status.RINGING.toString());
    private static final State IN_PROGRESS = new State(Status.IN_PROGRESS.toString());
    private static final State COMPLETED = new State(Status.COMPLETED.toString());
    private static final State BUSY = new State(Status.BUSY.toString());
    private static final State FAILED = new State(Status.FAILED.toString());
    private static final State NO_ANSWER = new State(Status.NO_ANSWER.toString());
    private static final State CANCELLED = new State(Status.CANCELLED.toString());
    private static final State TRYING = new State(Status.TRYING.toString());

    static {
        IDLE.addTransition(TRYING);
        IDLE.addTransition(RINGING);
        IDLE.addTransition(QUEUED);
        QUEUED.addTransition(BUSY);
        QUEUED.addTransition(RINGING);
        QUEUED.addTransition(IN_PROGRESS);
        QUEUED.addTransition(FAILED);
        QUEUED.addTransition(CANCELLED);
        QUEUED.addTransition(COMPLETED);
        RINGING.addTransition(IN_PROGRESS);
        RINGING.addTransition(FAILED);
        RINGING.addTransition(CANCELLED);
        RINGING.addTransition(COMPLETED);
        IN_PROGRESS.addTransition(COMPLETED);
        IN_PROGRESS.addTransition(FAILED);
        TRYING.addTransition(RINGING);
        TRYING.addTransition(QUEUED);
        TRYING.addTransition(FAILED);
        TRYING.addTransition(CANCELLED);
        TRYING.addTransition(BUSY);
    }
    private Sid sid;
    private Direction direction;
    private List<CallObserver> observers;
    private SipServletRequest initialInvite;
    private MgcpServer server;
    private MgcpSession session;
    private MgcpPacketRelayEndpoint relayEndpoint;
    private MgcpConferenceEndpoint cnfEndpoint;
    private MgcpIvrEndpoint ivrEndpoint;
    private MgcpConferenceEndpoint remoteEndpoint;
    private MgcpConnection userAgentConnection;
    private MgcpConnection relayOutboundConnection;
    private MgcpConnection relayInboundConnection;
    private MgcpConnection ivrOutboundConnection;
    private MgcpConnection ivrInboundConnection;
    private MgcpConference remoteConference;
    private MgcpConnection remoteOutboundConnection;
    private MgcpConnection remoteInboundConnection;
    private DateTime dateCreated;
    private DateTime dateStarted;
    private DateTime dateEnded;
    private volatile String digits;
    private volatile boolean muted;
    //for jvxml
    /** CallControl Listeners. */
    private final List<TelephonyListener> callControlListeners;
    //current URIs playing
    List<URI> currentAnnouncements=null;
    //
    private String callID;
    public BroadcastObj brcastObj=null;

    public MgcpCallTerminal(final MgcpServer server) 
    {
        super(IDLE);
        
        // Initialize the state machine.
        addState(IDLE);
        addState(QUEUED);
        addState(RINGING);
        addState(IN_PROGRESS);
        addState(COMPLETED);
        addState(BUSY);
        addState(FAILED);
        addState(NO_ANSWER);
        addState(CANCELLED);
        addState(TRYING);

        this.sid = Sid.generate(Sid.Type.CALL);
        VNXLog.debug("constructing a terminal with :"+sid);
        this.server = server;
        this.session = server.createMediaSession();
        this.observers = new ArrayList<CallObserver>();
        this.dateCreated = DateTime.now();
        this.direction = Direction.INBOUND;
        callControlListeners = new ArrayList<TelephonyListener>();
        callID = "";
        
    }

    public MgcpCallTerminal(final SipServletRequest initialInvite, final MgcpServer server) {
        this(server);
        this.direction = Direction.OUTBOUND_DIAL;
        this.initialInvite = initialInvite;
        setState(QUEUED);
    }
    public MgcpCallTerminal(final SipServletRequest initialInvite, final MgcpServer server,BroadcastObj obj) {
        this(server);
        this.direction = Direction.OUTBOUND_DIAL;
        this.initialInvite = initialInvite;
        brcastObj = obj;
        setState(QUEUED);
    }

    @Override
    public synchronized void addObserver(final CallObserver observer) 
    {
        if(observers.contains(observer)==false)
        {
            VNXLog.debug("adding CallObserver:"+observer);
            observers.add(observer);
        }
    }

    private String mmsTimedOutException() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("The server @ ").append(server.getDomainName()).append(" failed for Call-ID ")
                .append(initialInvite.getCallId())
                .append(" One or all of our requests failed to receive a response in time.");
        return buffer.toString();
    }

    private void block(final int numberOfRequests) throws InterruptedException {
        // ResponseTimeout * NumberOfRequests
        wait(server.getResponseTimeout() * numberOfRequests);
    }

    private void block(final int numberOfRequests, final State errorState) throws Exception {
        // ResponseTimeout * NumberOfRequests
        wait(server.getResponseTimeout() * numberOfRequests);
        if (errorState.equals(getState())) {
            throw new Exception(mmsTimedOutException());
        }
    }

    public synchronized void trying(final SipServletRequest request) throws CallException 
    {
        VNXLog.debug("...send trying message ...");
        assertState(IDLE);
        final SipServletResponse trying = request.createResponse(SipServletResponse.SC_TRYING);
        try {
            trying.send();
            if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(trying,VAppCfg.SIP_MSG_OUTGOING);
            initialInvite = request;
            setState(TRYING);
            fireStatusChanged();
            // Make sure that we have an offer.
            if (request.getContentLength() == 0 || !"application/sdp".equalsIgnoreCase(request.getContentType())) {
                final SipServletResponse response = request.createResponse(SipServletResponse.SC_BAD_REQUEST);
                response.send();
                if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(response,VAppCfg.SIP_MSG_OUTGOING);
                throw new CallException("The remote client did not send an SDP offer with their INVITE request.");
            }
            // Establish a connection between the user agent and the media server.
            relayEndpoint = session.getPacketRelayEndpoint();
            final byte[] offer = patchMedia(request.getInitialRemoteAddr(), request.getRawContent());
            final ConnectionDescriptor remoteDescriptor = new ConnectionDescriptor(new String(offer));
            userAgentConnection = session.createConnection(relayEndpoint, remoteDescriptor);
            userAgentConnection.addObserver(this);
            userAgentConnection.connect(ConnectionMode.SendRecv);
            block(1);
            if (MgcpConnection.OPEN.equals(userAgentConnection.getState())) {
                alert(request);
            } else {
                throw new Exception(mmsTimedOutException());
            }
        } catch (final Exception exception) {
            cleanup();
            setState(FAILED);
            fireStatusChanged();
            VNXLog.error("trying error with the request:"+request);
            VNXLog.error(exception);
            throw new CallException(exception);
        }
    }

    private synchronized void alert(final SipServletRequest request) throws CallException 
    {
        VNXLog.debug("..send ringing...");
        assertState(TRYING);
        final SipServletResponse ringing = request.createResponse(SipServletResponse.SC_RINGING);
        try {
            ringing.send();
            setState(RINGING);
            fireStatusChanged();
        } catch (final Exception exception) {
            cleanup();
            setState(FAILED);
            fireStatusChanged();
            VNXLog.error(exception);
            throw new CallException(exception);
        }
    }

    @Override
    public synchronized void answer() throws CallException 
    {
        VNXLog.debug("...the terminal...");
        assertState(RINGING);
        try {
            // Establish the media path way.
            relayOutboundConnection = session.createConnection(relayEndpoint);
            relayOutboundConnection.addObserver(this);
            relayOutboundConnection.connect(ConnectionMode.SendRecv);
            block(6, RINGING);
            // Send the response back to the caller.
            final byte[] answer = patchMedia(server.getExternalAddress(),
                    userAgentConnection.getLocalDescriptor().toString().getBytes());
            final SipServletResponse ok = initialInvite.createResponse(SipServletResponse.SC_OK);
            ok.setContent(answer, "application/sdp");
            ok.send();
        } catch (final Exception exception) {
            fail(SipServletResponse.SC_SERVER_INTERNAL_ERROR);
            fireStatusChanged();
            VNXLog.error(exception);
            throw new CallException(exception);
        }
    }

    public synchronized void busy() 
    {
        VNXLog.debug("...the terminal...");
        assertState(QUEUED);
        cleanup();
        setState(BUSY);
        fireStatusChanged();
    }

    public synchronized void bye(final SipServletRequest request) throws IOException 
    {
        VNXLog.debug("...the terminal...");
        //
        VNXLog.info("received a bye request :"+SIPUtil.dumpSIPMsgHdr(request));
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(QUEUED);
        possibleStates.add(RINGING);
        possibleStates.add(IN_PROGRESS);
        assertState(possibleStates);
        final SipServletResponse ok = request.createResponse(SipServletResponse.SC_OK);
        try 
        {
            ok.send();
            if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(ok,VAppCfg.SIP_MSG_OUTGOING);
            if (remoteConference != null) {
                remoteConference.removeParticipant(this);
            }
        }
        catch(Exception ex)        
        {
            ExLog.exception(LOGGER, ex);
        }
        finally 
        {
            VNXLog.debug("cleanup the terminal :"+this);
            cleanup();
            setState(COMPLETED);
            dateEnded = DateTime.now();
            fireStatusChanged();
        }
    }

    @Override
    public synchronized void cancel() throws CallException 
    {
        VNXLog.debug("...the terminal...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(QUEUED);
        possibleStates.add(RINGING);
        assertState(possibleStates);
        if (Direction.OUTBOUND_DIAL == getDirection()) {
            final SipServletRequest cancel = initialInvite.createCancel();
            try {
                cancel.send();
                if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(cancel,VAppCfg.SIP_MSG_OUTGOING);
                cleanup();
                setState(CANCELLED);
                fireStatusChanged();
            } catch (final IOException exception) {
                cleanup();
                setState(FAILED);
                fireStatusChanged();
                throw new CallException(exception);
            }
        }
    }

    public synchronized void cancel(final SipServletRequest request) throws IOException 
    {
        VNXLog.debug("...the terminal...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(QUEUED);
        possibleStates.add(RINGING);
        possibleStates.add(TRYING);
        assertState(possibleStates);
        final SipServletResponse ok = request.createResponse(SipServletResponse.SC_OK);
        try {
            ok.send();
            if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(ok,VAppCfg.SIP_MSG_OUTGOING);
        } finally {
            cleanup();
            setState(CANCELLED);
            fireStatusChanged();
        }
    }

    private void cleanup() {
        server.destroyMediaSession(session);
        initialInvite.getSession().invalidate();
    }

    @Override
    public synchronized void dial() throws CallException {
        assertState(QUEUED);
        // Try to negotiate media with a packet relay end point.
        try {
            relayEndpoint = session.getPacketRelayEndpoint();
            userAgentConnection = session.createConnection(relayEndpoint);
            userAgentConnection.addObserver(this);
            userAgentConnection.connect(ConnectionMode.SendRecv,initialInvite);
            block(1);
            VNXLog.debug("...");
            if (!MgcpConnection.HALF_OPEN.equals(userAgentConnection.getState())) {
                throw new Exception(mmsTimedOutException());
            }
            final byte[] offer = patchMedia(server.getExternalAddress(), 
                    userAgentConnection.getLocalDescriptor().toString().getBytes());
            initialInvite.setContent(offer, "application/sdp");
            initialInvite.send();
        } catch (final Exception exception) 
        {
            
            cleanup();
            setState(FAILED);
            fireStatusChanged();
            final StringBuilder buffer = new StringBuilder();
            buffer.append("There was an error while dialing out from ");
            buffer.append(initialInvite.getFrom().toString()).append(" to ");
            buffer.append(initialInvite.getTo().toString());
            throw new CallException(exception);
        }
    }

    public synchronized void established() {
        VNXLog.debug("ACK received");
    }

    public synchronized void established(final SipServletResponse successResponse) throws CallException, IOException 
    {
        VNXLog.debug("...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(QUEUED);
        possibleStates.add(RINGING);
        assertState(possibleStates);
        byte[] answer = successResponse.getRawContent();
        try {
            if (answer != null) 
            {
                //update status
                
                //update database
                DBMng.getInstance().updateBrcastObjAnswer(BrcastObjState.ANSWER,
                        getBroadcastID(),BroadcastObj.NORMAL_CALL_ANSWERING);
                
                answer = patchMedia(successResponse.getInitialRemoteAddr(), successResponse.getRawContent());
                final ConnectionDescriptor remoteDescriptor = new ConnectionDescriptor(new String(answer));
                userAgentConnection.modify(remoteDescriptor);
            } else {
                terminate();
                setState(FAILED);
                fireStatusChanged();
                throw new CallException("The remote client did not send an SDP answer with their 200 OK response.");
            }
        } catch (final SdpException exception) {
            terminate();
            setState(FAILED);
            fireStatusChanged();
            throw new CallException(exception);
        }
        
        final SipServletRequest ack = successResponse.createAck();
        ack.send();
        if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(ack,VAppCfg.SIP_MSG_OUTGOING);
        
    }

    public synchronized void failed() 
    {
        VNXLog.debug("...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(QUEUED);
        possibleStates.add(RINGING);
        possibleStates.add(IN_PROGRESS);
        assertState(possibleStates);
        final State currentState = getState();
        if (QUEUED.equals(currentState) || RINGING.equals(currentState)) {
            cleanup();
        } else if (IN_PROGRESS.equals(currentState)) {
            terminate();
        }
        setState(FAILED);
        fireStatusChanged();
    }

    private void fail(int code) {
        final SipServletResponse fail = initialInvite.createResponse(code);
        try {
            fail.send();
        } catch (final IOException exception) {
            VNXLog.error(exception);
        }
        cleanup();
        setState(FAILED);
    }

    private void fireStatusChanged() {
        VNXLog.debug("...");
        for (final CallObserver observer : observers) {
            observer.onStatusChanged(this);
        }
    }

    @Override
    public DateTime getDateCreated() {
        return dateCreated;
    }

    @Override
    public DateTime getDateStarted() {
        return dateStarted;
    }

    @Override
    public DateTime getDatedEnded() {
        return dateEnded;
    }

    @Override
    public String getDigits() {
        return digits;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String getForwardedFrom() {
        return null;
    }

    @Override
    public Sid getSid() {
        return sid;
    }

    @Override
    public String getOriginator() {
        final SipURI from = (SipURI) initialInvite.getFrom().getURI();
        return from.getUser();
    }

    @Override
    public String getOriginatorName() {
        return initialInvite.getFrom().getDisplayName();
    }

    @Override
    public String getRecipient() {
        final SipURI to = (SipURI) initialInvite.getTo().getURI();
        return to.getUser();
    }

    @Override
    public Status getStatus() {
        return Status.getValueOf(getState().getName());
    }
     public State getIVREndPointState() 
     {
        return ivrEndpoint==null?null:ivrEndpoint.getState();
    }
     public State getIVREndPointOldState() 
     {
         return ivrEndpoint==null?null:ivrEndpoint.getOldState();
    }

    @Override
    public synchronized void hangup() 
    {
        VNXLog.debug("...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(COMPLETED);
        possibleStates.add(IN_PROGRESS);
        assertState(possibleStates);
        if (remoteConference != null) {
            remoteConference.removeParticipant(this);
        }
        MgcpCallManager.listCurrentOutCalls.remove(((MgcpCallTerminal)this).getSIPCallID());
        terminate();
        dateEnded = DateTime.now();
        setState(COMPLETED);
        fireStatusChanged();
    }
    public synchronized void hangupFromBye() 
    {
        VNXLog.debug("...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(COMPLETED);
        possibleStates.add(IN_PROGRESS);
        assertState(possibleStates);
        if (remoteConference != null) {
            remoteConference.removeParticipant(this);
        }
        cleanup();
        dateEnded = DateTime.now();
        setState(COMPLETED);
        fireStatusChanged();
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    public synchronized void join(final MgcpConference conference) {
        assertState(IN_PROGRESS);
        remoteEndpoint = conference.getConferenceEndpoint();
        remoteOutboundConnection = session.createConnection(remoteEndpoint);
        remoteOutboundConnection.addObserver(this);
        remoteOutboundConnection.connect(ConnectionMode.Confrnce);
        try {
            block(3);
            //Issue 138: http://code.google.com/p/vnxivr/issues/detail?id=138
            if (MgcpConnection.OPEN.equals(remoteInboundConnection.getState())
                    && MgcpConnection.OPEN.equals(remoteOutboundConnection.getState())) {
                remoteConference = conference;
            } else {
                throw new Exception(mmsTimedOutException());
            }
        } catch (final Exception ignored) {
            leave(conference);
        }
    }

    public synchronized void leave(final MgcpConference conference) {
        assertState(IN_PROGRESS);
        if (remoteOutboundConnection != null) {
            session.destroyConnection(remoteOutboundConnection);
            try {
                block(2);
                //Issue 138: http://code.google.com/p/vnxivr/issues/detail?id=138
                if (!(MgcpConnection.DISCONNECTED.equals(relayInboundConnection.getState())
                        || MgcpConnection.FAILED.equals(relayInboundConnection.getState()))
                        && !(MgcpConnection.DISCONNECTED.equals(remoteOutboundConnection.getState())
                        || MgcpConnection.FAILED.equals(remoteOutboundConnection.getState()))) {
                    throw new Exception(mmsTimedOutException());
                }
            } catch (final Exception exception) {
                VNXLog.error(exception);
            }
            remoteOutboundConnection.removeObserver(this);
            remoteOutboundConnection = null;
            if (remoteInboundConnection != null) {
                remoteInboundConnection.removeObserver(this);
                remoteInboundConnection = null;
                remoteEndpoint = null;
            }
            remoteConference = null;
        }
    }

    @Override
    public synchronized void mute() {
        relayOutboundConnection.modify(ConnectionMode.RecvOnly);
        try {
            block(1);
        } catch (final InterruptedException ignored) {
            unmute();
        }
        muted = true;
    }

    private byte[] patchMedia(final String realIp, final byte[] data)
            throws UnknownHostException, SdpException 
    {
        
        final String text = new String(data);
        final SessionDescription sdp = SdpFactory.getInstance().createSessionDescription(text);
        final Connection connection = sdp.getConnection();
        if (Connection.IN.equals(connection.getNetworkType())
                && Connection.IP4.equals(connection.getAddressType())) {
            final InetAddress address = InetAddress.getByName(connection.getAddress());
            final String ip = address.getHostAddress();
            if (!IPUtils.isRoutableAddress(ip)) {
                connection.setAddress(realIp);
            }
        }
        VNXLog.debug("realIp:"+realIp + " with sdp:"+sdp);
        return sdp.toString().getBytes();
    }

    @Override
    public synchronized void play(URI anounuri, final int iterations) throws CallException {

        try {
            final List<URI> announcements = new ArrayList<URI>();
            announcements.add(anounuri);
            assertState(IN_PROGRESS);
            ivrEndpoint.play(announcements, iterations);
            currentAnnouncements=announcements;
//            wait();
        } catch (Exception ignored) 
        {
            stopMedia();
            ExLog.exception(LOGGER, ignored);
        }
    }

    @Override
    public synchronized void play(final List<URI> announcements, final int iterations) throws CallException {
        assertState(IN_PROGRESS);
        ivrEndpoint.play(announcements, iterations);
        currentAnnouncements=announcements;
        try {
            wait();
        } catch (final InterruptedException ignored) {
            stopMedia();
        }
    }

    @Override
    public synchronized void playAndCollect(final List<URI> prompts, final int maxNumberOfDigits, final int minNumberOfDigits,
            final long firstDigitTimer, final long interDigitTimer, final String endInputKey) throws CallException {
        assertState(IN_PROGRESS);
        ivrEndpoint.playCollect(prompts, maxNumberOfDigits, minNumberOfDigits, firstDigitTimer, interDigitTimer, endInputKey);
        try {
            wait();
        } catch (final InterruptedException ignored) {
            stopMedia();
        }
    }
    
    public synchronized void recordAfterBeep(long recordingLength)
    {
        try {
            //location for storing recorded files
            URI recordfile = new URI(VAppCfg.accessMethod+VAppCfg.recordDir+getOriginator()
                    +"_"+getRecipient()+"_"+VDate.now("HHmmss_yyyyMMdd")+".wav");
            URI beepuri = new URI(VAppCfg.accessMethod+VAppCfg.audioDir+VAppCfg.beepFile);
            final List<URI> beep = new ArrayList<URI>();
            beep.add(beepuri);
//            assertState(IN_PROGRESS);
//            ivrEndpoint.playRecord(beep, recordfile, 20, recordingLength/1000, null);
//            ivrEndpoint.playRecord(null, recordfile, 20, recordingLength/1000, null);
            ivrEndpoint.playRecord(beep, recordfile, TimeUtils.MINUTE_IN_MILLIS * 30
                    , recordingLength/1000, null);
        } catch (Exception ignored) 
        {
            stopMedia();
            ExLog.exception(LOGGER, ignored);
        }
    }
    
    public synchronized void recordAfterBeep(long recordingLength,String filenamepath)
    {
        try {
            //location for storing recorded files
            URI recordfile = new URI(filenamepath);
            URI beepuri = new URI("file:////home/Audio/SMSTalk/vie_beep.wav");
            final List<URI> beep = new ArrayList<URI>();
            beep.add(beepuri);
            ivrEndpoint.playRecord(beep, recordfile, TimeUtils.MINUTE_IN_MILLIS * 30
                    , recordingLength/1000, null);
        } catch (Exception ignored) 
        {
            stopMedia();
            ExLog.exception(LOGGER, ignored);
        }
    }

    
    
    @Override
    public synchronized void playAndRecord(final List<URI> prompts, final URI recordId, final long postSpeechTimer,
            final long recordingLength, final String patterns) throws CallException {
        assertState(IN_PROGRESS);
        ivrEndpoint.playRecord(prompts, recordId, postSpeechTimer, recordingLength, patterns);
        try {
            wait();
        } catch (final InterruptedException ignored) {
            stopMedia();
        }
    }

    @Override
    public synchronized void reject() 
    {
        VNXLog.debug("...");
        assertState(RINGING);
        final SipServletResponse busy = initialInvite.createResponse(SipServletResponse.SC_BUSY_HERE);
        try {
            busy.send();
        } catch (final IOException exception) {
            cleanup();
            setState(FAILED);
            fireStatusChanged();
            VNXLog.error(exception);
        }
    }

    public synchronized void ringing() 
    {
        VNXLog.debug("...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(QUEUED);
        possibleStates.add(RINGING);
        assertState(possibleStates);
        final State state = getState();
        if (QUEUED.equals(state)) {
            setState(RINGING);
            fireStatusChanged();
        }
    }

    @Override
    public synchronized void removeObserver(CallObserver observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void setExpires(final int minutes) 
    {
        initialInvite.getApplicationSession().setExpires(minutes);
    }

    @Override
    public synchronized void stopMedia() 
    {
        try 
        {
            ivrEndpoint.stop();
        }
       catch(IllegalStateException illegal)
       {
            VNXLog.warn(illegal.getMessage());
       }
        catch (final Exception ignored) {
            VNXLog.error(ignored);
        }
    }

    private void terminate() 
    {
        final SipSession sipSession = initialInvite.getSession();
        final SipServletRequest bye = sipSession.createRequest("BYE");
        try {
            MgcpCallManager.listCurrentOutCalls.remove(((MgcpCallTerminal)this).getSIPCallID()) ;
            //update database
            DBMng.getInstance().updateBrcastObjComplete(BrcastObjState.COMPLETE,
                        getBroadcastID(),BroadcastObj.NORMAL_CALL_CLEARING);
            bye.addHeader("Reason", "Q.850;cause=16;text=\"Normal call clearing\"");
            bye.send();
            if(DBCfg.dbUsed==1) DBMng.getInstance().insrSIPMsgs(bye,VAppCfg.SIP_MSG_OUTGOING);
        } catch (final IOException exception) {
            VNXLog.error(exception);
        }
        cleanup();
    }
    

    @Override
    public synchronized void unmute() {
        relayOutboundConnection.modify(ConnectionMode.SendRecv);
        try {
            block(1);
        } catch (final InterruptedException ignored) {
        }
        muted = false;
    }

    public synchronized void updateInitialInvite(final SipServletRequest initialInvite) {
        this.initialInvite = initialInvite;
    }

    @Override
    public synchronized void operationCompleted(final MgcpIvrEndpoint endpoint) 
    {
        VNXLog.debug("...");
        digits = endpoint.getDigits();
        fireStatusChanged();
        notify();
    }
    @Override
    public synchronized void operationCompleted(final MgcpIvrEndpoint endpoint,State oldstate) 
    {
        VNXLog.debug("...");
        digits = endpoint.getDigits();
        fireStatusChanged();
        notify();
    }

    @Override
    public synchronized void operationFailed(final MgcpIvrEndpoint endpoint) 
    {
        VNXLog.debug("...");
        fireStatusChanged();
        notify();
    }

    @Override
    public synchronized void halfOpen(final MgcpConnection connection) {
        VNXLog.debug("halfOpen notification for connection: " + connection + ", endpoint: " + connection.getEndpoint()
                + ", state: " + connection.getState().getName());
        final List<State> impossibleStates = new ArrayList<State>();
        impossibleStates.add(COMPLETED);
        impossibleStates.add(FAILED);
        assertStateNot(impossibleStates);

        if (connection == relayOutboundConnection) {
            cnfEndpoint = session.getConferenceEndpoint();
            relayInboundConnection = session.createConnection(cnfEndpoint, connection.getLocalDescriptor());
            relayInboundConnection.addObserver(this);
            relayInboundConnection.connect(ConnectionMode.Confrnce);
        } else if (connection == ivrOutboundConnection) {
            ivrInboundConnection = session.createConnection(cnfEndpoint, connection.getLocalDescriptor());
            ivrInboundConnection.addObserver(this);
            ivrInboundConnection.connect(ConnectionMode.Confrnce);
        } else if (connection == remoteOutboundConnection) {
            remoteInboundConnection = session.createConnection(cnfEndpoint, connection.getLocalDescriptor());
            remoteInboundConnection.addObserver(this);
            remoteInboundConnection.connect(ConnectionMode.Confrnce);
        } else if (connection == userAgentConnection) {
            notify();
        }
    }

    @Override
    public synchronized void open(final MgcpConnection connection) {
        VNXLog.debug("open notification for connection: " + connection + ", endpoint: " + connection.getEndpoint()
                + ", state: " + connection.getState().getName());
        final List<State> impossibleStates = new ArrayList<State>();
        impossibleStates.add(COMPLETED);
        impossibleStates.add(FAILED);
        assertStateNot(impossibleStates);

        if (connection == relayInboundConnection) {
            relayOutboundConnection.modify(connection.getLocalDescriptor());
        }
        if (connection == relayOutboundConnection) {
            ivrEndpoint = session.getIvrEndpoint();
            ivrEndpoint.addObserver(this);
            ivrOutboundConnection = session.createConnection(ivrEndpoint);
            ivrOutboundConnection.addObserver(this);
            ivrOutboundConnection.connect(ConnectionMode.SendRecv);
        } else if (connection == ivrInboundConnection) {
            ivrOutboundConnection.modify(connection.getLocalDescriptor());
        }
        if (connection == ivrOutboundConnection) {
            final List<State> possibleStates = new ArrayList<State>();
            possibleStates.add(QUEUED);
            possibleStates.add(RINGING);
            possibleStates.add(TRYING);
            assertState(possibleStates);
            setExpires(0);
            dateStarted = DateTime.now();
            setState(IN_PROGRESS);
            fireStatusChanged();
            if (Direction.INBOUND == getDirection()) {
                notify();
            }
        } else if (connection == remoteInboundConnection) {
            remoteOutboundConnection.modify(connection.getLocalDescriptor());
        } else if (connection == remoteOutboundConnection) {
            notify();
        } else if (connection == userAgentConnection) {
            if (direction == Direction.INBOUND) {
                notify();
            } else if (direction == Direction.OUTBOUND_DIAL) {
                relayOutboundConnection = session.createConnection(relayEndpoint);
                relayOutboundConnection.addObserver(this);
                relayOutboundConnection.connect(ConnectionMode.SendRecv);
            }
        }
    }

    @Override
    public synchronized void disconnected(final MgcpConnection connection) {
        if (connection == remoteOutboundConnection) {
            if (remoteInboundConnection != null) {
                session.destroyConnection(remoteInboundConnection);
            } else {
                notify();
            }
        } else if (connection == remoteInboundConnection) {
            notify();
        }
    }

    @Override
    public synchronized void failed(final MgcpConnection connection) {
        failed();
        notify();
    }

    @Override
    public synchronized void modified(final MgcpConnection connection) {
        if (connection == relayOutboundConnection) {
            notify();
        }
    }

    /**
     * Retrieves the name of a terminal.
     * @return name of the terminal
     */
    public String getName() {
        VNXLog.error("not support yet");
        return null;
    }

    /**
     * Starts waiting for incoming connections. This method is expected to run
     * asynchronously.
     * <p>
     * This method is called if the {@link org.jvoicexml.CallManager} starts up.
     * </p>
     * @throws IOException
     *         error waiting for connections.
     */
    public void waitForConnections() throws IOException {
        VNXLog.error("not support yet");
    }

    /**
     * Stops waiting for incoming connections.
     */
    public void stopWaiting() {
        VNXLog.error("not support yet");
    }

    /**
     * Retrieves the terminal name.
     *
     * @return name of the terminal.
     */
    public String getTerminalName() {
        return getOriginatorName();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final TelephonyListener callControlListener) {
        synchronized (callControlListeners) {
            callControlListeners.add(callControlListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final TelephonyListener callControlListener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(callControlListener);
        }
    }

    /**
     * Disconnects the connection.
     * @since 0.7
     */
    public synchronized void disconnect() 
    {
        VNXLog.info("terminating the call "+this);
        terminate();
    }

    public void setSIPCallID(String callid) 
    {
        
        callID = callid;
    }

    public String getSIPCallID() 
    {
        return initialInvite!=null?initialInvite.getCallId():callID;
    }
    
    public MgcpIvrEndpoint getIVREndpoint() {
        return ivrEndpoint;
    }
    public List<URI> getCurrentAnnouncements()
    {
        return currentAnnouncements;
    }
    public int getBroadcastID()
    {
        return brcastObj==null?-1:brcastObj.broadcast_id;
    }
    public String getBroadcastVXMLFile()
    {
            return brcastObj==null?"":brcastObj.vxml_uri;
    }
    public String toString()
    {
        return " terminal:callID:" +callID+ " status:"+getStatus() + " broadcast_id:"+getBroadcastID()
                + " ivrEndpoint:"+ivrEndpoint + " sid:"+sid + " currentAnnouncements:"+currentAnnouncements;
    }
    public SipServletMessage getInitInvite()
    {
        return initialInvite;
    }
}
