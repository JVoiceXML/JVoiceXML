/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
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

package org.jvoicexml.interpreter.tagstrategy;

import java.net.URI;
import java.util.Collection;

import org.jvoicexml.documentserver.VariableEncoder;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
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
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 */
final class SubmitStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SubmitStrategy.class);

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
     * Submit the vars to the specified VoixeXML document.
     *
     * @todo Extend to process all settings.
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final URI nextUri = nextUri(context);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("submitting to uri '" + nextUri.toString() + "'...");
        }

        throw new GotoNextDocumentEvent(nextUri);
    }

    /**
     * Get the uri for the next document.
     *
     * @param context
     *        The VoiceXML interpreter context.
     * @return The <code>URI</code> of the next document.
     * @exception BadFetchError
     *            Syntax error in the URI.
     * @exception SemanticError
     *            A referenced variable is undefined.
     */
    private URI nextUri(final VoiceXmlInterpreterContext context)
            throws BadFetchError, SemanticError {
        final URI uri;
        try {
            uri = new URI(next);
        } catch (java.net.URISyntaxException use) {
            throw new BadFetchError(use);
        }

        return appendVariables(context, uri);
    }

    /**
     * Append the variables of the namelist to the uri.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param uri
     *        The uri of this node.
     * @return The URI with appended varibales.
     * @exception SemanticError
     *            A referenced variable is undefined
     */
    private URI appendVariables(final VoiceXmlInterpreterContext context,
                                final URI uri)
            throws SemanticError {
        final VariableEncoder encoder = new VariableEncoder(uri);
        final ScriptingEngine scripting = context.getScriptingEngine();

        for (String name : namelist) {
            final String value = (String) scripting.eval(name);

            if ((value == null) || (value == Context.getUndefinedValue())) {
                throw new SemanticError("'" + name + "' is undefined!");
            }

            encoder.add(name, value);
        }

        return encoder.toUri();
    }
}
