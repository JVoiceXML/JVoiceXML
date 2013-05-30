/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.mock.documentserver.schemestrategy;

import org.jvoicexml.documentserver.schemestrategy.SessionIdentifierFactory;

/**
 * Dummy implementation of a {@link SessionIdentifierFactory} for test purposes.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.7
 */
public final class MockSessionIdentifierFactory
    implements SessionIdentifierFactory<String> {
    /** Counter for session identifiers. */
    private static int count;

    /**
     * {@inheritDoc}
     */
    @Override
    public String createSessionIdentifier(final String sessionId) {
        ++count;
        return "identifier" + Integer.toString(count);
    }
}