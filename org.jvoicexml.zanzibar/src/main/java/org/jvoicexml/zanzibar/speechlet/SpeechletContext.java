/*
 * red5asr - Open source speech application server.
 *
 * Copyright (C) 2010 Spencer Lord 
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

public interface SpeechletContext  {
   

    public void init() throws InvalidContextException;
    
    /**
     * @return the speechClient
     */
    public SpeechClient getSpeechClient();

    /**
     * @return the telephonyClient
     */
    public TelephonyClient getTelephonyClient();
    
    /**
     * Notify the container that the speechlets Dialog has completed.  Typically the container will end the session(s) with the remote
     * party or parties (perhaps via SIP).  And return the resources that were allocated to this speechlet.
     * @throws InvalidContextException invalid context 
     */
    public void dialogCompleted() throws InvalidContextException;

	/**
     * @return the container
     */
    public SpeechletService getContainer();

	/**
     * @param container the container to set
     */
    public void setContainer(SpeechletService container);

	/**
     * @return the speechlet
     */
    public SessionProcessor getSpeechlet();

	/**
     * @param speechlet the speechlet to set
     */
    public void setSpeechlet(SessionProcessor speechlet);
    
	/**
	 * @return the externalSession
	 */
	public SipSession getPBXSession();

	/**
	 * @param externalSession the externalSession to set
	 */
	public void setPBXSession(SipSession externalSession);

}
