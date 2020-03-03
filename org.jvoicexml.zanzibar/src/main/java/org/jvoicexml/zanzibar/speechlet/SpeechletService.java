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

/**
 * Interface that must be implemented by Container of Speech Applications.
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public interface SpeechletService {

    /**
     * Startup.  This method is called once at startup time.
     */
    public abstract void startup();

    /**
     * Shutdown.  This methods is called whens shutting down the speech application container
     */
    public abstract void shutdown();

    
    public void startNewMrcpDialog(SipSession pbxSession, SipSession mrcpSession) throws Exception;
    
    	  
    	/**
     * Stop dialog.
     * 
     * @param session the session
     * 
     * @throws SipException the sip exception
     */
    public abstract void stopDialog(SipSession session) throws SipException;

    /**
     * Dtmf.
     * 
     * @param session the session
     * @param code the dtmf code (0-9, *, #)
     */																																																									
    public void dtmf(SipSession session, char code);
    
    
}