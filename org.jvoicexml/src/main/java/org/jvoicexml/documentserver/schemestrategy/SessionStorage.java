/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.documentserver.schemestrategy;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.SessionIdentifier;

/**
 * Container that associates a JVoiceXML session to a custom strategy object,
 * Identifying the session with the repository.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @param <T> type of the session identifier
 */
public class SessionStorage<T> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(SessionStorage.class);

    /** Association storage. */
    private final Map<SessionIdentifier, T> sessions;

    /** Factory for new session identifiers. */
    private final SessionIdentifierFactory<T> factory;

    /**
     * Constructs a new object.
     * @param identifierFactory the factory for new session identifiers.
     */
    public SessionStorage(final SessionIdentifierFactory<T> identifierFactory) {
        sessions = new java.util.HashMap<SessionIdentifier, T>();
        factory = identifierFactory;
    }

    /**
     * Retrieves the identifier for the given session.If no identifier exists
     * a new identifier is created using the {@link SessionIdentifierFactory}.
     * @param sessionId the Id of the JVoiceXML session
     * @return session identifier, <code>null</code> if the given session is
     *         <code>null</code>.
     */
    public synchronized T getSessionIdentifier(
            final SessionIdentifier sessionId) {
        if (sessionId == null) {
            LOGGER.warn("No session given. Unable to determine a session"
                    + " identifier");
            return null;
        }
        T identifier = sessions.get(sessionId);
        if (identifier == null) {
            identifier = factory.createSessionIdentifier(sessionId);
            sessions.put(sessionId, identifier);
        }
        return identifier;
    }

    /**
     * Removes the identifier from the list of known session identifiers.
     * @param sessionId the Id of the JVoiceXML session
     */
    public synchronized void releaseSession(final SessionIdentifier sessionId) {
        sessions.remove(sessionId);
    }
}
