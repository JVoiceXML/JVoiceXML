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

import javax.sip.SipException;

import org.speechforge.cairo.sip.SipSession;
import org.speechforge.cairo.client.SpeechClient;


/**
 * An object that implements this interface can run in the speech client.  The application must receieve a valid session at startup time.  The session contains a
 * reference to a speech client (context).  An application name can be passed as well.  If it is it must be of the form apptype|appname.  If it is not the session
 * processor must have the name already set before calling setup (perhasp configured using spring dependancy injection).
 * 
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public interface SessionProcessor {

    /**
     * Gets the id of the speech application.
     * 
     * @return the id
     */
    public  String getId();

    /**
     * Gets the speech client context.
     * 
     * @return the client
     */
    public  SpeechClient getClient();

    /**
     * Stop the speech application.  Typical use is when the session is terminated by the remote party.  
     * 
     * @throws SipException the sip exception
     */
    public  void stop() throws SipException;

    /**
     * Startup.
     * 
     * @param context the speechlet context
     * @param applicationName the application name
     * 
     * @throws Exception the exception
     */
    public  void startup(SpeechletContext context,  String applicationName) throws Exception;
    

    /**
     * Startup the speech application.  If using this method to start a speech application, 
     * you must set the application name before calling this method using setter method (perhaps wired in using spring).
     * 
     * @param context the speechlet context
     * 
     * @throws Exception the exception
     */
    public  void startup(SpeechletContext context) throws Exception;
    
    /**
     * Gets the context.  This object is used to communicate with the speechlet container.
     * 
     * @return the context
     */
    public SpeechletContext getContext();
    
    /**
     * Sets the context.  This object is used to communicate with the speechlet container.
     * 
     * @param context the speechlet context
     */
    public void setContext(SpeechletContext context);
    
    /**
     * Gets the session.
     * 
     * @return the session
     */
    public SipSession getSession();
    
	/**
     * @return the instrumentation flag
     */
    public boolean isInstrumentation();

	/**
     * @param instrumentation a flag that enable insrumenation 
     * (sending mrcp events in sip info headers)
     */
    public void setInstrumentation(boolean instrumentation);

}