/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/SessionIdentifierFactory.java $
 * Version: $LastChangedRevision: 2839 $
 * Date:    $Date: 2011-10-13 09:33:06 +0200 (Thu, 13 Oct 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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


/**
 * Factory for session identifiers that can be used in a {@link SessionStorage}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2839 $
 * @since 0.7
 * @param <T> type of the session identifier
 */
public interface SessionIdentifierFactory<T> {
    /**
     * Creates a new session identifier.
     * @param sessionId the Id of the current JVoiceXML session
     * @return new session identifier.
     */
    T createSessionIdentifier(final String sessionId);
}
