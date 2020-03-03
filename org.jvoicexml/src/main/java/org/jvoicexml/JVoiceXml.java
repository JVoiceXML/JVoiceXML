/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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


import org.jvoicexml.event.ErrorEvent;

/**
 * Main entry point for all {@link ConnectionInformation}s.
 *
 * @author Dirk Schnelle-Walka
 * @see org.jvoicexml.Session
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
     * The {@link Session} is the entry point to start the interpreter. A
     * session is linked to resources identified according to the info provided
     * by {@link ConnectionInformation}.
     *
     * @param info
     *        information about the current connection,
     *        maybe <code>null</code>. If it is <code>null</code> the
     *        default implementation platform is used.
     *
     * @param id 
     *          the session identifier to use.
     * @return The new session.
     *
     * @exception ErrorEvent
     *            Error creating the session.
     */
    Session createSession(final ConnectionInformation info,
            final SessionIdentifier id)
            throws ErrorEvent;
    
    /**
     * Shuts down the interpreter and frees all resources.
     */
    void shutdown();
}
