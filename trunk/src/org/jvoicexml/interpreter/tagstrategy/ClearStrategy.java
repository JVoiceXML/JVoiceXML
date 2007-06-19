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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Collection;

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.EventCountable;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.interpreter.formitem.PromptCountable;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Clear;
import org.mozilla.javascript.Context;

/**
 * Strategy of the FIA to execute a <code>&lt;clear&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Clear
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
final class ClearStrategy
        extends AbstractTagStrategy {
    /** Logger instance. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ClearStrategy.class);

    /** Names to clear. */
    private TokenList namelist;

    /**
     * Constructs a new object.
     */
    ClearStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes()
            throws ErrorEvent {
        final String names = (String) getAttribute(Clear.ATTRIBUTE_NAMELIST);

        if (names == null) {
            namelist = null;
        } else {
            namelist = new TokenList(names);
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
        if ((namelist == null) || namelist.isEmpty()) {
            clearAllFormItems(context, fia);
        } else {
            clearVariables(context, fia);
        }

    }

    /**
     * Clear all form items.
     * @param context The current VoiceXML interpreter context.
     * @param fia The current form interpretation algorithm
     *
     * @since 0.3.1
     */
    private void clearAllFormItems(final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia) {
        final ScriptingEngine scripting = context.getScriptingEngine();
        final Collection<FormItem> items = fia.getFormItems();

        for (FormItem item : items) {
            final String name = item.getName();
            scripting.setVariable(name, Context.getUndefinedValue());
            resetCounter(scripting, name);
        }
    }

    /**
     * Resets the counterif an {@link InputItem} exists.
     * @param scripting scripting engine.
     * @param name name of the variable.
     */
    private void resetCounter(final ScriptingEngine scripting,
            final String name) {
        /** @todo Remove the knowledge, that a shadow var ends with $. */
        final Object value = scripting.getVariable(name + "$");
        if (value instanceof EventCountable) {
            final EventCountable countable = (EventCountable) value;
            countable.resetEventCounter();
        }
        if (value instanceof PromptCountable) {
            final PromptCountable countable = (PromptCountable) value;
            countable.resetPromptCount();
        }
    }

    /**
     * Clear all specified variables.
     * @param context The current VoiceXML interpreter context.
     * @param fia The current form interpretation algorithm
     * @exception SemanticError
     *            A variable is not defined.
     * @since 0.3.1
     */
    private void clearVariables(final VoiceXmlInterpreterContext context,
                                final FormInterpretationAlgorithm fia)
            throws SemanticError {
        final ScriptingEngine scripting = context.getScriptingEngine();

        /** @todo If namelist is not specified: Clear all form items. */
        for (String name : namelist) {
            if (!scripting.isVariableDefined(name)) {
                throw new SemanticError("'" + name + "' is not defined!");
            }

            resetCounter(scripting, name);
            scripting.setVariable(name, Context.getUndefinedValue());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("cleared var '" + name + "'");
            }
        }
    }
}
