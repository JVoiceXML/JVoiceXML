/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.InternalExitEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Exit;

/**
 * Strategy of the FIA to execute a <code>&lt;exit&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Exit
 *
 * @author Dirk Schnelle-Walka
 */
final class ExitStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = 
            LogManager.getLogger(ExitStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Exit.ATTRIBUTE_EXPR);
    }

    /** Return value. */
    private String expr;

    /** Variable to return. */
    private TokenList namelist;

    /**
     * Constructs a new object.
     */
    ExitStrategy() {
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
    public void validateAttributes(final DataModel model) throws ErrorEvent {
        expr = (String) getAttribute(Exit.ATTRIBUTE_EXPR);
        final String names = (String) getAttribute(Exit.ATTRIBUTE_NAMELIST);
        if (names == null) {
            namelist = new TokenList();
        } else {
            namelist = new TokenList(names);
        }
        if ((expr != null) && (names != null)) {
            throw new BadFetchError("Exactly one of \"expr\" and \"namelist\" "
                    + " may be specified!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        if (expr != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("setting exit return value to '" + expr + "'");
            }

            throw new InternalExitEvent(expr);
        }

        // An empty namelist is the default behavior, so there is no
        // need to care about that issue.
        final Map<String, Object> variables = evaluateNames(context);
        throw new InternalExitEvent(variables);
    }

    /**
     * Evaluate the current values of the variables and store them in a map.
     * 
     * @param context
     *            The current VoiceXML interpreter context.
     * @return Map of variables and their values.
     * @exception SemanticError
     *                Error evaluating the variables.
     * @since 0.3
     */
    private Map<String, Object> evaluateNames(
            final VoiceXmlInterpreterContext context) throws SemanticError {
        final Map<String, Object> mappings = 
                new java.util.HashMap<String, Object>();
        final DataModel model = context.getDataModel();
        for (String name : namelist) {
            final Object value = model.readVariable(name, Object.class);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("adding return value '" + name + "': '" + value
                        + "'");
            }
            mappings.put(name, value);
        }

        return mappings;
    }
}
