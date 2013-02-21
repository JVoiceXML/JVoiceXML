/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Strategy of the FIA to execute a <code>&lt;submit&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Submit
 *
 * @author Dirk Schnelle
 * @version $Revision$
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
    private String next;

    /** List of variables to submit. */
    private TokenList namelist;

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

        next = (String) getAttribute(Submit.ATTRIBUTE_NEXT);
        if (next != null) {
            return;
        }

        if (isAttributeDefined(Submit.ATTRIBUTE_EXPR)) {
            next = (String) getAttribute(Submit.ATTRIBUTE_EXPR);
        }

        if (next == null) {
            throw new BadFetchError("exactly one of \"next\" or \"expr\" must "
                                    + "be specified!");
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
        final URI uri;
        try {
            uri = new URI(next);
        } catch (java.net.URISyntaxException use) {
            throw new BadFetchError(use);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("submitting to uri '" + uri.toString() + "'...");
        }
        final Submit submit = (Submit) node;
        RequestMethod method = submit.getMethod();
        if (method == null) {
            method = RequestMethod.GET;
        }
        final DocumentDescriptor descriptor =
            new DocumentDescriptor(uri, method);
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
