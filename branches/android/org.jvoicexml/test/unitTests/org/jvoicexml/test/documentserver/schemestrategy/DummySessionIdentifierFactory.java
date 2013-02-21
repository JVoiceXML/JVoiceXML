/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/test/documentserver/schemestrategy/DummySessionIdentifierFactory.java $
 * Version: $LastChangedRevision: 2830 $
 * Date:    $Date: 2011-09-23 06:04:56 -0500 (vie, 23 sep 2011) $
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

package org.jvoicexml.test.documentserver.schemestrategy;

import org.jvoicexml.documentserver.schemestrategy.SessionIdentifierFactory;

/**
 * Dummy implementation of a {@link SessionIdentifierFactory} for test purposes.
 * @author Dirk Schnelle
 * @version $Revision: 2830 $
 * @since 0.7
 */
public final class DummySessionIdentifierFactory
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
