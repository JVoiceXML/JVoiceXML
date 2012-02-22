/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/RemoteJVoiceXml.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $Date: 2010-10-07 20:28:03 +0200 (Do, 07 Okt 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.event.ErrorEvent;

/**
 * Remote interface to enable remote method calls.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
 *
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
     * session is obtained using the information given.
     * </p>
     *
     * @param connectionInformation
     *        The connection information to use for this session,
     *        maybe <code>null</code>. If it is <code>null</code> the
     *        default implementation platform is used.
     *
     * @return The new session.
     *
     * @see org.jvoicexml.ImplementationPlatform
     *
     * @exception RemoteException
     *            Error creating the session.
     * @exception ErrorEvent
     *            Error creating the session.
     */
    RemoteSession createSession(
            final ConnectionInformation connectionInformation)
            throws ErrorEvent, RemoteException;

    /**
     * Shutdown the interpreter and free all resources.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void shutdown()
            throws RemoteException;

}
