/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/KeyedResourcePool.java $
 * Version: $LastChangedRevision: 330 $
 * Date:    $Date: 2007-06-21 09:15:10 +0200 (Do, 21 Jun 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jtapi;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.telephony.Address;
import javax.telephony.CallEvent;
import javax.telephony.CallListener;
import javax.telephony.CallObserver;
import javax.telephony.Connection;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.Event;
import javax.telephony.InvalidStateException;
import javax.telephony.MetaEvent;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.events.CallActiveEv;
import javax.telephony.events.CallEv;
import javax.telephony.events.CallInvalidEv;
import javax.telephony.events.ConnAlertingEv;
import javax.telephony.events.ConnConnectedEv;
import javax.telephony.events.ConnCreatedEv;
import javax.telephony.events.ConnDisconnectedEv;
import javax.telephony.events.ConnFailedEv;
import javax.telephony.events.ConnInProgressEv;
import javax.telephony.events.ConnUnknownEv;
import javax.telephony.events.TermConnActiveEv;
import javax.telephony.events.TermConnCreatedEv;
import javax.telephony.events.TermConnDroppedEv;
import javax.telephony.events.TermConnPassiveEv;
import javax.telephony.events.TermConnRingingEv;
import javax.telephony.events.TermConnUnknownEv;
import javax.telephony.media.MediaResourceException;
import javax.telephony.media.PlayerEvent;
import javax.telephony.media.PlayerListener;

import net.sourceforge.gjtapi.media.GenericMediaService;

import org.jvoicexml.CallControl;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CallControlListener;
import org.jvoicexml.implementation.ObservableCallControl;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * JTAPI based implementation of a {@link CallControl}.
 * 
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle
 * 
 * @version $Revision: 206 $
 * 
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 * 
 * @since 0.6
 */
public final class JtapiCallControl implements CallControl,
        ObservableCallControl, ConnectionListener, CallObserver, PlayerListener {
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JtapiCallControl.class);

    /** Reference to the call manager. */
    private final CallManager callManager;

    /** Listener to this call control. */
    private final Collection<CallControlListener> callControlListeners;

    private final GenericMediaService mediaService;

    private final String terminalName;

    private Connection connection = null;

    /** A related JVoiceXML session. */
    private Session session;

    /**
     * Constructs a new object.
     * 
     * @param cm
     *            the call manager.
     * @param service
     *            GenericMediaService
     */
    public JtapiCallControl(final CallManager cm,
            final GenericMediaService service) {
        callManager = cm;
        // listener to Jvxml
        callControlListeners = new ArrayList<CallControlListener>();

        // Media service object
        mediaService = service;

        // Adds a listener to a Call object when this Address object first
        // becomes part of that Call.
        final Terminal terminal = mediaService.getTerminal();
        final Address[] addrs = terminal.getAddresses();
        terminalName = terminal.getName();

        final CallListener[] listener = terminal.getCallListeners();
        try {
            // validate if the terminal already has a listener.
            if (listener == null) {
                for (int i = 0; i < addrs.length; i++) {
                    // Search the address that corresponds to this terminal.
                    if (terminalName.equals(addrs[i].getName())) {
                        addrs[i].addCallListener(this); // add a call Listener
                        addrs[i].addCallObserver(this); // add a call Observer
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("added a listener to terminal "
                                    + terminalName);
                        }
                    }
                }
            }
        } catch (MethodNotSupportedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ResourceUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final CallControlListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.add(listener);
        }
    }

    /**
     * Retrieves the terminal name.
     * 
     * @return name of the terminal.
     */
    public String getTerminalName() {
        return terminalName;
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final CallControlListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void play(final URI sourceUri) {
        firePlayEvent();
        PlayerEvent event = null;
        try {
            event = mediaService.play(sourceUri.toString(), 0, null, null);
        } catch (MediaResourceException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() {
        mediaService.stop();
    }

    /**
     * {@inheritDoc}
     */
    public void record(final URI destinationUri) {

        fireRecordEvent(); // may be after record method!!!

        try {
            mediaService.record(destinationUri.toString(), null, null);
        } catch (MediaResourceException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecord() {
        mediaService.stop();
    }

    /**
     * {@inheritDoc}
     */
    public void tranfer(final URI destinationPhoneUri) {
    }

    /**
     * {@inheritDoc}
     */
    public void tranfer(final URI destinationPhoneUri, final Map props) {
    }

    /**
     * Inform the {@link CallControlListener} about an answered event.
     */
    protected void fireAnswerEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.answered();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a play started event.
     */
    protected void firePlayEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.playStarted();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a play stopped event.
     */
    protected void fireplayStoppedEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.playStopped();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a record started event.
     */
    protected void fireRecordEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.recordStarted();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a hangup event.
     */
    protected void firehangedUpEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.hangedup();
            }
        }
    }

    /**
     * 
     * @param connectionEvent
     *            ConnectionEvent
     */
    public void connectionAlerting(final ConnectionEvent connectionEvent) {
        System.err.println("5.3.3: Alerting Connection event with cause: "
                + this.causeToString(connectionEvent.getCause()));
    }

    /**
     * 
     * @param connectionEvent
     *            ConnectionEvent
     */
    public void connectionConnected(final ConnectionEvent connectionEvent) {
        if (LOGGER.isDebugEnabled()) {
            final int cause = connectionEvent.getCause();
            LOGGER.debug("connection connected with cause " + cause);
        }

        if (LOGGER.isInfoEnabled()) {
            final CallControlCall call = (CallControlCall) connectionEvent
                    .getCall();
            LOGGER.info("call connected from "
                    + call.getCallingAddress().getName() + " to "
                    + call.getCalledAddress().getName());
        }
        fireAnswerEvent();

        // establishes a connection to JVoiceXML
        try {
            session = callManager.createSession(this);
        } catch (ErrorEvent e) {
            LOGGER.error("error creating a session", e);
        }
    }

    /**
     * 
     * @param connectionEvent
     *            ConnectionEvent
     */
    public void connectionCreated(final ConnectionEvent connectionEvent) {
        System.err.println("5.3.1: Connection Created event with cause: "
                + this.causeToString(connectionEvent.getCause()));
    }

    /**
     * 
     * @param connectionEvent
     *            ConnectionEvent
     */
    public void connectionDisconnected(
            javax.telephony.ConnectionEvent connectionEvent) {
        if (LOGGER.isDebugEnabled()) {
            final int cause = connectionEvent.getCause();
            LOGGER.debug("Connection disconnected with cause "
                    + causeToString(cause));
        }
        /**
         * @todo doen't entry when HangUp- fix this problem
         */
        this.stopPlay();

        firehangedUpEvent();
        try {
            if (connection != null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("disconnecting the connection");
                }
                connection.disconnect();
            }
        } catch (InvalidStateException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (MethodNotSupportedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ResourceUnavailableException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (PrivilegeViolationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        if (session != null) {
            session.close();
            session = null;
        }
    }

    /**
     * 
     * @param connectionEvent
     *            ConnectionEvent
     */
    public void connectionFailed(ConnectionEvent connectionEvent) {
        final int cause = connectionEvent.getCause();
        LOGGER.error("connection failed event with cause "
                + causeToString(cause));
    }

    /**
     * call in progress. In this state we have to answer the call and inform
     * jvxml (may be!)
     * 
     * @param connectionEvent
     *            ConnectionEvent
     */
    public void connectionInProgress(final ConnectionEvent connectionEvent) {
        if (LOGGER.isDebugEnabled()) {
            final int cause = connectionEvent.getCause();
            LOGGER.debug("Connection in progress with cause "
                    + causeToString(cause));
        }

        TerminalConnection[] tc = mediaService.getTerminal()
                .getTerminalConnections();

        if (tc != null && tc.length > 0) {
            TerminalConnection termConn = tc[0];
            connection = termConn.getConnection();
            try {
                // terminal Answer
                termConn.answer();
                // voiceXml aplication initialization
                // _appVxml = initAppVXML();
            } catch (InvalidStateException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (MethodNotSupportedException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (ResourceUnavailableException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (PrivilegeViolationException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        } else {
            LOGGER.error("failed to find any TerminalConnections");
        }

    }

    /**
     * {@inheritDoc}
     */
    public void connectionUnknown(final ConnectionEvent connectionEvent) {
        final int cause = connectionEvent.getCause();
        LOGGER.error("connection unknown event with cause "
                + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void callActive(final CallEvent callEvent) {
        final int cause = callEvent.getCause();
        LOGGER.error("active call event with cause " + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void callInvalid(final CallEvent callEvent) {
        final int cause = callEvent.getCause();
        LOGGER.error("invalid call event with cause " + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void callEventTransmissionEnded(final CallEvent callEvent) {
        final int cause = callEvent.getCause();
        LOGGER.error("event transmission ended event with cause "
                + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressStarted(MetaEvent metaEvent) {
        final int cause = metaEvent.getCause();
        LOGGER.error("single call progress started event with cause "
                + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressEnded(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotStarted(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotEnded(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeStarted(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeEnded(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferStarted(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferEnded(MetaEvent metaEvent) {
    }

    /**
     * Convert the event cause string to a cause. Creation date: (2000-05-01
     * 9:58:39)
     * 
     * @author: Richard Deadman
     * @return English description of the cause
     * @param cause
     *            The Event cause id.
     */
    public String causeToString(final int cause) {
        switch (cause) {
        case Event.CAUSE_CALL_CANCELLED:
            return "Call canceled";
        case Event.CAUSE_DEST_NOT_OBTAINABLE:
            return "Destination not obtainable";
        case Event.CAUSE_INCOMPATIBLE_DESTINATION:
            return "Incompatible destination";
        case Event.CAUSE_LOCKOUT:
            return "Lockout";
        case Event.CAUSE_NETWORK_CONGESTION:
            return "Network congestion";
        case Event.CAUSE_NETWORK_NOT_OBTAINABLE:
            return "Network not obtainable";
        case Event.CAUSE_NEW_CALL:
            return "New call";
        case Event.CAUSE_NORMAL:
            return "Normal";
        case Event.CAUSE_RESOURCES_NOT_AVAILABLE:
            return "Resource not available";
        case Event.CAUSE_SNAPSHOT:
            return "Snapshot";
        case Event.CAUSE_UNKNOWN:
            return "Unknown";
        default:
            return "Cause mapping error: " + cause;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void callChangedEvent(final CallEv[] eventList) {
        String event = null;
        int id = eventList[0].getID();
        switch (id) {
        case CallActiveEv.ID: {
            event = "call active";
            break;
        }
        case CallInvalidEv.ID: {
            event = "call invalid";
            break;
        }
        case ConnAlertingEv.ID: {
            event = "Connection alerting";
            break;
        }
        case ConnConnectedEv.ID: {
            event = "Connection connected";
            break;
        }
        case ConnCreatedEv.ID: {
            event = "Connection created";
            break;
        }
        case ConnDisconnectedEv.ID: {
            event = "Connection disconnected";
            break;
        }
        case ConnFailedEv.ID: {
            event = "Connection failed";
            break;
        }
        case ConnInProgressEv.ID: {
            event = "Connection in progress";
            break;
        }
        case ConnUnknownEv.ID: {
            event = "Connection unknown";
            break;
        }
        case TermConnActiveEv.ID: {
            event = "Terminal Connection active";
            break;
        }
        case TermConnCreatedEv.ID: {
            event = "Terminal Connection created";
            break;
        }
        case TermConnDroppedEv.ID: {
            event = "Terminal Connection dropped";
            break;
        }
        case TermConnPassiveEv.ID: {
            event = "Terminal Connection passive";
            break;
        }
        case TermConnRingingEv.ID: {
            event = "Terminal Connection ringing";
            break;
        }
        case TermConnUnknownEv.ID: {
            event = "Terminal Connection unknown";
            break;
        }
        default:
            event = "unknown: " + id;
        }
        System.err.println("Observer event: " + event);
    }

    /**
     * {@inheritDoc}
     */
    public void onSpeedChange(PlayerEvent playerEvent) {
        System.err.print("\n onSpeedChange: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void onVolumeChange(PlayerEvent playerEvent) {
        System.err.print("\n onVolumeChange: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void onPause(PlayerEvent playerEvent) {
        System.err.print("\n onPause: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void onResume(PlayerEvent playerEvent) {
        System.err.print("\n onResume: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jtapi";
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
    }

    /**
     * {@inheritDoc}
     */
    public void connect(RemoteClient client) throws IOException {
    }
}
