/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Map;

import javax.sip.Dialog;
import javax.sip.SipException;
import javax.sip.address.Address;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.SessionListener;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.RemoteClientFactory;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.mrcp4j.client.MrcpChannel;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechClientImpl;
import org.speechforge.cairo.client.cloudimpl.SpeechCloudClient;
import org.speechforge.cairo.rtp.server.RTPStreamReplicator;
import org.speechforge.cairo.sip.SipSession;
import org.speechforge.zanzibar.sip.SipServer;
import org.speechforge.zanzibar.speechlet.SpeechletService;
import org.speechforge.zanzibar.telephony.TelephonyClient;

import com.spokentech.speechdown.client.rtp.RtpTransmitter;

/**
 * A SIP call manager.
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class SipCallManager
    implements CallManager, SpeechletService, SessionListener {
    static Logger logger = Logger.getLogger(SipCallManager.class);
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(SipCallManager.class);

    //TODO: Better management (clean out orphaned sessions, or leases/timeouts)
    /** Map of sessions. */
    private  Map<String, SipCallManagerSession> sessions;

    //TODO make the ids the same.  Perhaps set the voicexml session id with
    //sip id (rather than have it create its own UUID) or maybe there 
    // is a way to attach the voicexml id to the sip session...
    /** Map of sip id's to voicexml session ids. **/
    private  Map<String, String> idMap;
 
    /** Map of terminal names associated to an application. */
    private Map<String, String> applications;    

    /** Factory to create the {@link org.jvoicexml.RemoteClient} instances. */
    private RemoteClientFactory clientFactory; 

    /** The local SIP server. */
    private SipServer sipServer;
    private String cloudUrl;

    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /**
     * @return the cloudUrl
     */
    public String getCloudUrl() {
        return cloudUrl;
    }


    /**
     * @param url the cloudUrl to set
     */
    public void setCloudUrl(final String url) {
        cloudUrl = url;
    }


    /**
     * @return the sipServer
     */
    public SipServer getSipServer() {
        return sipServer;
    }


    /**
     * @param server the sipServer to set
     */
    public void setSipServer(final SipServer server) {
        sipServer = server;
    }


    /**
     * @return the applications
     */
    public Map<String, String> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, String> applications) {
        this.applications = applications;
    }

    
    
    public String getApplicationUrl(String applicationId) {
        return applications.get(applicationId);
    }
    
    //TODO: Rename this method to "stopDialog"  Need to change the interface first in the thirdparty jar.  
    @Override
    public void StopDialog(SipSession pbxSession) throws SipException {

        String id = pbxSession.getId();
        cleanupSession(id);
        
    }


    private void cleanupSession(final String id) {

        final SipCallManagerSession session = sessions.get(id);
        if (session == null) {
            LOGGER.error("no session given. unable to cleanup session");
            return;
        }
        session.getJvxmlSession().hangup();

        try {
            //need to check for null mrcp session (in speechcloud case it
            //will be null)
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoMediaControlChannelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        //TODO: Clean up after telephony client?
        //session.getTelephonyClient();

        //remove the session from the map
        sessions.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dtmf(final SipSession session, final char dtmf) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewMrcpDialog(final SipSession pbxSession,
            final SipSession mrcpSession) throws Exception {
            final MrcpChannel ttsChannel = mrcpSession.getTtsChannel();
            final MrcpChannel asrChannel = mrcpSession.getRecogChannel();
            final SpeechClient speechClient =
                new SpeechClientImpl(ttsChannel, asrChannel);
            final TelephonyClient telephonyClient = null;//new TelephonyClientImpl(pbxSession.getChannelName());
            final Dialog dialog = pbxSession.getSipDialog();
            final Address localParty = dialog.getLocalParty();
            final String displayName = localParty.getDisplayName();
            final String calledNumber;
            if ((displayName == null) || displayName.startsWith("sip:")) {
                final String uri = localParty.getURI().toString();
                String[] parts = uri.split(":");
                // get the first part of the address, which is the number that
                // was called.
                String[] parts2 = parts[1].split("@");  
                calledNumber = parts2[0];
            } else {
                calledNumber = displayName;
            }
            //separate the scheme and port from the address
            final String applicationUri = applications.get(calledNumber);


            //use the number for looking up the application
            LOGGER.info("called number: '" + calledNumber + "'");
            LOGGER.info("calling application '" + applicationUri + "'...");
                  
            // Create a session (so we can get other signals from the caller)
            // and release resources upon call completion
            final String id = pbxSession.getId();
            final SipCallManagerSession session =
                new SipCallManagerSession(id, pbxSession, mrcpSession,
                        speechClient, telephonyClient);

            final SipCallParameters parameters = new SipCallParameters();
            parameters.setSpeechClient(speechClient);
            
            
            //todo: is this application really needed?
            ConfiguredApplication application = new ConfiguredApplication();
            application.setUri(applicationUri);
            application.setInputType("mrcpv2");
            application.setOutputType("mrcpv2");
            application.setTerminal(calledNumber);
            
            final RemoteClient remote;
            try {
                remote = clientFactory.createRemoteClient(
                        this, application, parameters);

                // Create a jvoicxml session and initiate a call at JVoiceXML.
                final Session jsession = jvxml.createSession(remote);
                
                //add a listener to capture the end of voicexml session event
                jsession.addSessionListener(this);
                
                //add the jvoicexml session to the session bag
                session.setJvxmlSession(jsession);
                synchronized (sessions) {
                    sessions.put(id, session);
                }
                
                //workaround to deal with two id's
                //maps the voicexml sessionid to sip session id 
                //needed for case when the voicxml session ends before a hang up
                // and need to get to close the sip session
                synchronized (idMap) {
                   idMap.put(jsession.getSessionID(), id);
                }

                //start the application
                final URI uri = application.getUriObject();
  
                jsession.call(uri);
            }  catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            } catch (ErrorEvent e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new Exception(e);
            }
    }



    @Override
    public void startNewCloudDialog(SipSession pbxSession,
            RTPStreamReplicator rtpReplicator, RtpTransmitter rtpTransmitter) throws Exception {
        SpeechClient speechClient = new SpeechCloudClient(rtpReplicator,rtpTransmitter,cloudUrl);
        TelephonyClient telephonyClient = null;//new TelephonyClientImpl(pbxSession.getChannelName());
   

        Address localParty = pbxSession.getSipDialog().getLocalParty();  
        String applicationUri = applications.get(localParty.getDisplayName());
        
        // Create a session (so we can get other signals from the caller)
        // and release resources upon call completion
        String id = pbxSession.getId();
        SipCallManagerSession session =
            new SipCallManagerSession(id, pbxSession, null, speechClient,
                    telephonyClient);
        
        SipCallParameters parameters = new SipCallParameters();
        parameters.setSpeechClient(speechClient);
        
        
        //todo: is this application really needed?
        ConfiguredApplication application = new ConfiguredApplication();
        application.setUri(applicationUri);
        application.setInputType("mrcpv2");
        application.setOutputType("mrcpv2");
        application.setTerminal(localParty.getDisplayName());

        
        final RemoteClient remote;
        try {
            remote = clientFactory.createRemoteClient(this, application,
                    parameters);

            // Create a session and initiate a call at JVoiceXML.
            final Session jsession = getJVoiceXml().createSession(remote);
            
            //add a listener to capture the end of voicexml session event
            jsession.addSessionListener(this);
            
            //start the application
            final URI uri = application.getUriObject();
            jsession.call(uri);
            
            //add the jvoicexml session to the session bag
            session.setJvxmlSession(jsession);
            synchronized (sessions) {
                sessions.put(id, session);
            }
            
            //workaround to deal with two id's
            //maps the voicexml sessionid to sip session id 
            //needed for case when the voicxml session ends before a hang up and
            // need to get to close the sip session
            synchronized (idMap) {
               idMap.put(session.getId(), id);
            }

        }  catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } catch (ErrorEvent e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new Exception(e);
        }


    }

    /**
     * Sets the remote client factory.
     * @param factory the remote client factory.
     * @since 0.7
     */
    public void setRemoteClientFactory(
            final RemoteClientFactory factory) {
        clientFactory = factory;
    }

    
    /**
     * Retrieves the reference to the interpreter.
     * @return the interpreter
     */
    public JVoiceXml getJVoiceXml() {
        return jvxml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
       jvxml = jvoicexml;
    }

    
    // todo: startup/shutdown are in the DialogManagerInterface  and start/stop are in the CallManager Interface -- don't need both sets.
 
    /**
     * {@inheritDoc}
     */
    @Override
    public void startup() {
        // TODO Auto-generated method stub
        logger.info("startup mrcp sip callManager");
        sessions = new java.util.HashMap<String, SipCallManagerSession>();
        idMap = new java.util.HashMap<String, String>();
    }
    
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        logger.info("shutdown mrcp sip callManager");
        
    }
    

    @Override
    public void start() throws NoresourceError, IOException {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void sessionEnded(final Session session) {
        String id = session.getSessionID();
        //workaround to deal with two id's
        //maps the voicexml sessionid to sip session id 
        //needed for case when the voicxml session ends before a hang up and
        // need to get to close the sip session
        
        // get the sip sesison id
        
        //remove the session id mapping
        final String sipId;
        synchronized (idMap) {
            sipId = idMap.get(id);
            idMap.remove(id);
        }
        
        //clean up the session
        cleanupSession(sipId);
    }
}
