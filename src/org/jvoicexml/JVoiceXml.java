/*
/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;


import org.jvoicexml.event.error.ErrorEvent;

/**
 * Main entry point for all clients.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.Session
 * @see org.jvoicexml.ApplicationRegistry
 * @see org.jvoicexml.DocumentServer
 * @see org.jvoicexml.ImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface JVoiceXml {
    /**
     * Retrieves the version information of JVoiceXml.
     * @return Version number.
     *
     * @since 0.4.1
     */
    String getVersion();

    /**
     * Creates a new session.
     *
     * <p>
     * The <code>Session</code> is the entry point to start the interpreter. A
     * session is obtained by a remote client and the id of an application
     * which has been registered at the <code>ApplicationRegistry</code>.
     * </p>
     *
     * @param client
     *        The remote client that called the interpreter,
     *        maybe <code>null</code>.
     * @param id
     *        Id of the application.
     *
     * @return The new session or <code>null</code> if
     *         <ol>
     *         <li>there is no application for the given application id,</li>
     *         <li>there is no free implementation platform or</li>
     *         <li>the VoiceXML interpreter was shut down.</li>
     *         </ol>
     *
     * @see org.jvoicexml.ApplicationRegistry
     * @see org.jvoicexml.ImplementationPlatform
     *
     * @exception ErrorEvent
     *            Error creating the session.
     */
    Session createSession(final RemoteClient client, final String id)
            throws ErrorEvent;

    /**
     * Shuts down the interpreter and frees all resources.
     */
    void shutdown();
}
