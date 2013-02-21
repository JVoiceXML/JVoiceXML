/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/config/IgnoringEntityResolver.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $LastChangedDate: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An {@link EntityResolver} that resolves all declared entities to nothing.
 * This can be useful if you do not have access to the referenced DTDs.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 * @version $LastChangedRevision: 2129 $
 *
 */
public final class IgnoringEntityResolver implements EntityResolver {
    /**
     * {@inheritDoc}
     */
    public InputSource resolveEntity(final String publicId,
            final String systemId)
            throws SAXException, IOException {
        final String empty = "";
        final StringReader reader = new StringReader(empty);
        return new InputSource(reader);
    }
}
