/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.GrammarProcessor;

/**
 * View of the interpreter and components like the {@link CallManager} on the
 * main class.
 *
 * <p>
 * While {@link JVoiceXml} defines the view of {@link ConnectionInformation}s on
 * the main class, the interpreter needs more functionality.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.4.1
 */
public interface JVoiceXmlCore
        extends JVoiceXml {
    /**
     * Retrieves a reference to the document server.
     *
     * @return The document server.
     */
    DocumentServer getDocumentServer();

    /**
     * Retrieves a reference to the grammar processor.
     * @return The grammar processor.
     */
    GrammarProcessor getGrammarProcessor();

    /**
     * Retrieves the configuration object if any.
     * @return the configuration object, <code>null</code> if there is no
     * configuration available.
     * @since 0.7.4
     */
    Configuration getConfiguration();
    
    /**
     * Retrieves the implementation platform factory.
     * @return the implementation platform factory
     * @since 0.7.9
     */
    ImplementationPlatformFactory getImplementationPlatformFactory();

    /**
     * Creates a new session given the implementation platform.
     *
     * <p>
     * The {@link Session} is the entry point to start the interpreter. A
     * session is linked to resources identified according to the info provided
     * by {@link ConnectionInformation} and accessible in the provided
     * {@link ImplementationPlatform}.
     * </p>
     *
     * @param info
     *        information about the current connection,
     *        maybe <code>null</code>. If it is <code>null</code> the
     *        default implementation platform is used.
     * @param platform
     *        the implementation platform to use
     * @param id
     *        the session identifier
     * @return The new session.
     *
     * @exception ErrorEvent
     *            Error creating the session.
     */
    Session createSession(final ConnectionInformation info,
            final ImplementationPlatform platform, final SessionIdentifier id)
            throws ErrorEvent;
}
