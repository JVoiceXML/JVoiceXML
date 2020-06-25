/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.callmanager.mrcpv2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.sip.Dialog;
import javax.sip.ObjectInUseException;
import javax.sip.SipException;
import javax.sip.address.Address;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SessionListener;
import org.jvoicexml.client.mrcpv2.Mrcpv2ConnectionInformation;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.jvxml.JVoiceXmlCallControl;
import org.jvoicexml.zanzibar.sip.SipServer;
import org.jvoicexml.zanzibar.speechlet.SpeechletService;
import org.mrcp4j.client.MrcpChannel;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientImpl;
import org.speechforge.cairo.sip.SipSession;

/**
 * A SIP call manager.
 * 
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @since 0.7.3
 */
public final class SipCallManager
        implements CallManager, SpeechletService, SessionListener {
    /** Logger instance. */
    private static final Logger LOGGER = LogManager
            .getLogger(SipCallManager.class);

    /** Map of sessions. */
    private Map<SessionIdentifier, SipCallManagerSession> sessions;

    /** Map of terminal names associated to an application. */
    private Map<String, String> applications;

    /** The local SIP server. */
    private SipServer sipServer;

    /** Reference to JVoiceXML. */
    private JVoiceXmlCore jvxml;

    /** The implementation platform factory. */
    private ImplementationPlatformFactory platformFactory;
    
    /** Flag if the call manager has been started. */
    private boolean started;
    
    /**
     * Sets the SIP server.
     * 
     * @param server
     *            the sipServer to set
     */
    public void setSipServer(final SipServer server) {
        sipServer = server;
    }

    /**
     * Sets the configured applications.
     * 
     * @param apps
     *            the configured applications
     */
    public void setApplications(final Map<String, String> apps) {
        applications = apps;
        LOGGER.info("loaded applications:");
        for (String id : applications.keySet()) {
            final String url = applications.get(id);
            LOGGER.info(" - " + id + ": " + url);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopDialog(final SipSession pbxSession) throws SipException {
        final String sessionId = pbxSession.getId();
        final SessionIdentifier id = new SipSessionIdentifier(sessionId);
        LOGGER.info("stopping dialog for '" + id + "'");
        final SipCallManagerSession session;
        synchronized (sessions) {
            session = sessions.get(id);
            if (session == null) {
                LOGGER.warn("cannot stop dialog: no session '" + id + "'");
                return;
            }
        }
        final Telephony telephony = session.getTelephony();
        telephony.hangup();
        cleanupSession(id);
    }

    /**
     * Cleanup the session after the call ended.
     * 
     * @param sessionId
     *            the session id.
     */
    private void cleanupSession(final SessionIdentifier sessionId) {
        final SipCallManagerSession session;
        synchronized (sessions) {
            session = sessions.get(sessionId);
        }
        if (session == null) {
            LOGGER.warn("no session given. unable to cleanup session");
            return;
        }
        // Hangup the dialog
        final Session jvxmlSession = session.getJvxmlSession();
        jvxmlSession.hangup();
        try {
            // need to check for null mrcp session
            final SipSession mrcpsession = session.getMrcpSession();
            if (mrcpsession != null) {
                mrcpsession.bye();
            }
            final SipSession pbxsession = session.getPbxSession();
            pbxsession.bye();
            final SpeechClient client = session.getSpeechClient();
            client.stopActiveRecognitionRequests();
            client.shutdown();
        } catch (MrcpInvocationException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (NoMediaControlChannelException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (SipException e) {
            LOGGER.error(e.getMessage(), e);
        }

        // remove the session from the map
        synchronized (sessions) {
            sessions.remove(sessionId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewMrcpDialog(final SipSession pbxSession,
            final SipSession mrcpSession) throws Exception {
        // Create a session (so we can get other signals from the caller)
        // and release resources upon call completio
        final String sessionId = pbxSession.getId();
        final SessionIdentifier id = new SipSessionIdentifier(sessionId);
        final SpeechClient speechClient = createSpeechClient(mrcpSession);
        try {
            final Mrcpv2ConnectionInformation info =
                    createConnectionInformation(pbxSession, mrcpSession);
            final ImplementationPlatform platform =
                    platformFactory.getImplementationPlatform(info);
            final JVoiceXmlCallControl call = (JVoiceXmlCallControl) platform.getCallControl();
            final Telephony telephony = call.getTelephony();
            final SipCallManagerSession session = new SipCallManagerSession(id,
                            pbxSession, mrcpSession, speechClient, null,
                            telephony);
            // Create a jvoicexml session and initiate a call at JVoiceXML.
            final Session jsession = jvxml.createSession(info, platform, id);

            // add a listener to capture the end of voicexml session event
            jsession.addSessionListener(this);

            // add the jvoicexml session to the session bag
            session.setJvxmlSession(jsession);
            synchronized (sessions) {
                sessions.put(id, session);
            }

            // Get the random code
            final String randomCode = getRandomCode(pbxSession);
            // Append the sessionId to the application uri
            final String applicationUri = applications.get(info.getCalledDevice().toString())
                    + "?sessionId=" + jsession.getSessionId() + "&randomCode="
                    + randomCode;

            LOGGER.info("called number: '" + info.getCalledDevice() + "'");
            LOGGER.info("calling application '" + applicationUri + "'...");

            // start the application
            final URI uri = new URI(applicationUri);
            jsession.call(uri);
        } catch (Exception  e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (ErrorEvent | ConnectionDisconnectHangupEvent e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
    }

    private Mrcpv2ConnectionInformation createConnectionInformation(
            final SipSession pbxSession, final SipSession mrcpSession)
                    throws URISyntaxException {

        // Create a session (so we can get other signals from the caller)
        // and release resources upon call completion
        final Dialog dialog = pbxSession.getSipDialog();
        final Address remoteParty = dialog.getRemoteParty();
        final URI callingDevice = getURI(remoteParty);
        final Address localParty = dialog.getLocalParty();
        final URI calledDevice = getCalledNumber(localParty);
        final Mrcpv2ConnectionInformation info =
                new Mrcpv2ConnectionInformation(callingDevice, calledDevice);
        final SpeechClient speechClient = createSpeechClient(mrcpSession);
        info.setTtsClient(speechClient);
        info.setAsrClient(speechClient);
        info.setProtocolName("sip");
        return info;
    }
    
    private URI getURI(final Address address) throws URISyntaxException {
        final javax.sip.address.URI uri = address.getURI();
        final String str = uri.toString();
        return new URI(str);
    }
    
    /**
     * Creates an instance of a speech client.
     * @param mrcpSession the current mrcp session
     * @return created speech client
     * @since 0.7.8
     */
    private SpeechClient createSpeechClient(final SipSession mrcpSession) {
        final MrcpChannel ttsChannel = mrcpSession.getTtsChannel();
        final MrcpChannel asrChannel = mrcpSession.getRecogChannel();
        final SpeechClient speechClient = new SpeechClientImpl(ttsChannel,
                asrChannel);
        return speechClient;
    }

    /**
     * Extracts the called number from the local party address
     * 
     * @param localParty
     *            the local party address
     * @return called number
     * @throws URISyntaxException error converting the address into a URI
     * @since 0.7.8
     */
    private URI getCalledNumber(final Address localParty) throws URISyntaxException {
        final String displayName = localParty.getDisplayName();
        if ((displayName == null) || displayName.startsWith("sip:")) {
            final String uri = localParty.getURI().toString();
            final String[] parts = uri.split(":");
            // get the first part of the address, which is the number that
            // was called.
            final String[] parts2 = parts[1].split("@");
            return new URI(parts2[0]);
        } else {
            return new URI(displayName);
        }
    }

    /**
     * Tries to obtain a random code from the calling number
     * 
     * @param callingNumber
     *            the calling number
     * @return random code, empty string if there is none.
     * @since 0.7.8
     */
    private String getRandomCode(final SipSession pbxSession) {
        final String callingNumber = pbxSession.getSipDialog().getRemoteParty().getURI().toString();
	if (callingNumber.startsWith("sip:")) {
	    String[] parts = callingNumber.split(":");
	    String[] parts2 = parts[1].split("@");
	    String number = parts2[0];
	    if (number.length() > 8) {
		return number.substring(8);
	    }
    	}
	LOGGER.warn("No randomCode used.");
	return "";
    }

    /**
     * Retrieves the reference to the interpreter.
     * 
     * @return the interpreter
     */
    public JVoiceXml getJVoiceXml() {
        return jvxml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXmlCore jvoicexml) {
        jvxml = jvoicexml;
        platformFactory = jvxml.getImplementationPlatformFactory();
    }

    /**
     * {@inheritDoc}
     * 
     * Unused. Compatibility for {@link SpeechletService}
     */
    @Override
    public void startup() {
    }

    /**
     * {@inheritDoc}
     * 
     * Unused. Compatibility for {@link SpeechletService}
     */
    @Override
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     * 
     * Unused. Compatibility for {@link SpeechletService}
     */
    @Override
    public void dtmf(final SipSession session, final char dtmf) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws NoresourceError, IOException {
        LOGGER.info("startup MRCPv2 SIP CallManager");
        sessions = new java.util.HashMap<SessionIdentifier, SipCallManagerSession>();
        sipServer.startup();
        started = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("shutdown MRCPv2 SIP CallManager");
        try {
            if (sipServer != null) {
                sipServer.shutdown();
            }
        } catch (ObjectInUseException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getLocalizedMessage(), e);
            }
        } finally {
            sipServer = null;
            started = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionStarted(final Session session) {
        final SessionIdentifier id = session.getSessionId();
        LOGGER.info("session "+ id + " started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEnded(final Session session) {
        // clean up the session
        final SessionIdentifier id = session.getSessionId();
        cleanupSession(id);
    }
}
