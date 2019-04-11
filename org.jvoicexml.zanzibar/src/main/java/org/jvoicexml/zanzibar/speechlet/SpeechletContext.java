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

import org.jvoicexml.zanzibar.telephony.TelephonyClient;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.sip.SipSession;

public interface SpeechletContext {
   
    /**
     * Notify the container that the speechlets Dialog has completed.  Typically the container will end the session(s) with the remote
     * party or parties (perhaps via SIP).  And return the resources that were allocated to this speechlet.
     * @throws InvalidContextException 
     */
    public void dialogCompleted() throws InvalidContextException;

    /**
     * @return the externalSession
     */
    public SipSession getExternalSession();

    /**
     * @param externalSession the externalSession to set
     */
    public void setExternalSession(SipSession externalSession);

    /**
     * @return the internalSession
     */
    public SipSession getInternalSession();

    /**
     * @param internalSession the internalSession to set
     */
    public void setInternalSession(SipSession internalSession);
    
    public void init() throws InvalidContextException;
    
    /**
     * @return the speechClient
     */
    public SpeechClient getSpeechClient();

    /**
     * @return the telephonyClient
     */
    public TelephonyClient getTelephonyClient();
}
