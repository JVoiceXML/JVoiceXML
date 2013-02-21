/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/SubmitStrategy.java $
 * Version: $LastChangedRevision: 2715 $
 * Date:    $LastChangedDate: 2011-06-21 12:23:54 -0500 (mar, 21 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.SubmitEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.jvoicexml.xml.vxml.Submit;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * Strategy of the FIA to execute a <code>&lt;submit&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Submit
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2715 $
 */
final class SubmitStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SubmitStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Submit.ATTRIBUTE_EXPR);
    }

    /** The target of the submit. */
    private URI next;

    /** List of variables to submit. */
    private TokenList namelist;

    /** The request method to use. */
    private RequestMethod method;

    /**
     * Constructs a new object.
     */
    SubmitStrategy() {
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
        final String names = (String) getAttribute(Submit.ATTRIBUTE_NAMELIST);
        namelist = new TokenList(names);

        final String requestMethod = (String) getAttribute(
                Submit.ATTRIBUTE_METHOD);
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

        final boolean srcDefined = isAttributeDefined(Submit.ATTRIBUTE_NEXT);
        final boolean srcexprDefined =
            isAttributeDefined(Submit.ATTRIBUTE_EXPR);
        if (srcDefined == srcexprDefined) {
            throw new BadFetchError(
                    "Exactly one of 'next' or 'expr' must be specified");
        }

        final String nextAttribute =
            (String) getAttribute(Submit.ATTRIBUTE_NEXT);
        if (nextAttribute != null) {
            try {
                next = new URI(nextAttribute);
            } catch (URISyntaxException e) {
                throw new SemanticError(
                        "'" + nextAttribute + "' is no valid uri!");
            }
            return;
        }

        final String exprAttribute =
            (String) getAttribute(Submit.ATTRIBUTE_EXPR);
        try {
            next = new URI(exprAttribute);
        } catch (URISyntaxException e) {
            throw new SemanticError(
                    "'" + exprAttribute + "' is no valid uri!");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Submits the vars to the specified VoixeXML document.
     *
     * @todo Extend to process all settings.
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("submitting to uri '" + next.toString() + "'...");
        }
        final DocumentDescriptor descriptor =
            new DocumentDescriptor(next, method, true);
        appendVariables(context, descriptor);
        throw new SubmitEvent(descriptor);
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
            final Object value = scripting.eval(name + ";");
            if ((value == null) || (value == Context.getUndefinedValue())) {
                throw new SemanticError("'" + name + "' is undefined!");
            }
            final String str = value.toString();
            if (str.startsWith("file:/")) {
                final File file = new File(str);
                descriptor.addParameter(name, file);
            } else if (value instanceof ScriptableObject) {
                descriptor.addParameter(name, value);
            } else {
                descriptor.addParameter(name, str);
            }
        }
    }
}
