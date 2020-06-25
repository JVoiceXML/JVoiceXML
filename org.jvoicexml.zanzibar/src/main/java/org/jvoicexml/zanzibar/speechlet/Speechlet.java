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

import javax.media.rtp.InvalidSessionAddressException;
import javax.sip.SipException;

import org.apache.log4j.Logger;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.speechforge.cairo.sip.SipSession;

/**
 * Speechlet abstract class.  handles basic starup and shutdown of a instance of a speech application
 * 
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 */
public abstract class Speechlet implements Runnable, SessionProcessor {

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

	protected static Logger _logger = Logger.getLogger(Speechlet.class);
    
    protected boolean stopFlag = false;
    
    private SpeechletContext _context;
    
	boolean instrumentation = false;
 
    /**
     * Instantiates a new speechlet.
     */
    public Speechlet() {
        super();
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#getId()
     */
    public String getId() {
        return this._context.getPBXSession().getId();
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#getClient()
     */
    public SpeechClient getClient() {
        return _context.getSpeechClient();
    }


    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#stop()
     */
    public void stop() throws SipException {
        stopFlag = true;
        //_context.getSpeechClient().hangup();
        if (_context instanceof SpeechletContextMrcpv2Impl)
        	((SpeechletContextMrcpv2Impl)_context).getMRCPv2Session().bye();
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#startup(org.speechforge.cairo.util.sip.SipSession)
     */
    public void startup(SpeechletContext context) throws Exception {
        startup(context,null);
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#startup(org.speechforge.cairo.util.sip.SipSession, java.lang.String)
     */
    public void startup(SpeechletContext context, String name) throws Exception {
        _logger.debug("Starting up a speechlet...");
        _context = context;
        _context.init();
        stopFlag = false;
        (new Thread(this)).start();
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#getSession()
     */
    public SipSession getSession() {
        return _context.getPBXSession();
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#getContext()
     */
    public SpeechletContext getContext() {
        return _context;
    }

    /* (non-Javadoc)
     * @see org.speechforge.modules.common.dialog.SessionProcessor#setContext(org.speechforge.modules.common.dialog.SpeechletContext)
     */
    public void setContext(SpeechletContext context) {
       _context = context;        
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
        } catch (InvalidSessionAddressException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();

	        
        }
    }
    
    /**
     * Run application.
     * @throws NoMediaControlChannelException no media control channel
     * @throws InvalidSessionAddressException  invalid session address
     */
    protected abstract void  runApplication() throws NoMediaControlChannelException, InvalidSessionAddressException;

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
    
}