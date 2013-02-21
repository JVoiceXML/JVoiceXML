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

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;


import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import org.apache.commons.configuration.Configuration;

import org.mobicents.servlet.sip.restcomm.BootstrapException;
import org.mobicents.servlet.sip.restcomm.Bootstrapper;
import org.mobicents.servlet.sip.restcomm.Janitor;
import org.mobicents.servlet.sip.restcomm.ServiceLocator;
import org.mobicents.servlet.sip.restcomm.media.api.Call;
import org.mobicents.servlet.sip.restcomm.media.api.CallException;
import org.mobicents.servlet.sip.restcomm.media.api.CallManager;
import org.mobicents.servlet.sip.restcomm.media.api.CallManagerException;

import org.apache.log4j.Logger;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.xml.vxml.Vxml;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Meta;
import org.jvoicexml.event.error.NoresourceError;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.Terminal;
import org.jvoicexml.callmanager.TerminalConnectionInformationFactory;
import org.jvoicexml.implementation.mobicents.EmbeddedJVXML;
import org.util.ExLog;
import org.util.SIPUtil;



/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
public final class MgcpCallManager extends SipServlet 
    implements org.jvoicexml.CallManager,CallManager, SipApplicationSessionListener 
{
        private static final Logger LOGGER = Logger.getLogger(MgcpIvrEndpoint.class);
    private static final long serialVersionUID = 4758133818077979879L;
    private static SipFactory sipFactory;
    private static Configuration configuration;
    private static String proxyUser;
    private static String proxyPassword;
    private static SipURI proxyUri;
    private static MgcpServerManager servers;
    /** for implement interfacing to EmbeddedJVXML
     * 
     */
    private static EmbeddedJVXML embJVXML = null;
    /** Factory to create the {@link org.jvoicexml.ConnectionInformation} instances. */
    private TerminalConnectionInformationFactory clientFactory;

    /** Map of terminal names associated to an application. */
    private final Map<String, ConfiguredApplication> applications;

    /** All known terminals. */
    private Collection<Terminal> terminals;
    /** Established sessions. */
    private final Map<Terminal, Session> sessions;
    public MgcpCallManager() {
        super();
        applications = new java.util.HashMap<String, ConfiguredApplication>();
        sessions = new java.util.HashMap<Terminal, Session>();
    }
    
    @Override
    public Call createExternalCall(final String from, final String to) throws CallManagerException {
        try {
            String uri = proxyUri.toString().replaceFirst("sip:", "");
            final SipURI fromUri = sipFactory.createSipURI(from, uri);
            final SipURI toUri = sipFactory.createSipURI(to, uri);
            return createCall(fromUri, toUri);
        } catch (final Exception exception) {
            throw new CallManagerException(exception);
        }
    }
    
    @Override
    public Call createUserAgentCall(final String from, final String to) throws CallManagerException {
        try {
            String uri = proxyUri.toString().replaceFirst("sip:", "");
            final SipURI fromUri = sipFactory.createSipURI(from, uri);
            final URI toUri = sipFactory.createURI(to);
            return createCall(fromUri, toUri);
        } catch (final Exception exception) {
            throw new CallManagerException(exception);
        }
    }
    
    @Override
    public Call createCall(final String from, final String to) throws CallManagerException {
        try {
            final URI fromUri = sipFactory.createURI(from);
            final URI toUri = sipFactory.createURI(to);
            return createCall(fromUri, toUri);
        } catch (final Exception exception) {
            throw new CallManagerException(exception);
        }
    }
    
    private Call createCall(URI from, URI to) throws CallManagerException {
        SipServletRequest invite = null;
        try {
            invite = invite(from, to);
        } catch (final ServletException exception) {
            throw new CallManagerException(exception);
        }
        final MgcpServer server = servers.getMediaServer();
        final MgcpCallTerminal call = new MgcpCallTerminal(invite, server);
        invite.getApplicationSession().setAttribute("CALL", call);
        return call;
    }
    
    private SipServletRequest invite(final URI from, final URI to) throws ServletException {
        final SipApplicationSession application = sipFactory.createApplicationSession();
        final SipServletRequest invite = sipFactory.createRequest(application, "INVITE", from, to);
        final StringBuilder buffer = new StringBuilder();
        buffer.append(((SipURI) to).getHost());
        final int port = ((SipURI) to).getPort();
        if (port > -1) {
            buffer.append(":");
            buffer.append(port);
        }
        final SipURI destination = sipFactory.createSipURI(null, buffer.toString());
        invite.pushRoute(destination);
        final SipSession session = invite.getSession();
        session.setHandler("SipCallManager");
        return invite;
    }
    
    @Override
    protected final void doAck(final SipServletRequest request) throws ServletException, IOException {
        final SipApplicationSession session = request.getApplicationSession();
        final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
        call.established();
    }
    
    @Override
    protected final void doBye(final SipServletRequest request) throws ServletException, IOException 
    {
        try
        {
            LOGGER.info("incoming SIPBYE:"+SIPUtil.dumpSIPMsgHdr2(request)) ;
            final SipApplicationSession session = request.getApplicationSession();
            final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
            call.bye(request);
            //terminal a jvoice session
            embJVXML.deleteSession(call);
        }
          catch(Exception ex)       
        {
            ExLog.exception(LOGGER, ex);
        }
    }
    
    @Override
    protected final void doCancel(final SipServletRequest request) throws ServletException, IOException {
        final SipApplicationSession session = request.getApplicationSession();
        final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
        call.cancel(request);
    }
    
    @Override
    protected void doProvisionalResponse(SipServletResponse response) throws ServletException, IOException {
        final SipServletRequest request = response.getRequest();
        final MgcpCallTerminal call = (MgcpCallTerminal) request.getApplicationSession().getAttribute("CALL");
        final int status = response.getStatus();
        if (SipServletResponse.SC_RINGING == status || SipServletResponse.SC_SESSION_PROGRESS == status) {
            call.ringing();
        }
    }
    
    @Override
    protected void doErrorResponse(final SipServletResponse response) throws ServletException, IOException {
        final SipServletRequest request = response.getRequest();
        final MgcpCallTerminal call = (MgcpCallTerminal) request.getApplicationSession().getAttribute("CALL");
        final String method = request.getMethod();
        final int status = response.getStatus();
        if ("INVITE".equalsIgnoreCase(method)) {
            if (SipServletResponse.SC_UNAUTHORIZED == status || SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED == status) {
                final SipServletRequest invite = invite(request.getFrom().getURI(), request.getTo().getURI());
                final AuthInfo authorization = sipFactory.createAuthInfo();
                final String realm = response.getChallengeRealms().next();                
                authorization.addAuthInfo(status, realm, proxyUser, proxyPassword);
                invite.addAuthHeader(response, authorization);
                if (request.getContentLength() > 0) {
                    invite.setContent(request.getContent(), request.getContentType());
                }
                invite.getApplicationSession().setAttribute("CALL", call);
                invite.send();
                call.updateInitialInvite(invite);
            } else if (SipServletResponse.SC_BUSY_HERE == status || SipServletResponse.SC_BUSY_EVERYWHERE == status) {
                call.busy();
            } else {
                call.failed();
            }
        } else if ("CANCEL".equalsIgnoreCase(method)) {
            if (SipServletResponse.SC_REQUEST_TERMINATED == status) {
                final SipServletRequest ack = response.createAck();
                ack.send();
            }
        }
    }
    
    
    

    protected final void doInvite(final SipServletRequest request) throws ServletException, IOException {
        try 
        {
            LOGGER.info("incoming SIPInvite:"+SIPUtil.dumpSIPMsgHdr2(request)) ;
            // Create the call.
            final MgcpServer server = servers.getMediaServer();
            final MgcpCallTerminal call = new MgcpCallTerminal(server);
            call.setSIPCallID(request.getCallId());
            call.trying(request);
            call.answer();
            request.getApplicationSession().setAttribute("CALL", call);
            // Schedule the VXML script to execute for this call.
            //
            // Create a session and initiate a call at JVoiceXML.
            LOGGER.debug("Create a session and initiate a call at JVoiceXML.");
            embJVXML.execute(call);
        }
         catch(Exception ex)       
        {
            ExLog.exception(LOGGER, ex);
        }
                    
    }
    
    protected void doInfo(SipServletRequest request) throws ServletException,
            IOException 
    {
        try {
            String fromUri = request.getFrom().getURI().toString();
            SipURI toUri = (SipURI) request.getTo().getURI();
            LOGGER.info("<<<<<<<<<<< sipInfo comming:\n" + "fromUri:" + fromUri
                    + " toUri:" + toUri + " content:"+request.getContent());
            SipServletResponse ok = request.createResponse(SipServletResponse.SC_OK);
            ok.send();
            //get jvxml session
            embJVXML.procSIPInfo(request);
            
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
    }
    
    
    @Override
    protected void doOptions(final SipServletRequest request)
            throws ServletException, IOException {
        request.createResponse(SipServletResponse.SC_OK).send();
    }
    
    @Override
    protected void doSuccessResponse(final SipServletResponse response) throws ServletException, IOException {
        final SipServletRequest request = response.getRequest();
        final SipApplicationSession session = response.getApplicationSession();
        if (request.getMethod().equals("INVITE") && response.getStatus() == SipServletResponse.SC_OK) {
            final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
            try {
                call.established(response);
            } catch (final CallException exception) {
                throw new ServletException(exception);
            }
        }
    }
    
    @Override
    public final void destroy() {
        Janitor.cleanup();
    }
    

    
   
 
    @Override
    public final void init(final ServletConfig config) throws ServletException 
    {
        try{
        final ServletContext context = config.getServletContext();
        LOGGER.info("initializing the servlet with context:" + context + " ServerInfo:"
                + context.getServerInfo() + " ServletContextName:" + context.getServletContextName()
                + " config:"+config);
        context.setAttribute("org.mobicents.servlet.sip.restcomm.callmanager.CallManager", this);
        try {
            Bootstrapper.bootstrap(config);
        } catch (final BootstrapException exception) {
            throw new ServletException(exception);
        }
        sipFactory = (SipFactory) config.getServletContext().getAttribute(SIP_FACTORY);
        final ServiceLocator services = ServiceLocator.getInstance();
        servers = services.get(MgcpServerManager.class);
        configuration = services.get(Configuration.class);
        proxyUser = configuration.getString("outbound-proxy-user");
        proxyPassword = configuration.getString("outbound-proxy-password");
        final String uri = configuration.getString("outbound-proxy-uri");
        if (uri != null && !uri.isEmpty()) {
            proxyUri = sipFactory.createSipURI(null, uri);
        }
        ///////////
        LOGGER.info("initializing jvoicexml inteface........");
        embJVXML = new EmbeddedJVXML();
        embJVXML.init();
//        //init jvoicexml context
//        contextJXML = new InitialContext();
//        LOGGER.info("init jvoicexml context:contextJXML:"+contextJXML);
//        
//        //
//        LOGGER.info("creating VoiceXML documents and binding to the applications.....");
//        vxmlDocument = createVXMLDoc();
//        printDocument(vxmlDocument);
//        
//        //
//        LOGGER.info("adding VXML document to repository...");
//        vxmlURI=addDocument(vxmlDocument);
//        
        }
        catch(Exception e)
        {
            ExLog.exception(LOGGER, e);
        }
    }
    
    @Override
    public void sessionCreated(final SipApplicationSessionEvent event) {
    }
    
    @Override
    public void sessionDestroyed(final SipApplicationSessionEvent event) {
    }
    
    @Override
    public void sessionExpired(final SipApplicationSessionEvent event) {
        final SipApplicationSession session = event.getApplicationSession();
        final MgcpCallTerminal call = (MgcpCallTerminal) session.getAttribute("CALL");
        call.failed();
        final StringBuilder buffer = new StringBuilder();
        buffer.append("A call with ID ").append(call.getSid().toString()).append(" was forcefully clean up after SipApplicationSession timed out.");
        LOGGER.warn(buffer.toString());
    }
    
    @Override
    public void sessionReadyToInvalidate(final SipApplicationSessionEvent event) {
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
//       jvxml = jvoicexml;
    }
     /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws NoresourceError, IOException {
            LOGGER.error("error, it will be implemented late");
    }

       /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try 
        {
            LOGGER.error("error, it will be implemented late");
        } catch (Exception e) 
        {
            ExLog.exception(LOGGER, e);
        } 
    }
    
     /**
     * Create a simple VoiceXML document containing the hello world phrase.
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    
    private VoiceXmlDocument createVXMLDoc() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();

        final Meta author = vxml.appendChild(Meta.class);
        author.setName("author");
        author.setContent("VNXTele group");

        final Meta copyright = vxml.appendChild(Meta.class);
        copyright.setName("copyright");
        copyright.setContent("2012 VNXTele group - "
                + "http://VNXTele.sourceforge.net");

        final Form form = vxml.appendChild(Form.class);
        form.setId("play_wav");
        final Block block = form.appendChild(Block.class);
        final Audio audio = block.appendChild(Audio.class);
        final File file = new File("config/ivrClientWelcome.wav");
        final java.net.URI src = file.toURI();
        audio.setSrc(src);
        return document;
    }
    /**
     * Print the given VoiceXML document to <code>stdout</code>. Does nothing
     * if an error occurs.
     * @param document The VoiceXML document to print.
     * @return VoiceXML document as an XML string, <code>null</code> in case
     * of an error.
     */
    private void printDocument(final VoiceXmlDocument document) {
        String xml="";
        try {
            xml = document.toXml();
        } catch (IOException ioe) {
            ExLog.exception(LOGGER, ioe);
        }
        LOGGER.info(xml);
    }
    
//    /**
//     * Add the given document as a single document application.
//     * @param document The only document in this application.
//     * @return URI of the first document.
//     */
//    private java.net.URI addDocument(final VoiceXmlDocument document) {
//        MappedDocumentRepository repository;
//        try {
//            repository = (MappedDocumentRepository) contextJXML.lookup("MappedDocumentRepository");
//        } catch (javax.naming.NamingException ne) 
//        {
//            LOGGER.error("error obtaining the documentrepository", ne);
//            return null;
//        }
//
//        final java.net.URI uri;
//        try {
//            uri = repository.getUri("/root");
//        } catch (URISyntaxException e) {
//            LOGGER.error("error creating the URI", e);
//            return null;
//        }
//        repository.addDocument(uri, document.toString());
//        return uri;
//    }

    /**
     * Calls the VoiceXML interpreter context to process the given XML document.
     * @param uri URI of the first document to load
     * @exception JVoiceXMLEvent
     *            Error processing the call.
     */
//    private void interpretDocument(final java.net.URI uri)
//            throws JVoiceXMLEvent 
//    {
//        JVoiceXml jvxml;
//        try {
//            jvxml = (JVoiceXml) contextJXML.lookup("JVoiceXml");
//        } catch (javax.naming.NamingException ne) 
//        {
//            LOGGER.error("error obtaining JVoiceXml", ne);
//
//            return;
//        }
//        final ConnectionInformation client = new BasicConnectionInformation(
//                "dummy", "mobicents", "mobicents");
//        final Session session = jvxml.createSession(client);
//        
//        session.call(uri);
//        session.waitSessionEnd();
//        session.hangup();
//    }

}
