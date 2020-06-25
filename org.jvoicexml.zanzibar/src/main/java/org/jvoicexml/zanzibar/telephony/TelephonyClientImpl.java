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
package org.jvoicexml.zanzibar.telephony;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.event.HangupEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.jvoicexml.zanzibar.asterisk.AsteriskCallControl;
import org.jvoicexml.zanzibar.server.SpeechletServerMain;

// TODO: Auto-generated Javadoc
/**
 * Implementation of a telephony client.
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public class TelephonyClientImpl implements TelephonyClient , ManagerEventListener{
    
    /**
     * The _logger.
     */
    private static Logger _logger = Logger.getLogger(TelephonyClientImpl.class);
    

    /**
     * The _channel.	
     */
    private String _channel;
    
    /**
     * The _call completed.
     */
    private boolean _callCompleted = false;
    
    /**
     * The cc.
     */
    AsteriskCallControl cc = null;

    /**
     * Instantiates a new telephony client impl.
     * 
     * @param channel the channel
     */
    public TelephonyClientImpl(String channel) {
        super();
        if (channel != null)
           _channel = channel.trim();
         
        cc  =  (AsteriskCallControl) SpeechletServerMain.context.getBean("callControl");
        
    }

    /* (non-Javadoc)
     * @see org.speechforge.zanzibar.telephony.TelephonyClient#redirectBlocking(java.lang.String, java.lang.String, java.lang.String)
     */
    public void redirectBlocking(String channel, String connectContext, String connectTo) throws IOException, TimeoutException {
        
        cc.addEventListener(this);
        
        _callCompleted = false;
        cc.addEventListener(this);
        cc.amiRedirect(channel, connectContext, connectTo);
        
        //Wait for a hangup event and then transfer back        
         while (!_callCompleted) {
             synchronized (this) {        
                 try {
                     this.wait(1000);
                 } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
             }
         }
         
         cc.removeEventListener(this);
         _logger.info("Returning from the redirect blocking call");
    }
    
    
    /* (non-Javadoc)
     * @see org.speechforge.zanzibar.telephony.TelephonyClient#redirect(java.lang.String, java.lang.String, java.lang.String)
     */
    public void redirect(String channel, String connectContext, String connectTo) throws IOException, TimeoutException {
        cc.amiRedirect(channel, connectContext, connectTo);
    }


    /* (non-Javadoc)
     * @see org.asteriskjava.manager.ManagerEventListener#onManagerEvent(org.asteriskjava.manager.event.ManagerEvent)
     */
    public void onManagerEvent(ManagerEvent event) {
        _logger.debug(event.toString());
        
        _logger.info("Asterisk Event: "+event.getClass().getCanonicalName());
        if (event instanceof org.asteriskjava.manager.event.HangupEvent) {
            HangupEvent e = (HangupEvent) event;
            String channel = e.getChannel().trim();
            _logger.info("got a hangup on the channel: "+e.getChannel()+"|"+ _channel);

            if (channel.equals(_channel)) {
                synchronized (this) {
                    _callCompleted = true;
                    this.notifyAll();
                }

            }
        }
        
    } 

}
