/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.client;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Remote interface to enable remote method calls.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 * @see org.jvoicexml.Session
 * @see org.jvoicexml.jndi.SessionSkeleton
 *
 * @todo Remote sessions will require a unique ID
 */
public interface RemoteSession
        extends Remote {
    /**
     * Handles a call request.
     *
     * <p>
     * Starts processing of the current application.
     * </p>
     *
     * @param uri URI of the first document to load.
     *
     * @exception RemoteException
     *            Error in remote method call.
     * @exception ErrorEvent
     *            Error initiating the call.
     */
    void call(final URI uri)
            throws RemoteException, ErrorEvent;

    /**
     * Retrieves the DTMF input device.
     * @return DTMF input device.
     * @exception RemoteException
     *            Error in remote method call.
     * @exception NoresourceError
     *            Input device is not available.
     *
     * @since 0.5
     */
    CharacterInput getCharacterInput()
            throws RemoteException, NoresourceError;

    /**
     * Delays until the session ends.
     * @exception RemoteException
     *            Error in remote method call.
     * @exception ErrorEvent
     *            Error waiting for the end of the call.
     * @since 0.4
     */
    void waitSessionEnd()
            throws RemoteException, ErrorEvent;

    /**
     * Retrieves an error, if any, that happened during call processing.
     * @return an error that happened during call processing, <code>null</code>
     *         if there was no error.
     * @exception RemoteException
     *            Error in remote method call.
     * @since 0.7
     */
    ErrorEvent getLastError()
        throws RemoteException;

    /**
     * Closes this session.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void hangup()
            throws RemoteException;
}
