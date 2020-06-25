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

import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.TimeoutException;
import org.jvoicexml.zanzibar.telephony.TelephonyClient;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.header.IllegalValueException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.speechforge.cairo.sip.SipSession;

// TODO: Auto-generated Javadoc
/**
 * Speechlet Dialog interface.
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public interface SpeechletDialog {

    /**
     * Run dialog.
     * 
     * @param sClient the Speech client
     * @param tClient the Telephony client
     * @param session the SIP session
     * 
     * @return the recognition result
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws MrcpInvocationException the mrcp invocation exception
     * @throws InterruptedException the interrupted exception
     * @throws IllegalValueException the illegal value exception
     * @throws AuthenticationFailedException the authentication failed exception
     * @throws TimeoutException the timeout exception
     */
    public  RecognitionResult runDialog(SpeechClient sClient, TelephonyClient tClient,
            SipSession session) throws IOException, MrcpInvocationException,
            InterruptedException, IllegalValueException, AuthenticationFailedException, TimeoutException, 
            NoMediaControlChannelException;

    /**
     * Checks if is session terminated.
     * 
     * @return the sessionTerminated
     */
    public  boolean isSessionTerminated();

    /**
     * Checks if is help requested.
     * 
     * @return true, if is help requested
     */
    public  boolean isHelpRequested();
    
    /**
     * Checks if is call redirected.
     * 
     * @return true, if is call redirected
     */
    public boolean isCallRedirected();

}