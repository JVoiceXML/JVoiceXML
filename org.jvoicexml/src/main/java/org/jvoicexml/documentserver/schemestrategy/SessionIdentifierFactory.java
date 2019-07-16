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

import org.jvoicexml.SessionIdentifier;

/**
 * Factory for session identifiers that can be used in a {@link SessionStorage}.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @param <T> type of the session identifier
 */
public interface SessionIdentifierFactory<T> {
    /**
     * Creates a new session identifier for the {@link SessionStorage}.
     * @param sessionId the Id of the current JVoiceXML session
     * @return new session identifier.
     */
    T createSessionIdentifier(final SessionIdentifier sessionId);
}
