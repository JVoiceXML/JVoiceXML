/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import java.net.URI;
import java.util.Map;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Basic methods of a {@link GrammarCreator}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public abstract class AbstractGrammarCreator implements GrammarCreator {
    /**
     * Retrieves the mode of the grammar.
     * @param uri the given URI.
     * @return the mode
     * @throws BadFetchError
     *         error parsing the URI
     */
    protected final ModeType getMode(final URI uri) throws BadFetchError {
        final String host = uri.getHost();
        final ModeType mode;
        try {
            final String str = host.toUpperCase();
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
        final String query = uri.getQuery();
        if (query == null) {
            return new java.util.HashMap<String, String>();
        }
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
