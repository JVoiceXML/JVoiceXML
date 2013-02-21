/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Convenient class to access variables from a given {@link URI} or add
 * variables to it to send them back to the
 * {@link org.jvoicexml.DocumentServer}.
 *
 * <p>
 * Variables are appended to the given URI like the query string in a
 * <code>URL</code>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @see org.jvoicexml.DocumentServer
 * @see java.net.URL#getQuery()
 */
public final class VariableEncoder {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(VariableEncoder.class);

    /** The base URI. */
    private final URI base;

    /** The map of variables, with the name of the variable being key. */
    private final Map<String, String> variables;

    /** Encoding that should be used to encode/decode URLs. */
    private final String encoding;

    /**
     * Construct a new object.
     *
     * @param uri
     *        The base URI for encoding/decoding variables.
     */
    public VariableEncoder(final URI uri) {
        base = getBaseUri(uri);
        variables = getVariables(uri);
        encoding = System.getProperty("jvoicexml.xml.encoding", "UTF-8");
    }

    /**
     * Determine the base URI for the given URI. Remove a query string.
     *
     * @param uri
     *        The URI to analyze.
     * @return The base URI without any query parameters.
     */
    private URI getBaseUri(final URI uri) {
        final String str = uri.toString();
        final int queryPos = str.indexOf('?');
        if (queryPos < 0) {
            return uri;
        }

        try {
            return new URI(str.substring(0, queryPos));
        } catch (URISyntaxException use) {
            LOGGER.error("Unable to extract base uri", use);
        }

        return uri;
    }

    /**
     * Extracts the variables from a query string in the given URI.
     *
     * @param uri
     *        The uri to analyze.
     * @return Map of variables.
     */
    private Map<String, String> getVariables(final URI uri) {
        Map<String, String> vars = new java.util.HashMap<String, String>();

        final String str = uri.toString();
        final int queryPos = str.indexOf('?');
        if (queryPos < 0) {
            return vars;
        }

        final StringTokenizer tokenizer = new StringTokenizer(str, "&=");
        while (tokenizer.hasMoreTokens()) {
            final String name = tokenizer.nextToken();
            String value;

            if (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                try {
                    value = URLDecoder.decode(token, encoding);
                } catch (UnsupportedEncodingException ex) {
                    LOGGER.warn("unable to decode '" + token + "'", ex);
                    value = token;
                }
            } else {
                value = "";
            }

            vars.put(name, value);
        }

        return vars;
    }

    /**
     * Gets the currently defined variables and their values.
     *
     * @return Map of variable names and their values.
     */
    public Map<String, String> getVariables() {
        return variables;
    }

    /**
     * Adds a variable with the given name and value.
     *
     * @param name
     *        Name of the new variable.
     * @param value
     *        Value of the variable.
     */
    public void add(final String name, final String value) {
        if ((name == null) || (value == null)) {
            return;
        }

        variables.put(name, value);
    }

    /**
     * Gets an encoded URI, consisting of the base URI, and assigned variables.
     * Use <code>getBaseUri()</code> to retrieve the URI without any
     * variables.
     *
     * @return URI with variables.
     * @see #getBaseUri()
     */
    public URI toUri() {
        if (variables.isEmpty()) {
            return base;
        }

        final StringBuilder str = new StringBuilder();
        str.append(base.toString());
        str.append('?');

        final Iterator<String> iterator = variables.keySet().iterator();
        while (iterator.hasNext()) {
            final String name = iterator.next();
            str.append(name);
            str.append('=');
            final String value = variables.get(name);
            try {
                str.append(URLEncoder.encode(value, encoding));
            } catch (UnsupportedEncodingException ex) {
                LOGGER.warn("unable to encode '" + value + "'", ex);
                str.append(value);
            }

            if (iterator.hasNext()) {
                str.append('&');
            }
        }

        try {
            return new URI(str.toString());
        } catch (URISyntaxException use) {
            LOGGER.error("unable to create uri", use);

            return null;
        }
    }

    /**
     * Get the base URI without any parameters. Use <code>toUri()</code> to
     * retrieve the URI with assigned variables.
     *
     * @return Base URI without variables.
     *
     * @see #toUri()
     */
    public URI getBaseUri() {
        return base;
    }
}
