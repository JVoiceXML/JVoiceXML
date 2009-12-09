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

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sip.SipException;
import javax.sip.address.Address;

import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.callmanager.BaseCallManager;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.Terminal;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
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
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class SipCallManager extends BaseCallManager implements SpeechletService{
    static Logger logger = Logger.getLogger(SipCallManager.class);
    
   
    //TODO: Combine with other session in base call manager?
    //TODO: Better management (clean out orphaned sessions, or leases/timeouts)
    /** Map of sessions. */
    private  Map<String, SipCallManagerSession> sipSessions;
    
    
    private SipServer sipServer;
    private String cloudUrl;

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
     * {@inheritDoc}
     */
    @Override
    protected Collection<Terminal> createTerminals() throws NoresourceError {
        final Collection<ConfiguredApplication> applications =
            getApplications();
        final Collection<Terminal> terminals =
            new java.util.ArrayList<Terminal>();
        for (ConfiguredApplication application : applications) {
            final String terminalName = application.getTerminal();
            final Terminal terminal = new SipTerminal(terminalName, this);
            terminals.add(terminal);
        }
        return terminals;
    }


    @Override
    public void StopDialog(SipSession pbxSession) throws SipException {

        
        SipCallManagerSession session = sipSessions.get(pbxSession.getId());
        //todo Stop the jvociexml session (other end hungup)
        
        //remove the session from the map
        sipSessions.remove(pbxSession.getId());
        
    }

    @Override
    public void dtmf(SipSession arg0, char arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startNewMrcpDialog(SipSession pbxSession, SipSession mrcpSession) throws Exception {
            SpeechClient speechClient = new SpeechClientImpl(mrcpSession.getTtsChannel(),mrcpSession.getRecogChannel());
            TelephonyClient telephonyClient = null;//new TelephonyClientImpl(pbxSession.getChannelName());
            
            //TODO:  Get the terminal (but for what purpose?)
            //TODO:  Get the name of the application (from the terminal)
            //maybe a lookup table callee->app  is that the terminal collection
            //but then it should be a map.  Also it seems that terminals are
            //one-to-one mapping to a call(not one-to-many
            Terminal terminal = null;
            ConfiguredApplication application = new ConfiguredApplication();
            //application.setUri("file:///work/zanzibar/src/main/voicexml/hello.vxml");
            application.setUri("file:///work/zanzibar/src/main/voicexml/parrot.vxml");
            //application.setUri("http://localhost:8080/voicexml/test.vxml");

            Address callee = pbxSession.getSipDialog().getLocalParty();  
            logger.info(callee.toString());
            logger.info(callee.getDisplayName());
            logger.info(callee.getURI().toString());
            
            //Perhaps something like this?
            //terminal term = terminalLookup(callee);
            //final String name = term.getName();
            //final ConfiguredApplication application = applications.get(name);
            //if (application == null) {
            //    throw new BadFetchError("No application defined for terminal '"
            //            + name + "'");
            //}
  

            
            // Create a session (so we can get other signals from the caller)
            // and release resources upon call completion
            String id =pbxSession.getId();
            SipCallManagerSession session = new SipCallManagerSession(id,pbxSession,mrcpSession,speechClient,telephonyClient);
            sipSessions.put(id, session);

            SipCallParameters parameters = new SipCallParameters();
            parameters.setSpeechClient(speechClient);
            
            
            final RemoteClient remote;
            try {
                remote = clientFactory.createRemoteClient(this, application, parameters);

                // Create a session and initiate a call at JVoiceXML.
                Session jsession = null;
                jsession = this.getJVoiceXml().createSession(remote);
                final URI uri = application.getUriObject();
                jsession.call(uri);
                synchronized (sessions) {
                    sessions.put(terminal, jsession);
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
        SpeechClient speechClient = new SpeechCloudClient(rtpReplicator,rtpTransmitter);
        TelephonyClient telephonyClient = null;//new TelephonyClientImpl(pbxSession.getChannelName());
   
        //TODO:  Get the terminal (but for what purpose?)
        //TODO:  Get the name of the application (from the terminal)
        //maybe a lookup table callee->app  is that the terminal collection
        //but then it should be a map.  Also it seems that terminals are
        //one-to-one mapping to a call(not one-to-many
        Terminal terminal = null;
        ConfiguredApplication application = new ConfiguredApplication();
        //application.setUri("file:///work/zanzibar/src/main/voicexml/hello.vxml");
        application.setUri("file:///work/zanzibar/src/main/voicexml/parrot.vxml");
        //application.setUri("http://localhost:8080/voicexml/test.vxml");

        Address callee = pbxSession.getSipDialog().getLocalParty();  
        logger.info(callee.toString());
        logger.info(callee.getDisplayName());
        logger.info(callee.getURI().toString());
        
        //Perhaps something like this?
        //terminal term = terminalLookup(callee);
        //final String name = term.getName();
        //final ConfiguredApplication application = applications.get(name);
        //if (application == null) {
        //    throw new BadFetchError("No application defined for terminal '"
        //            + name + "'");
        //}


        
        // Create a session (so we can get other signals from the caller)
        // and release resources upon call completion
        String id = pbxSession.getId();
        SipCallManagerSession session = new SipCallManagerSession(id,pbxSession,null,speechClient,telephonyClient);
        sipSessions.put(id, session);

        SipCallParameters parameters = new SipCallParameters();
        parameters.setSpeechClient(speechClient);
        
        
        final RemoteClient remote;
        try {
            remote = clientFactory.createRemoteClient(this, application, parameters);

            // Create a session and initiate a call at JVoiceXML.
            Session jsession = null;
            jsession = this.getJVoiceXml().createSession(remote);
            final URI uri = application.getUriObject();
            jsession.call(uri);
            synchronized (sessions) {
                sessions.put(terminal, jsession);
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
    public void startup() {
        // TODO Auto-generated method stub
        logger.info("startup mrcp sip callManager");
        sipSessions = new HashMap<String,SipCallManagerSession>();
        
    }
    
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        logger.info("shutdown mrcp sip callManager");
        
    }
}
