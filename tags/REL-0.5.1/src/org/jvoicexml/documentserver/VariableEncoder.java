/*
 * File:    $RCSfile: VariableEncoder.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2005/12/13 08:28:24 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Convinient class to access variables from a given <code>URI</code> or add
 * variables to it to send them back to the <code>DocumentServer</code>.
 *
 * <p>
 * Variables are appended to the given URI like the query string in a
 * <code>URL</code>.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * @see org.jvoicexml.documentserver.DocumentServer
 * @see java.net.URL#getQuery()
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class VariableEncoder {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(VariableEncoder.class);

    /** The base URI. */
    private final URI base;

    /** The map of variables, with the name of the variable being key. */
    private final Map<String, String> variables;

    /**
     * Construct a new object.
     *
     * @param uri
     *        The base uri for encoding/decoding variables.
     */
    public VariableEncoder(final URI uri) {
        base = getBaseUri(uri);
        variables = getVariables(uri);
    }

    /**
     * Determine the base uri for the given uri. Remove a query sring.
     *
     * @param uri
     *        The uri to analyze.
     * @return The base uri without any query parameters.
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
     * Extract the variables from a query string in the given uri.
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
            final String value;

            if (tokenizer.hasMoreTokens()) {
                value = tokenizer.nextToken();
            } else {
                value = "";
            }

            vars.put(name, value);
        }

        return vars;
    }

    /**
     * Get the currently defined variables and their values.
     *
     * @return Map of variable names and their values.
     */
    public Map<String, String> getVariables() {
        return variables;
    }

    /**
     * Add a variable with the given name and value.
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
     * Get an encoded uri, consisting of the base uri, and assigned variables.
     * Use <code>getBaseUri()</code> to retrieve the uri without any
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
            str.append(variables.get(name));

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
     * Get the base uri without any parameters. Use <code>toUri()</code> to
     * retrieve the uri with assigned variables.
     *
     * @return Base URI without variables.
     *
     * @see #toUri()
     */
    public URI getBaseUri() {
        return base;
    }
}
