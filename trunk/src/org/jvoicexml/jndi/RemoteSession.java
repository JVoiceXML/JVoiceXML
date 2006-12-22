/*
 * File:    $RCSfile: RemoteSession.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jndi;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.CharacterInput;

/**
 * Remote interface to enable remote method calls betwennK
 * <code>SessionSkeleton</code> and
 * <code>SessionStub</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
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
     */
    void call(final URI uri)
            throws RemoteException;

    /**
     * Handles a hangup request.
     *
     * @exception RemoteException
     *            Error in remote method call.
     * @since 0.4
     */
    void hangup()
            throws RemoteException;

    /**
     * Retrieves the DTMF input device.
     * @return DTMF input device.
     * @exception RemoteException
     *            Error in remote method call.
     *
     * @since 0.5
     */
    CharacterInput getCharacterInput()
            throws RemoteException;

    /**
     * Delays until the session ends.
     * @exception RemoteException
     *            Error in remote method call.
     * @since 0.4
     */
    void waitSessionEnd()
            throws RemoteException;

    /**
     * Closes this session.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void close()
            throws RemoteException;
}
