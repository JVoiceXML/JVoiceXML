/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/ClearStrategy.java $
 * Version: $LastChangedRevision: 1013 $
 * Date:    $Date: 2008-07-17 07:44:58 +0200 (Do, 17 Jul 2008) $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Data;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.mozilla.javascript.Context;
import org.w3c.dom.Document;

/**
 * Strategy of the FIA to execute a <code>&lt;data&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Data
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1013 $
 * @since 0.7.1
 */
final class DataStrategy
        extends AbstractTagStrategy {
    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Data.ATTRIBUTE_SRCEXPR);
    }

    /** URI of the XML document to fetch. */
    private URI src;

    /** List of variables to submit. */
    private TokenList namelist;

    /** The request method to use. */
    private RequestMethod method;

    /**
     * Constructs a new object.
     */
    DataStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes()
            throws ErrorEvent {
        final String names = (String) getAttribute(Data.ATTRIBUTE_NAMELIST);
        namelist = new TokenList(names);

        final String requestMethod = (String) getAttribute(
                Data.ATTRIBUTE_METHOD);
        if (requestMethod == null) {
            method = RequestMethod.GET;
        } else if (RequestMethod.POST.getMethod().equalsIgnoreCase(
                requestMethod)) {
            method = RequestMethod.POST;
        } else if (RequestMethod.GET.getMethod().equalsIgnoreCase(
                requestMethod)) {
            method = RequestMethod.GET;
        } else {
            throw new SemanticError("Method must be one of '"
                    + RequestMethod.GET + "' or '" + RequestMethod.POST + "'!");
        }

        final boolean srcDefined = isAttributeDefined(Data.ATTRIBUTE_SRC);
        final boolean srcexprDefined =
            isAttributeDefined(Data.ATTRIBUTE_SRCEXPR);
        if (srcDefined == srcexprDefined) {
            throw new BadFetchError(
                    "Exactly one of 'src' or 'srcexpr' must be specified");
        }
        final String srcAttribute =
            (String) getAttribute(Data.ATTRIBUTE_SRC);
        if (srcAttribute != null) {
            try {
                src = new URI(srcAttribute);
            } catch (URISyntaxException e) {
                throw new SemanticError(
                        "'" + srcAttribute + "' is no valid uri!");
            }
            return;
        }
        final String srcExprAttribute =
            (String) getAttribute(Data.ATTRIBUTE_SRCEXPR);
        try {
            src = new URI(srcExprAttribute);
        } catch (URISyntaxException e) {
            throw new SemanticError(
                    "'" + srcExprAttribute + "' is no valid uri!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final DocumentServer server = context.getDocumentServer();
        final Session session = context.getSession();

        final DocumentDescriptor descriptor =
            new DocumentDescriptor(src, method);
        appendVariables(context, descriptor);

        final Document document =
            (Document) server.getObject(session, src, DocumentServer.TEXT_XML);
        final String name = (String) getAttribute(Data.ATTRIBUTE_NAME);
        if (name == null) {
            return;
        }
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable(name, document);
    }

    /**
     * Expand the variables of the namelist to the descriptor.
     *
     * @param context
     *        the current <code>VoiceXmlInterpreterContext</code>.
     * @param descriptor
     *        the document descriptor where to add the resolved parameters
     * @exception SemanticError
     *            A referenced variable is undefined
     */
    private void appendVariables(final VoiceXmlInterpreterContext context,
                                final DocumentDescriptor descriptor)
            throws SemanticError {
        final ScriptingEngine scripting = context.getScriptingEngine();

        for (String name : namelist) {
            final String value = (String) scripting.eval(name);
            if ((value == null) || (value == Context.getUndefinedValue())) {
                throw new SemanticError("'" + name + "' is undefined!");
            }

            if (value.startsWith("file:/")) {
                final File file = new File(value);
                descriptor.addParameter(name, file);
            } else {
                descriptor.addParameter(name, value);
            }
        }
    }
}
