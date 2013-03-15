/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
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
package org.jvoicexml.systemtest.mobicents;

import com.vnxtele.util.VDate;
import javax.servlet.sip.Address;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.networkconnection.NetworkConnection;
import javax.media.mscontrol.networkconnection.SdpPortManager;
import javax.media.mscontrol.spi.Driver;
import javax.media.mscontrol.spi.DriverManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletContextEvent;
import javax.servlet.sip.SipServletListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import java.net.URI;

import org.mobicents.servlet.sip.message.SipFactoryImpl;


import com.vnxtele.util.VNXLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.sip.TimerService;
import javax.media.mscontrol.MediaEventListener;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.join.JoinEvent;
import javax.media.mscontrol.join.JoinEventListener;
import javax.media.mscontrol.join.Joinable.Direction;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mediagroup.Recorder;
import javax.media.mscontrol.networkconnection.SdpPortManagerEvent;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.TimerListener;
import org.apache.commons.configuration.ConfigurationException;
import org.util.SIPUtil;
import org.vnxtele.ivrgw.sip.utils.VIVRUtil;

import org.apache.commons.configuration.XMLConfiguration;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallTerminal;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpServer;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpServerManager;
import org.mobicents.servlet.sip.restcomm.media.api.Call;


/*
 * This example shows a simple User agent that can playback audio.
 * @author Vladimir Ralev
 *
 */
//@javax.servlet.sip.annotation.SipServlet
//@javax.servlet.sip.annotation.SipListener
public class BroadcastServletDemo extends SipServlet implements SipServletListener,TimerListener {

    private static final long serialVersionUID = 1L;
    private TimerService clock;
    private static final String MS_CONTROL_FACTORY = "MsControlFactory";
    public static final String PR_JNDI_NAME = "media/trunk/PacketRelay/$";
    // Property key for the Unique MGCP stack name for this application 
    public static final String MGCP_STACK_NAME = "mgcp.stack.name";
    // Property key for the IP address where CA MGCP Stack (SIP Servlet 
    // Container) is bound 
//    public static final String MGCP_STACK_IP = "mgcp.server.address"; 
    public static final String MGCP_STACK_IP = "mgcp.stack.ip";
    // Property key for the port where CA MGCP Stack is bound 
//    public static final String MGCP_STACK_PORT = "mgcp.local.port"; 
    public static final String MGCP_STACK_PORT = "mgcp.stack.port";
    // Property key for the IP address where MGW MGCP Stack (MMS) is bound 
//    public static final String MGCP_PEER_IP = "mgcp.bind.address"; 
    public static final String MGCP_PEER_IP = "mgcp.stack.peer.ip";
    // Property key for the port where MGW MGCP Stack is bound 
//    public static final String MGCP_PEER_PORT = "mgcp.server.port"; 
    public static final String MGCP_PEER_PORT = "mgcp.stack.peer.port";
    private final static String WELCOME_MSG = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf_welcome.wav";
    private final static String DTMF_0 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf0.wav";
    private final static String DTMF_1 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf1.wav";
    private final static String DTMF_2 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf2.wav";
    private final static String DTMF_3 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf3.wav";
    private final static String DTMF_4 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf4.wav";
    private final static String DTMF_5 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf5.wav";
    private final static String DTMF_6 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf6.wav";
    private final static String DTMF_7 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf7.wav";
    private final static String DTMF_8 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf8.wav";
    private final static String DTMF_9 = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/dtmf9.wav";
    private final static String STAR = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/star.wav";
    private final static String POUND = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/pound.wav";
    private final static String A = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/A.wav";
    private final static String B = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/B.wav";
    private final static String C = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/C.wav";
    private final static String D = "http://"
            + System.getProperty("jboss.bind.address", VAppCfg.httpServerBindAddress)
            + ":" + VAppCfg.httpServerBindPort + "/BroadcastDemo/audio/D.wav";
    private static MgcpServerManager servers;
    /**
     * In this case MGW and CA are on same local host
     */
    public static VAppCfg appCfg = null;
    public static SipFactory sipFactory = null;
    private MsControlFactory msCtrlfactory;
    public static int countSIPPHone = 0;
    public static int alreadyInit = 0;
    public static Timer tmrSendHrbt = null;
    public static SipFactoryImpl sipFactoryImpl;
    protected boolean isBye = false;

    public BroadcastServletDemo() {
        VNXLog.info("constructor  :" + this
                + " ProcessID:" + ManagementFactory.getRuntimeMXBean().getName()
                + " CurrentThreadID:" + Thread.currentThread().getId()
                + " " + Thread.currentThread().toString()
                + " " + Thread.currentThread()
                + " VAppCfg:" + VAppCfg.getInstance());

    }

    public static void setSipFact(SipFactoryImpl sipimpl) {
        sipFactoryImpl = sipimpl;
    }

    protected URI getPrompt() throws Exception {
        return URI.create(WELCOME_MSG);
    }
    
    

    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("init servletConfig:" + servletConfig);
        VNXLog.info("init servletConfig:" + servletConfig);
        super.init(servletConfig);
        sipFactory = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);
        clock = (TimerService)getServletContext().getAttribute(TIMER_SERVICE);
        VNXLog.info("init sipFactory:" + sipFactory + " clock:"+clock);
        try {
            VNXLog.info("****** the BroadcastServletDemo servlet  ********* ");
            properties = new Properties();
            
            //
            VNXLog.info("set servlet context :BroadcastServletDemo:" + this);
            
            //loading media configuration
            String path=VAppCfg.CATALINA_HOME+"conf/MediaServer.xml";
            VNXLog.info("loading configuration file located at " + path);
            // Load the vnxivr configuration.
            XMLConfiguration configuration = null;
            configuration = new XMLConfiguration(path);
            VNXLog.info("load media server configurations");
            servers = new MgcpServerManager();
            servers.configure(configuration.subset("media-server-manager"));
            servers.start();
        }
        catch (ConfigurationException e) 
        {
            VNXLog.error(e);
        }
        catch (Exception e) {
            VNXLog.error("couldn't start the underlying MGCP Stack");
            VNXLog.error(e);
        }

    }
    
   

    protected void doRegister(SipServletRequest req) throws ServletException, IOException {
        VNXLog.info("Received register request: " + req.getTo());
        int response = SipServletResponse.SC_OK;
        SipServletResponse resp = req.createResponse(response);
        HashMap<String, String> users = (HashMap<String, String>) getServletContext().getAttribute("registeredUsersMap");
        if (users == null) {
            users = new HashMap<String, String>();
        }
        getServletContext().setAttribute("registeredUsersMap", users);

        Address address = req.getAddressHeader(VAppCfg.CONTACT_HEADER);
        String fromURI = req.getFrom().getURI().toString();

        int expires = address.getExpires();
        if (expires < 0) {
            expires = req.getExpires();
        }
        if (expires == 0) {
            users.remove(fromURI);
            VNXLog.info("User " + fromURI + " unregistered");
        } else {
            resp.setAddressHeader(VAppCfg.CONTACT_HEADER, address);
            users.put(fromURI, address.getURI().toString());
            VNXLog.info("User " + fromURI
                    + " registered with an Expire time of " + expires);
        }

        resp.send();
    }

    @Override
    protected void doInfo(SipServletRequest request) throws ServletException,
            IOException {
        int responseCode = SipServletResponse.SC_OK;
        // Getting the message content

        String messageContent = new String((byte[]) request.getContent());
        VNXLog.info("got INFO request with following content "
                + messageContent);
        int signalIndex = messageContent.indexOf(VAppCfg.digitPattern);
        // Playing file only if the DTMF session has been started
        if (messageContent != null && messageContent.length() > 0
                && signalIndex != -1) {
            String signal = messageContent.substring(VAppCfg.digitPattern.length()).trim();
            signal = signal.substring(0, 1);
            VNXLog.info("Signal received " + signal);
            MediaGroup mediaGroup = (MediaGroup) request.getSession().getAttribute("MediaGroup");
            try {
//                playDTMF(mediaGroup.getPlayer(), signal);
                ;
            } catch (Exception e) {
                VNXLog.error(
                        "Problem playing the stream corresponding to the following DTMF "
                        + signal, e);
                responseCode = SipServletResponse.SC_SERVER_INTERNAL_ERROR;
            }
        } else {
            VNXLog.error("error when processing signal digits:signalIndex:" + signalIndex
                    + " messageContent:" + messageContent);

        }

        // sending response
        SipServletResponse response = request.createResponse(responseCode);
        response.send();
    }

    

    protected void doPing(SipServletRequest request) throws ServletException,
            IOException {
        try {
            // sending OK
            VNXLog.info("sending 200 ok to caller ping ");
            SipServletResponse ok = request.createResponse(SipServletResponse.SC_OK);
            ok.send();

        } catch (Exception e) {
            VNXLog.error(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doBye(SipServletRequest request) throws ServletException,
            IOException {

        VNXLog.info("incoming SIPBYE:"+SIPUtil.dumpSIPMsgHdr2(request)) ;
            final SipApplicationSession session = request.getApplicationSession();
            final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
            call.bye(request);

    }

    /**
     * {@inheritDoc}
     */
    protected void doResponse(SipServletResponse response)
            throws ServletException, IOException {
        String fromUri = response.getFrom().getURI().toString();
        String toUri = response.getTo().getURI().toString();
        VNXLog.info("<<<<<<<<<<<<<<<<<<<<<<<< Got a sip response:\n" + response
                + "\n remoteAddress:" + response.getInitialRemoteAddr() + ":" + response.getInitialRemotePort()
                + "\n localAddress:" + response.getLocalAddr() + ":" + response.getLocalPort()
                + "\n fromUri:" + fromUri + " toUri:" + toUri + " method:" + response.getMethod());
        if (response.getRequest().getMethod().equalsIgnoreCase("INVITE")) 
        {
            if (response.getStatus() == 200) 
            {
                try {
                    VNXLog.error("reponse 200 ok is not processed:" + response);
                    
                    
                    
                    
                } catch (Exception e) {
                    VNXLog.error(e);
                }
            } else if (response.getStatus() == 180) {
                VNXLog.info("reponse ok with RINGING");
            } else {
                VNXLog.error("reponse is not processed:" + response);
            }
        } else if (response.getRequest().getMethod().equalsIgnoreCase("INFO")) {
            VNXLog.info("reponse ok with SIP INFO dtmf");
        } else if (response.getRequest().getMethod().equalsIgnoreCase("RINGING")) {
            VNXLog.info("reponse ok with RINGING");
        } else {
            VNXLog.error("reponse is unknow processed:" + response);
        }
    }
    Properties properties = null;

    public void contextDestroyed(ServletContextEvent event) {
        Iterator<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasNext()) {
            Driver driver = drivers.next();
            DriverManager.deregisterDriver(driver);
        }
    }

    protected void doInvite1(SipServletRequest request) throws ServletException,
            IOException {
        isBye = false;
        VNXLog.info("<<<<<<<<<<< sipInvite comming:\n" + VIVRUtil.dumpSIPMsgHdr2(request));
        SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_RINGING);
        sipServletResponse.send();
        SipSession sipSession = request.getSession();
        try {
            MsControlFactory msControlFactory = (MsControlFactory) getServletContext().getAttribute(MS_CONTROL_FACTORY);
            // Create new media session and store in SipSession
            MediaSession mediaSession = (MediaSession) msControlFactory.createMediaSession();

            sipSession.setAttribute("MEDIA_SESSION", mediaSession);
            mediaSession.setAttribute("SIP_SESSION", sipSession);
            // Store INVITE so it can be responded to later
            sipSession.setAttribute("UNANSWERED_INVITE", request);

            // Create a new NetworkConnection and store in SipSession
            NetworkConnection conn = mediaSession.createNetworkConnection(NetworkConnection.BASIC);

            SdpPortManager sdpManag = conn.getSdpPortManager();

            NetworkConnectionListener ncListener = new NetworkConnectionListener();

            sdpManag.addListener(ncListener);

            byte[] sdpOffer = request.getRawContent();

            sdpManag.processSdpOffer(sdpOffer);

        } catch (MsControlException e) {
            VNXLog.error(e);
            request.createResponse(SipServletResponse.SC_SERVER_INTERNAL_ERROR).send();
        }

    }
    
    
    protected final void doInvite(final SipServletRequest request) throws ServletException, IOException {
        try 
        {
            VNXLog.info("incoming SIPInvite:"+SIPUtil.dumpSIPMsgHdr2(request)) ;
            // Create the call.
            final MgcpServer server = servers.getMediaServer();
            final MgcpCallTerminal call = new MgcpCallTerminal(server);
            call.setSIPCallID(request.getCallId());
            call.trying(request);
            call.answer();
            request.getApplicationSession().setAttribute("CALL", call);
        }
         catch(Exception ex)       
        {
            VNXLog.error(ex);
        }
                    
    }
    
    protected void doAck(SipServletRequest request) throws ServletException,
            IOException {
        VNXLog.debug("Received ACK for INVITE: " + VIVRUtil.dumpSIPMsgHdr2(request));
        try {
            
            final SipApplicationSession session = request.getApplicationSession();
            final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
            call.established();
            Thread.sleep(1000);
//            String recordfile = VAppCfg.recordingDir + VIVRUtil.getOriginator(request)
//                                    + "_" + VIVRUtil.getRecipient(request) + "_" + 
//                    VDate.now("HHmmss_yyyyMMdd") + ".wav";
//            call.recordAfterBeep(60000,recordfile);
            //waiting for a moment before simulating press dtmf key            
            Thread.sleep(5000);
            SipServletRequest info =request.getSession().createRequest("INFO");
            String sdp = "Signal=1\r\n" + "Duration=250\r\n";
            info.setContent(sdp, "application/dtmf-relay");
            VNXLog.debug("dump INFO request = " + info);
            info.send();
        } catch (Exception e) 
        {
            VNXLog.error(e);
        }
    }

    protected void doAck1(SipServletRequest request) throws ServletException,
            IOException {
        VNXLog.debug("Received ACK for INVITE: " + VIVRUtil.dumpSIPMsgHdr2(request));
        SipSession sipSession = request.getSession();
        MediaSession ms = (MediaSession) sipSession.getAttribute("MEDIA_SESSION");

        
        try {
            //start timer service
            final SipApplicationSession application = request.getApplicationSession();
				long expires = 30 * 1000;
				clock.createTimer(application, expires, false, "REGISTER");
				// Issue http://code.google.com/p/vnxivr/issues/detail?id=66
				expires += 1000 * 30;
				application.setExpires(TimeUtils.millisToMinutes(expires));
					final StringBuilder buffer = new StringBuilder();
					buffer.append("Successfully registered\n");
					buffer.append("for a duration of ").append(expires).append(" seconds.");
					VNXLog.debug(buffer.toString());
            ////influencing to MMS for recording 
            MediaGroup mg = ms.createMediaGroup(MediaGroup.PLAYER_RECORDER_SIGNALDETECTOR);
            mg.addListener(new MyJoinEventListener());
            NetworkConnection nc = (NetworkConnection) sipSession.getAttribute("NETWORK_CONNECTION");
            mg.joinInitiate(Direction.DUPLEX, nc, this);
            //waiting for a moment before simulating press dtmf key            
            Thread.sleep(5000);
            SipServletRequest info = sipSession.createRequest("INFO");
            String sdp = "Signal=1\r\n" + "Duration=250\r\n";
            info.setContent(sdp, "application/dtmf-relay");
            VNXLog.debug("dump INFO request = " + info);
            info.send();
        } catch (Exception e) {
            VNXLog.error(e);
            // Clean up media session
            terminate(sipSession, ms);
        }
    }
    
    
    

    protected void genSIPInfo(SipServletRequest request) throws ServletException,
            IOException {
        String fromUri = request.getFrom().getURI().toString();
        SipURI toUri = (SipURI) request.getTo().getURI();
        VNXLog.info("<<<<<<<<<<< sipInfo comming:\n" + "fromUri:" + fromUri
                + " toUri:" + toUri);
        VNXLog.info("Got Request: " + request.toString());
        VNXLog.info(request.getFrom().getURI().toString());
        B2buaHelper helper = request.getB2buaHelper();
        SipFactory sipFactory = (SipFactory) getServletContext().getAttribute(
                SIP_FACTORY);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> toHeaderSet = new ArrayList<String>();
        List<String> callid = new ArrayList<String>();
        callid.add(request.getCallId());
        headers.put(VAppCfg.VCalled_CallID, callid);
        headers.put("To", toHeaderSet);
        toHeaderSet.add(toUri.toString());
        // creating a forward SIP Info to called
        SipServletRequest forkedRequest = helper.createRequest(request, true,
                headers);
        forkedRequest.setRequestURI(toUri);
        String sdp = "Signal=1\r\n" + "Duration=250\r\n";
        forkedRequest.setContent(sdp, "application/dtmf-relay");
        VNXLog.info("forkedRequest = " + forkedRequest);
        forkedRequest.send();
    }

    public static void loadConfigs() {
        try {
            VNXLog.info("current dir:" + System.getProperty("user.dir")
                    + "\n canonicalPath:" + new java.io.File(".").getCanonicalPath());
        } catch (Exception e) {
            VNXLog.error(e);
        }
    }

    protected void terminate(SipSession sipSession, MediaSession mediaSession) {
        SipServletRequest bye = sipSession.createRequest("BYE");
        try {
            bye.send();
            // Clean up media session
            mediaSession.release();
            sipSession.removeAttribute("MEDIA_SESSION");
        } catch (Exception e1) {
            log("Terminating: Cannot send BYE: " + e1);
        }
    }

    public void servletInitialized(SipServletContextEvent event) {
        try {
            VNXLog.info("****** the VNXIVR servlet has been started ********* ");
////			loadConfigs();
//            properties = new Properties();
//            // properties.setProperty(MGCP_STACK_NAME, "SipServlets");
//            properties.setProperty(MGCP_STACK_NAME, "DEFAULT");
//            properties.setProperty(MGCP_PEER_IP, VAppCfg.MGW_ADDRESS);
//            properties.setProperty(MGCP_PEER_PORT, VAppCfg.MGW_PORT);
//
//            properties.setProperty(MGCP_STACK_IP, VAppCfg.LOCAL_ADDRESS);
//            properties.setProperty(MGCP_STACK_PORT, VAppCfg.LOCAL_MGCP_PORT);
//
//            properties.setProperty("mgcp.bind.address", VAppCfg.LOCAL_ADDRESS);
//            properties.setProperty("mgcp.server.address", VAppCfg.MGW_ADDRESS);
//            properties.setProperty("mgcp.local.port", VAppCfg.LOCAL_MGCP_PORT);
//            properties.setProperty("mgcp.server.port", VAppCfg.MGW_PORT);
//            if (event.getServletContext().getAttribute(MS_CONTROL_FACTORY) == null) {
//                DriverImpl d = new DriverImpl();
//                // create the Media Session Factory
//                VNXLog.info("**************************init media with properties: \n"
//                        + properties);
//                final MsControlFactory msControlFactory = new DriverImpl().getFactory(properties);
//                MsControlObjects.msControlFactory = msControlFactory;
//                event.getServletContext().setAttribute(MS_CONTROL_FACTORY,
//                        msControlFactory);
//                VNXLog.info("started MGCP Stack on " + VAppCfg.LOCAL_ADDRESS
//                        + "and port " + VAppCfg.LOCAL_MGCP_PORT + " obj: "
//                        + MsControlObjects.msControlFactory);
//
//            } else {
//                VNXLog.info("MGCP Stack already started on "
//                        + VAppCfg.LOCAL_ADDRESS
//                        + "and port "
//                        + VAppCfg.CA_PORT
//                        + " MS_CONTROL_FACTORY:"
//                        + event.getServletContext().getAttribute(
//                        MS_CONTROL_FACTORY));
//                MsControlObjects.msControlFactory = (MsControlFactory) getServletContext().getAttribute(MS_CONTROL_FACTORY);
//
//            }


        } catch (Exception e) {
            VNXLog.error("couldn't start the underlying MGCP Stack");
            VNXLog.error(e);
        }

    }

    private class NetworkConnectionListener implements
            MediaEventListener<SdpPortManagerEvent> {

        public void onEvent(SdpPortManagerEvent event) {

            SdpPortManager sdpmana = event.getSource();
            NetworkConnection conn = sdpmana.getContainer();
            MediaSession mediaSession = event.getSource().getMediaSession();

            SipSession sipSession = (SipSession) mediaSession.getAttribute("SIP_SESSION");

            SipServletRequest inv = (SipServletRequest) sipSession.getAttribute("UNANSWERED_INVITE");
            

            if (event.isSuccessful()) {
                SipServletResponse resp = inv.createResponse(SipServletResponse.SC_OK);
                try {
                    byte[] sdp = event.getMediaServerSdp();
                    if (sdp == null || sdp.length == 0) {
                        VNXLog.error("can't generating 200 OK Response for INVITE: " + inv);
                        SipServletRequest bye = inv.getSession().createRequest("BYE");
                        bye.addHeader("Reason", "Q.850;cause=16;text=\"no SDP in the 200 OK,Media server has errors\"");
                        bye.send();
                        return;
                    }
                    //modified sdp here
                    sipSession.setAttribute("NETWORK_CONNECTION", conn);
                    String newsdp = new String(sdp);
                    newsdp = newsdp.replaceAll("Mobicents Media Server", "SIP Call");
//		    newsdp=newsdp.replaceAll("a=control:audio", "a=rtpmap:101 telephone-event/8000");
                    newsdp = newsdp.replaceAll("a=control:audio", "");
                    newsdp = newsdp.replaceAll("a=silenceSupp:off", "");
                    resp.setContent(newsdp, "application/sdp");
                    //
                    // Send 200 OK
                    VNXLog.debug("Sent 200 OK Response for INVITE: " + VIVRUtil.dumpSIPMsgHdr(inv));
                    resp.send();
                } catch (Exception e) {
                    VNXLog.error(e);
                    // Clean up
                    sipSession.getApplicationSession().invalidate();
                    mediaSession.release();
                }
            } else {
                try {
                    if (SdpPortManagerEvent.SDP_NOT_ACCEPTABLE.equals(event.getError())) {

                        VNXLog.error("Sending SipServletResponse.SC_NOT_ACCEPTABLE_HERE for INVITE");
                        // Send 488 error response to INVITE
                        inv.createResponse(
                                SipServletResponse.SC_NOT_ACCEPTABLE_HERE).send();
                    } else if (SdpPortManagerEvent.RESOURCE_UNAVAILABLE.equals(event.getError())) {
                        VNXLog.error("Sending SipServletResponse.SC_BUSY_HERE for INVITE");
                        // Send 486 error response to INVITE
                        inv.createResponse(SipServletResponse.SC_BUSY_HERE).send();
                    } else {
                        VNXLog.error("Sending SipServletResponse.SC_SERVER_INTERNAL_ERROR for INVITE");
                        // Some unknown error. Send 500 error response to INVITE
                        inv.createResponse(
                                SipServletResponse.SC_SERVER_INTERNAL_ERROR).send();
                    }
                    // Clean up media session
                    sipSession.removeAttribute("MEDIA_SESSION");
                    mediaSession.release();
                } catch (Exception e) {
                    VNXLog.error(e);

                    // Clean up
                    sipSession.getApplicationSession().invalidate();
                    mediaSession.release();
                }
            }
        }
    }
   public void timeout(ServletTimer servletTimer) {

		String sessionId = (String) servletTimer.getInfo();
		VNXLog.info("Timer fired on sip session " + sessionId);
		SipSession sipSession = servletTimer.getApplicationSession()
				.getSipSession(sessionId);

		if (sipSession != null) {
			MediaGroup mediaGroup = (MediaGroup) sipSession
					.getAttribute("MEDIA_GROUP");
			if (mediaGroup != null) {
				VNXLog.info("Timer fired, stopping the recording");
				try {
					mediaGroup.getRecorder().stop();
				} catch (MsControlException e) {
					VNXLog.info("recording couldn't be stopped", e);
				}
			}
		} else {
			VNXLog.info("the session has not been found, it may have been already invalidated");
		}
	}

    private class MyJoinEventListener implements JoinEventListener {

        public void onEvent(JoinEvent event) {
            try {
                MediaGroup mg = (MediaGroup) event.getThisJoinable();
                SipSession sipSession = (SipSession) mg.getMediaSession().getAttribute("SIP_SESSION");
                SipServletRequest inv = (SipServletRequest) sipSession.getAttribute("UNANSWERED_INVITE");
                SipApplicationSession sipAppSession = sipSession.getApplicationSession();
                sipSession.removeAttribute("UNANSWERED_INVITE");
                if (event.isSuccessful()) {
                    if (JoinEvent.JOINED == event.getEventType()) {
                        // NC Joined to MG
                        VNXLog.debug("NC joined to MG, start recording process for message");
                        try {
                            Recorder recoredr = mg.getRecorder();


                            URI recordfile = new URI(VAppCfg.recordingDir + VIVRUtil.getOriginator(inv)
                                    + "_" + VIVRUtil.getRecipient(inv) + "_" + VDate.now("HHmmss_yyyyMMdd") + ".wav");

                            VNXLog.info("recording the user at " + recordfile);

                            recoredr.record(recordfile, null, null);

                            TimerService timer = (TimerService) getServletContext().getAttribute(TIMER_SERVICE);
                            timer.createTimer(sipAppSession, VAppCfg.recordingDelayTime,
                                    false, sipSession.getId());
                        } catch (Exception e) {
                            VNXLog.error(e);
                        }
                    } else if (JoinEvent.UNJOINED == event.getEventType()) {
                        if (VNXLog.isDebugEnabled()) {
                            VNXLog.debug("Un-Joined MG and NC");
                        }
                    }
                } else {
                    VNXLog.error("Joining of MG and NC failed");
                }
            } catch (Exception ex) {
                VNXLog.error(ex);
            }
        }
    }

   

    
}