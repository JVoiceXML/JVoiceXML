/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy.builtin;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Basic methods of a {@link GrammarCreator}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public abstract class AbstractGrammarCreator implements GrammarCreator {
    /**
     * Retrieves a byte representation of the given document.
     * @param document the document to convert to bytes.
     * @return byte representation of the document
     * @throws IOException
     *         error creating the byte representation
     * @since 0.7.5
     */
    public byte[] getBytes(final SrgsXmlDocument document) throws IOException {
        final String xml = document.toXml();
        return xml.getBytes();
    }

    /**
     * Retrieves the mode of the grammar.
     * @param uri the given URI.
     * @return the mode
     * @throws BadFetchError
     *         error parsing the URI
     */
    protected final ModeType getMode(final URI uri) throws BadFetchError {
        final String schemeSpecificPart = uri.getSchemeSpecificPart();
        final String[] path = schemeSpecificPart.split("/");
        final String modeSpecifier = path[0];
        final ModeType mode;
        try {
            final String str = modeSpecifier.toUpperCase();
            // Voice based grammars should start with grammar and not with
            // voice. A better choice would be to have voice vs. dtmf again,
            // but the spec is inconsistent in this case.
            if (str.equals("GRAMMAR")) {
                return ModeType.VOICE;
            }
            mode = ModeType.valueOf(str);
        } catch (IllegalArgumentException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        return mode;
    }

    /**
     * Retrieves the query parameters from the URI.
     * @param uri the given URI
     * @return parameters
     * @exception BadFetchError
     *            error parsing the query string
     */
    protected final Map<String, String> getParameters(final URI uri)
        throws BadFetchError {
        final String schemeSpecificPart = uri.getSchemeSpecificPart();
        final int pos = schemeSpecificPart.indexOf('?');
        if (pos < 0) {
            return new java.util.HashMap<String, String>();
        }
        final String query = schemeSpecificPart.substring(pos + 1);
        final String[] pairs = query.split(";");
        final Map<String, String> parameters =
            new java.util.HashMap<String, String>();
        for (String pair : pairs) {
            final String[] current = pair.split("=");
            if (current.length != 2) {
                throw new BadFetchError("Error parsing the parameters from '"
                        + uri + "'!");
            }
            final String key = current[0];
            final String value = current[1];
            parameters.put(key, value);
        }
        return parameters;
    }

}
