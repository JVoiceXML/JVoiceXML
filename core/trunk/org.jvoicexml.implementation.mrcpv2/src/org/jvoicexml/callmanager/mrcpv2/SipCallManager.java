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
import java.util.HashMap;
import java.util.Map;

import javax.sip.SipException;
import javax.sip.address.Address;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.RemoteClientFactory;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
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
public final class SipCallManager implements CallManager, SpeechletService{
    static Logger logger = Logger.getLogger(SipCallManager.class);
    
   
    //TODO: Better management (clean out orphaned sessions, or leases/timeouts)
    /** Map of sessions. */
    private  Map<String, SipCallManagerSession> sessions;

    
    /** Map of terminal names associated to an application. */
    private Map<String, String> applications;    

    /** Factory to create the {@link org.jvoicexml.RemoteClient} instances. */
    protected RemoteClientFactory clientFactory; 
    
    private SipServer sipServer;
    private String cloudUrl;


    private JVoiceXml jvxml;

    /**
     * @return the cloudUrl
     */
    public String getCloudUrl() {
        return cloudUrl;
    }


    /**
     * @param cloudUrl the cloudUrl to set
     */
    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }


    /**
     * @return the sipServer
     */
    public SipServer getSipServer() {
        return sipServer;
    }


    /**
     * @param sipServer the sipServer to set
     */
    public void setSipServer(SipServer sipServer) {
        this.sipServer = sipServer;
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
    
    

    @Override
    public void StopDialog(SipSession pbxSession) throws SipException {
     
        SipCallManagerSession session = sessions.get(pbxSession.getId());
        if (session == null) {
            //todo: throw an exception
        }
        session.getJvxmlSession().hangup();
        session.getMrcpSession().bye();
        session.getPbxSession().bye();
        try {
            session.getSpeechClient().stopActiveRecognitionRequests();
            session.getSpeechClient().shutdown();
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
        } 
        
        //TODO: Clean up after telephony client?
        //session.getTelephonyClient();

        //remove the session from the map
        sessions.remove(pbxSession.getId());
        
    }

    @Override
    public void dtmf(SipSession arg0, char arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startNewMrcpDialog(SipSession pbxSession, SipSession mrcpSession) throws Exception {
            SpeechClient speechClient = new SpeechClientImpl(mrcpSession.getTtsChannel(),mrcpSession.getRecogChannel());
            TelephonyClient telephonyClient = null;//new TelephonyClientImpl(pbxSession.getChannelName());

            Address callee = pbxSession.getSipDialog().getLocalParty();  
            logger.info(callee.toString());
            logger.info(callee.getDisplayName());
            logger.info(callee.getURI().toString());
    
            String applicationUri = applications.get(callee.getDisplayName());
            
            // Create a session (so we can get other signals from the caller)
            // and release resources upon call completion
            String id = pbxSession.getId();
            SipCallManagerSession session = new SipCallManagerSession(id,pbxSession,mrcpSession,speechClient,telephonyClient);

            SipCallParameters parameters = new SipCallParameters();
            parameters.setSpeechClient(speechClient);
            
            
            //todo: is this application really needed?
            ConfiguredApplication application = new ConfiguredApplication();
            application.setUri(applicationUri);
            application.setInputType("mrcpv2");
            application.setOutputType("mrcpv2");
            application.setTerminal(callee.getDisplayName());
            
            final RemoteClient remote;
            try {
                remote = clientFactory.createRemoteClient(this, application, parameters);

                // Create a jvoicxml session and initiate a call at JVoiceXML.
                Session jsession = null;
                jsession = this.getJVoiceXml().createSession(remote);
                jsession.call(new URI(applicationUri));
                
                //add the jvoicexml session to the session bag
                session.setJvxmlSession(jsession);
                synchronized (sessions) {
                    sessions.put(id, session);
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



    @Override
    public void startNewCloudDialog(SipSession pbxSession,
            RTPStreamReplicator rtpReplicator, RtpTransmitter rtpTransmitter) throws Exception {
        SpeechClient speechClient = new SpeechCloudClient(rtpReplicator,rtpTransmitter,cloudUrl);
        TelephonyClient telephonyClient = null;//new TelephonyClientImpl(pbxSession.getChannelName());
   

        Address callee = pbxSession.getSipDialog().getLocalParty();  
        logger.info(callee.toString());
        logger.info(callee.getDisplayName());
        logger.info(callee.getURI().toString());
        
        
        String applicationUri = applications.get(callee.getDisplayName());
        
        // Create a session (so we can get other signals from the caller)
        // and release resources upon call completion
        String id = pbxSession.getId();
        SipCallManagerSession session = new SipCallManagerSession(id,pbxSession,null,speechClient,telephonyClient);
        
        SipCallParameters parameters = new SipCallParameters();
        parameters.setSpeechClient(speechClient);
        
        
        //todo: is this application really needed?
        ConfiguredApplication application = new ConfiguredApplication();
        application.setUri(applicationUri);
        application.setInputType("mrcpv2");
        application.setOutputType("mrcpv2");
        application.setTerminal(callee.getDisplayName());

        
        final RemoteClient remote;
        try {
            remote = clientFactory.createRemoteClient(this, application, parameters);

            // Create a session and initiate a call at JVoiceXML.
            Session jsession = null;
            jsession = this.getJVoiceXml().createSession(remote);
            final URI uri = application.getUriObject();
            jsession.call(uri);
            
            //add the jvoicexml session to the session bag
            session.setJvxmlSession(jsession);
            synchronized (sessions) {
                sessions.put(id, session);
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
    public final void setRemoteClientFactory(
            final RemoteClientFactory factory) {
        clientFactory = factory;
    }

    
    /**
     * Retrieves the reference to the interpreter.
     * @return the interpreter
     */
    public final JVoiceXml getJVoiceXml() {
        return jvxml;
    }

    @Override
    public void setJVoiceXml(JVoiceXml jvxml) {
       this.jvxml = jvxml;
        
    }

    
    // todo: startup/shutdown are in the DialogManagerInterface  and start/stop are in the CallManager Interface -- don't need both sets.
 
    @Override
    public void startup() {
        // TODO Auto-generated method stub
        logger.info("startup mrcp sip callManager");
        sessions = new HashMap<String,SipCallManagerSession>();
        
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
}
