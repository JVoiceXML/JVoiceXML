/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.Session;

/**
 * Container that associates a JVoiceXML session to a custom strategy object,
 * Identifying the session with the repository.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.7
 * @param <T> type of the session identifier
 */
public final class SessionStorage<T> {
    /** Association storage. */
    private final Map<Session, T> sessions;

    /** Factory for new session identifiers. */
    private final SessionIdentifierFactory<T> factory;

    /**
     * Constructs a new object.
     * @param identifierFactory the factory for new session identifiers.
     */
    public SessionStorage(final SessionIdentifierFactory<T> identifierFactory) {
        sessions = new java.util.HashMap<Session, T>();
        factory = identifierFactory;
    }

    /**
     * Retrieves the identifier for the given session.If no identifier exists
     * a new identifier is created using the {@link SessionIdentifierFactory}.
     * @param session the JVoiceXML session
     * @return session identifier, <code>null</code> if the given session is
     *         <code>null</code>.
     */
    public synchronized T getSessionIdentifier(final Session session) {
        if (session == null) {
            return null;
        }
        T identifier = sessions.get(session);
        if (identifier == null) {
            identifier = factory.createSessionIdentifier(session);
            sessions.put(session, identifier);
        }
        return identifier;
    }

    /**
     * Removes the identifier from the list of known session identifiers.
     * @param session the JVoiceXML session
     */
    public synchronized void releaseSession(final Session session) {
        sessions.remove(session);
    }
}
