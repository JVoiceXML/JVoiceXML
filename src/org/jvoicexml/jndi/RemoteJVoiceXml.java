/*
 * File:    $RCSfile: RemoteJVoiceXml.java,v $
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

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;

/**
 * Remote interface to enable remote method calls betwenn
 * <code>JVoiceXmlSkeleton</code> and
 * <code>JVoiceXmlStub</code>.
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
 * @see org.jvoicexml.JVoiceXml
 * @see org.jvoicexml.jndi.JVoiceXmlSkeleton
 * @see org.jvoicexml.jndi.JVoiceXmlStub
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
     * session is obtained by a remote client.
     * </p>
     *
     * @param client
     *        The remote client that called the interpreter,
     *        maybe <code>null</code>. If it is <code>null</code> the
     *        default implementation platform is used.
     *
     * @return The new session.
     *
     * @see org.jvoicexml.ApplicationRegistry
     * @see org.jvoicexml.ImplementationPlatform
     *
     * @exception RemoteException
     *            Error creating the session.
     */
    Session createSession(final RemoteClient client)
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
