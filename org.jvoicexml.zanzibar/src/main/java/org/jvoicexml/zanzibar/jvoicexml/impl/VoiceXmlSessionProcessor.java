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
package org.jvoicexml.zanzibar.jvoicexml.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.media.rtp.InvalidSessionAddressException;
import javax.sip.SipException;

import org.apache.log4j.Logger;
import org.apache.mina.util.EventType;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.zanzibar.server.SpeechletServerMain;
import org.jvoicexml.zanzibar.speechlet.SessionProcessor;
import org.jvoicexml.zanzibar.speechlet.SpeechletContext;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.MrcpEvent;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.speechforge.cairo.sip.SipSession;

public class VoiceXmlSessionProcessor implements Runnable, SessionProcessor, SpeechEventListener{
    private static Logger _logger = Logger.getLogger(VoiceXmlSessionProcessor.class);
    //private SpeechClient client;
    //private SipSession session;
    private String appUrl;
    boolean stopFlag = false;
    Session jvxmlSession = null;
    private SpeechletContext _context;
	boolean instrumentation = false;
    
    
    //private static Map<String, VoiceXmlSessionProcessor> dialogs = new Hashtable<String, VoiceXmlSessionProcessor>();

    public String getId() {
        return _context.getExternalSession().getId();
    }

    /**
     * @return the client
     */
    public SpeechClient getClient() {
        return _context.getSpeechClient();
    }


    public void stop() throws SipException {
        jvxmlSession.hangup();
        _context.getInternalSession().bye();
    }

    public void recognitionEventReceived(MrcpEvent event, RecognitionResult r) {
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

    public void speechSynthEventReceived(MrcpEvent event) {
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

        ImplementationPlatform platform = (ImplementationPlatform) SpeechletServerMain.context.getBean("implementationplatform");
        //TODO: Fix the need to cast to the implementation and call setMRcpClient.  Maybe another i/f?...
        ((MrcpImplementationPlatform) platform).setMrcpClient(_context.getSpeechClient());
        JVoiceXmlCore core = (JVoiceXmlCore) SpeechletServerMain.context.getBean("jvoicexmlcore");
        jvxmlSession = new org.jvoicexml.interpreter.JVoiceXmlSession(platform, core, null, null);      
  
        
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
            //jvxmlSession.hangup();
        } catch (org.jvoicexml.event.JVoiceXMLEvent e ) {
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


    public void characterEventReceived(String c, EventType status) {
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
        return _context.getExternalSession();
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

	    public void characterEventReceived(String arg0, EventType arg1) {
		    // TODO Auto-generated method stub

	    }

	    public void recognitionEventReceived(MrcpEvent arg0, RecognitionResult arg1) {
	    	SipSession sipSession = _context.getExternalSession();
	    	try {
	            sipSession.getAgent().sendInfoRequest(sipSession, "application", "mrcp-event", arg0.toString());
            } catch (SipException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
	    }

	    public void speechSynthEventReceived(MrcpEvent arg0) {
	    	SipSession sipSession = _context.getExternalSession();
	    	try {
	            sipSession.getAgent().sendInfoRequest(sipSession, "application", "mrcp-event", arg0.toString());
            } catch (SipException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
	    }

		@Override
		public void recognitionEventReceived(SpeechEventType event, RecognitionResult r) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void speechSynthEventReceived(SpeechEventType event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void characterEventReceived(String c, DtmfEventType status) {
			// TODO Auto-generated method stub
			
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

	@Override
	public void recognitionEventReceived(SpeechEventType event, RecognitionResult r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void speechSynthEventReceived(SpeechEventType event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void characterEventReceived(String c, DtmfEventType status) {
		// TODO Auto-generated method stub
		
	}
	
}
