/*
/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/KeyedPlatformPool.java $
 * Version: $LastChangedRevision: 110 $
 * Date:    $Date: 2006-09-04 09:27:06 +0200 (Mo, 04 Sep 2006) $
 * Author:  $LastChangedBy: schnelle $
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
 * @version $Revision: 1.27 $
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
     * session is obtained by a calling device and the id of an application
     * which has been registered at the <code>ApplicationRegistry</code>.
     * </p>
     *
     * @param call
     *        The calling device.
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
     * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
     *
     * @exception ErrorEvent
     *            Error creating the session.
     */
    Session createSession(final CallControl call, final String id)
            throws ErrorEvent;

    /**
     * Shutdown the interpreter and free all resources.
     */
    void shutdown();
}
