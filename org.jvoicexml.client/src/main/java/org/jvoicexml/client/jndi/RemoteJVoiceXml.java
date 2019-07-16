/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.jndi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;

/**
 * Remote interface to enable remote method calls to
 * {@link org.jvoicexml.JVoiceXml}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.4
 * @see org.jvoicexml.JVoiceXml
 */
public interface RemoteJVoiceXml
        extends Remote {
    /**
     * Retrieves the version information of JVoiceXml.
     * @return Version number.
     *
     * @since 0.4.1
     * @exception RemoteException
     *            Error in remote method call.
     */
    String getVersion()
            throws RemoteException;

    /**
     * Creates a new session.
     *
     * <p>
     * The <code>Session</code> is the entry point to start the interpreter. A
     * session is obtained by a client.
     * </p>
     *
     * @param connectionInformation
     *        The connection information to use in this session,
     *        maybe <code>null</code>. If it is <code>null</code> the
     *        default implementation platform is used.
     *
     * @return The new session.
     *
     * @see org.jvoicexml.ImplementationPlatform
     *
     * @exception RemoteException
     *            Error creating the session.
     */
    Session createSession(ConnectionInformation connectionInformation)
            throws RemoteException;

    /**
     * Creates a new session.
     *
     * <p>
     * The <code>Session</code> is the entry point to start the interpreter. A
     * session is obtained by a client.
     * </p>
     *
     * @param connectionInformation
     *        The connection information to use in this session,
     *        maybe <code>null</code>. If it is <code>null</code> the
     *        default implementation platform is used.
     *
     *@param id
     *          the session identifier
     *          
     * @return The new session.
     *
     * @see org.jvoicexml.ImplementationPlatform
     *
     * @exception RemoteException
     *            Error creating the session.
     */
    Session createSession(ConnectionInformation connectionInformation,
            SessionIdentifier id)
            throws RemoteException;

    /**
     * Shutdown the interpreter and free all resources.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void shutdown()
            throws RemoteException;

}
