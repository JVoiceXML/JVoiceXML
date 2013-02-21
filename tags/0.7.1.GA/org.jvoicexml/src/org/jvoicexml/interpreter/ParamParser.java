/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.Session;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Param;
import org.jvoicexml.xml.vxml.ParamValueType;

/**
 * The <code>&lt;param&gt;</code> element is used to specify values that are
 * passed to subdialogs or objects.
 *
 * <p>
 * The <code>&lt;subdialog&gt;</code> and <code>&lt;object&gt;</code> tags may
 * have nested <code>&lt;param&gt;</code> tags to pass arguments. These are
 * evaluated by the {@link ParamParser} so that they can be passed directly to
 * the correspondent handler.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Param
 * @see org.jvoicexml.xml.vxml.Subdialog
 * @see org.jvoicexml.xml.vxml.ObjectTag
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
class ParamParser {
    /** The node containing param tags. */
    private final VoiceXmlNode node;

    /** The scripting engine for expression evaluation. */
    private final ScriptingEngine scripting;

    /** The document server to retrieve references values. */
    private final DocumentServer server;

    /** The current JVoiceXML session. */
    private final Session session;

    /**
     * Constructs a new object.
     * @param vxml
     *        the node to parse.
     * @param scriptingEngine
     *        the scripting engine to evaluate expressions.
     * @param documentServer
     *        the document server to retrieve ref values.
     * @param currentSession
     *        the current JVoiceXML session
     */
    public ParamParser(final VoiceXmlNode vxml,
            final ScriptingEngine scriptingEngine,
            final DocumentServer documentServer, final Session currentSession) {
        node = vxml;
        scripting = scriptingEngine;
        server = documentServer;
        session = currentSession;
    }

    /**
     * Retrieve all parameters defined in the current tag.
     * @return Mapping of parameter names to their values.
     * @throws SemanticError
     *         Error evaluating an expression of a <code>&lt;param&gt;</code>
     *         tag.
     * @throws BadFetchError
     *         A param tag features neither a value nor an expr attribute.
     */
    public Map<String, Object> getParameters()
        throws SemanticError, BadFetchError {
        final Collection<Param> paramtags = node.getChildNodes(Param.class);

        final Map<String, Object> parameters =
                new java.util.HashMap<String, Object>();

        for (Param param : paramtags) {
            final String name = param.getName();
            Object value = param.getValue();
            if (value == null) {
                final String expr = param.getExpr();
                if (expr == null) {
                    throw new BadFetchError("Exactly one of \"value\" or "
                            + "\"expr\" must be specified in a param tag!");
                }
                value = scripting.eval(expr);
            } else {
                final ParamValueType valueType = param.getValuetype();
                if (valueType == ParamValueType.REF) {
                    final URI uri;
                    try {
                        uri = new URI(value.toString());
                    } catch (URISyntaxException e) {
                        throw new BadFetchError("'" + value
                                + "' is not a valid URI");
                    }
                    final String type = param.getType();
                    final DocumentDescriptor descriptor =
                        new DocumentDescriptor(uri);
                    value = server.getObject(session, descriptor, type);
                }
            }
            parameters.put(name, value);
        }

        return parameters;
    }

    /**
     * Retrieve all parameters defined in the current tag.
     * @return collection of all parameters.
     * @throws SemanticError
     *         Error evaluating an expression of a <code>&lt;param&gt;</code>
     *         tag.
     * @throws BadFetchError
     *         A param tag features neither a value nor an expr attribute.
     */
    public Collection<Object> getParameterValues()
        throws SemanticError, BadFetchError {
        final Collection<Param> paramtags = node.getChildNodes(Param.class);

        final Collection<Object> parameters =
                new java.util.ArrayList<Object>();

        for (Param param : paramtags) {
            Object value = param.getValue();
            if (value == null) {
                final String expr = param.getExpr();
                if (expr == null) {
                    throw new BadFetchError("Exactly one of \"value\" or "
                            + "\"expr\" must be specified in a param tag!");
                }
                value = scripting.eval(expr);
            } else {
                final ParamValueType valueType = param.getValuetype();
                if (valueType == ParamValueType.REF) {
                    final URI uri;
                    try {
                        uri = new URI(value.toString());
                    } catch (URISyntaxException e) {
                        throw new BadFetchError("'" + value
                                + "' is not a valid URI");
                    }
                    final String type = param.getType();
                    final DocumentDescriptor descriptor =
                        new DocumentDescriptor(uri);
                    value = server.getObject(session, descriptor, type);
                }
            }
            parameters.add(value);
        }

        return parameters;
    }
}
