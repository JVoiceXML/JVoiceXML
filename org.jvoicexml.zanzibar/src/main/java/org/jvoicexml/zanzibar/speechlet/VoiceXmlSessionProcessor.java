/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.speechlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.media.rtp.InvalidSessionAddressException;
import javax.sip.SipException;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SessionListener;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.mrcpv2.Mrcpv2ConnectionInformation;
import org.jvoicexml.event.ErrorEvent;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.speechforge.cairo.sip.SipSession;

public class VoiceXmlSessionProcessor implements Runnable, SessionProcessor, SpeechEventListener, SessionListener{
    private static Logger _logger = Logger.getLogger(VoiceXmlSessionProcessor.class);
    //private SpeechClient client;
    //private SipSession session;
    private String appUrl;
    boolean stopFlag = false;
    Session jvxmlSession = null;
    private SpeechletContext _context;
	boolean instrumentation = false;
	private JVoiceXml jvxml;
    
    //private static Map<String, VoiceXmlSessionProcessor> dialogs = new Hashtable<String, VoiceXmlSessionProcessor>();

    public VoiceXmlSessionProcessor(JVoiceXml jvxml) {
		this.jvxml = jvxml;
	}
    
	public VoiceXmlSessionProcessor() {
	}
    
    public JVoiceXml getVxml() {
		return jvxml;
	}

	public void setVxml(JVoiceXml vxml) {
		this.jvxml = vxml;
	}
    
	public String getId() {
        return _context.getPBXSession().getId();
    }

    /**
     * @return the client
     */
    public SpeechClient getClient() {
        return _context.getSpeechClient();
    }


    public void stop() throws SipException {
        jvxmlSession.hangup();
        if (_context instanceof SpeechletContextMrcpv2Impl)
        	((SpeechletContextMrcpv2Impl)_context).getMRCPv2Session().bye();

    }

    public void recognitionEventReceived(SpeechEventType event, RecognitionResult r) {
        _logger.debug("Recog result: "+r.getText());
        try {
            _context.getSpeechClient().playBlocking(false,r.getText());
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
        } catch (InvalidSessionAddressException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }       
    }

    public void speechSynthEventReceived(SpeechEventType event) {
        // TODO Auto-generated method stub       
    }
    
    
    //TODO remove this.  
	public void startup(SpeechletContext context, String applicationName) throws Exception {
		
		//overriding the application name in the startup method
		this.appUrl = applicationName;
        startup(context);   
    }
    
    public void startup(SpeechletContext context) throws Exception {
    	
        _logger.debug("Starting up a speechlet...");
        _context = context;
        _context.init();
        stopFlag = false;
        (new Thread(this)).start();
    }
    
    public void runApplication() throws Exception {

        stopFlag = false;        

        /*  OLD CODE
        ImplementationPlatform platform = (ImplementationPlatform) SpeechletServerMain.context.getBean("implementationplatform");
        //TODO: Fix the need to cast to the implementation and call setMRcpClient.  Maybe another i/f?...
        ((MrcpImplementationPlatform) platform).setMrcpClient(_context.getSpeechClient());
        JVoiceXmlCore core = (JVoiceXmlCore) SpeechletServerMain.context.getBean("jvoicexmlcore");
        RemoteClient dummyClient = null;
		jvxmlSession = new org.jvoicexml.interpreter.JVoiceXmlSession(platform, core, dummyClient);      
  
        
        URI uri = null;
        try {
            _logger.info("app name:"+this.appUrl);
           uri = new URI(this.appUrl);
        } catch ( URISyntaxException e ) {
           e.printStackTrace();
        }
        try {
            jvxmlSession.call(uri);
            jvxmlSession.waitSessionEnd();
            _context.dialogCompleted();
            //jvxmlSession.hangup();
        } catch (org.jvoicexml.event.JVoiceXMLEvent e ) {
           e.printStackTrace();
        }
        END OLD CODE */
        
        String calledNumber ="123";
        String callingNumber ="456";
        //Address remoteParty = dialog.getRemoteParty();
        //String callingNumber = remoteParty.getURI().toString();
        URI calledDevice = new URI(calledNumber);
        URI callingDevice = new URI(callingNumber);
        final Mrcpv2ConnectionInformation remote = 
            new Mrcpv2ConnectionInformation(callingDevice, calledDevice);
//        speechClient = _context.getSpeechClient();
		remote.setTtsClient(_context.getSpeechClient());
        remote.setAsrClient(_context.getSpeechClient());

        // Create a jvoicxml session and initiate a call at JVoiceXML.
        // TODO use the SipSessionIdentifier
        final SessionIdentifier identifier = new UuidSessionIdentifier();
        Session jsession = null;
		try {
			jsession = jvxml.createSession(remote, identifier);
		} catch (ErrorEvent e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        //add a listener to capture the end of voicexml session event
        jsession.addSessionListener(this);
        
        //add the jvoicexml session to the session bag
       // session.setJvxmlSession(jsession);
       // synchronized (sessions) {
       //     sessions.put(id, session);
       // }
        
        //workaround to deal with two id's
        //maps the voicexml sessionid to sip session id 
        //needed for case when the voicxml session ends before a hang up
        // and need to get to close the sip session
       // synchronized (ids) {
       //    ids.put(jsession.getSessionID(), id);
       // }

        URI uri = null;
        try {
            _logger.info("app name:"+this.appUrl);
           uri = new URI(this.appUrl);
        } catch ( URISyntaxException e ) {
           e.printStackTrace();
        }
        try {
			jsession.call(uri);
		} catch (ErrorEvent e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

     
    }

    /**
     * @return the appUrl
     */
    public String getAppUrl() {
        return appUrl;
    }

    /**
     * @param appUrl the appUrl to set
     */
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }


    public void characterEventReceived(String c, DtmfEventType status) {
        // TODO Auto-generated method stub
        _logger.info("Character Event! status= "+ status+" code= "+c);
        
    }
    
    public SpeechletContext getContext() {
        return _context;
    }

    public void setContext(SpeechletContext context) {
       _context = context;        
    }

    public SipSession getSession() {
        return _context.getPBXSession();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
        	
			if (instrumentation) {
        	   //setup instrumentation hooks.  Send the mrcp events to the client (phone) via SIP INFO requests.
        	   _context.getSpeechClient().addListener(new InstrumentationListener()); 
			}
			
	        runApplication();
        } catch (NoMediaControlChannelException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }



    public class InstrumentationListener implements SpeechEventListener {

	    public void characterEventReceived(String arg0, DtmfEventType arg1) {
		    // TODO Auto-generated method stub

	    }

	    public void recognitionEventReceived(SpeechEventType arg0, RecognitionResult arg1) {
	    	SipSession sipSession = _context.getPBXSession();
	    	try {
	            sipSession.getAgent().sendInfoRequest(sipSession, "application", "mrcp-event", arg0.toString());
            } catch (SipException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
	    }

	    public void speechSynthEventReceived(SpeechEventType arg0) {
	    	SipSession sipSession = _context.getPBXSession();
	    	try {
	            sipSession.getAgent().sendInfoRequest(sipSession, "application", "mrcp-event", arg0.toString());
            } catch (SipException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
	    }

    }

	/**
     * @return the instrumentation
     */
    public boolean isInstrumentation() {
    	return instrumentation;
    }

	/**
     * @param instrumentation the instrumentation to set
     */
    public void setInstrumentation(boolean instrumentation) {
    	this.instrumentation = instrumentation;
    }
    



	public void sessionEnded(Session arg0) {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * Retrieves the reference to the interpreter.
     * @return the interpreter
     */
    public JVoiceXml getJvxml() {
        return jvxml;
    }

    /**
     * {@inheritDoc}
     */
    public void setJvxml( JVoiceXml jvxml) {
       this.jvxml = jvxml;
    }

	@Override
	public void sessionStarted(Session session) {
		// TODO Auto-generated method stub
		
	}
	
}
