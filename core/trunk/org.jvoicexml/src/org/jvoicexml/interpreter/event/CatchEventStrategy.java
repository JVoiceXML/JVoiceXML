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

package org.jvoicexml.interpreter.event;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.FilledMode;

/**
 * Strategy to execute a user defined catch node.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @see org.jvoicexml.ImplementationPlatform
 * @see org.jvoicexml.xml.vxml.AbstractCatchElement
 */
final class CatchEventStrategy
        extends AbstractEventStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(CatchEventStrategy.class);

    /**
     * Constructs a new object.
     */
    CatchEventStrategy() {
    }

    /**
     * Constructs a new object.
     *
     * @param ctx
     *        the VoiceXML interpreter context.
     * @param ip
     *        the VoiceXML interpreter.
     * @param interpreter
     *        the FIA.
     * @param formItem
     *        the current form item.
     * @param node
     *        the node to execute.
     * @param type
     *        the event type.
     */
    public CatchEventStrategy(final VoiceXmlInterpreterContext ctx,
                                  final VoiceXmlInterpreter ip,
                                  final FormInterpretationAlgorithm interpreter,
                                  final FormItem formItem,
                                  final VoiceXmlNode node, final String type) {
        super(ctx, ip, interpreter, formItem, node, type);
    }

    /**
     * {@inheritDoc}
     */
    public void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        if (fia == null) {
            LOGGER.warn("Unable to process event '"
                    + event.getEventType()
                    + "' No reference to a form FIA!");
            return;
        }
        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        context.enterScope(Scope.ANONYMOUS);

        // Declare the special variable _event which contains the name of the
        // event that was thrown.
        final ScriptingEngine scripting = context.getScriptingEngine();
        final String name = event.getEventType();
        scripting.setVariable("_event", name);

        final VoiceXmlInterpreter interpreter = getVoiceXmlInterpreter();
        final TagStrategyExecutor executor = getTagStrategyExecutor();

        try {
            final FormItem item = getFormItem();
            final VoiceXmlNode node = getVoiceXmlNode();
            executor.executeChildNodes(context, interpreter, fia, item, node);
        } finally {
            context.exitScope(Scope.ANONYMOUS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActiveBySpecialRule() throws SemanticError {
        final String type = getEventType();
        if (!Filled.TAG_NAME.equals(type)) {
            return true;
        }
        final Filled filled = (Filled) getVoiceXmlNode();
        final FilledMode mode = filled.getModeObject();
        final FormInterpretationAlgorithm fia =
            getFormInterpretationAlgorithm();
        final Collection<InputItem> items = fia.getJustFilled();
        final TokenList tokens = filled.getNameListObject();
        final Collection<FormItem> formItems = fia.getFormItems();
        if (tokens.isEmpty()) {
            for (FormItem formItem : formItems) {
                if (formItem instanceof InputItem) {
                    final String name = formItem.getName();
                    tokens.add(name);
                }
            }
        }
        // TODO check if control items are references
        if (mode == FilledMode.ALL) {
            return areAllFilled(tokens, items);
        } else {
            return isAnyFilled(tokens, items);
        }
    }

    /**
     * Checks if all of the tokens are contained in the just filled items.
     * @param tokens tokens to be processed.
     * @param items the just filled input items
     * @return <code>true</code> if all input items are filled
     * @since 0.7.3
     */
    private boolean areAllFilled(final TokenList tokens,
            final Collection<InputItem> items) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if all input items of '" + tokens
                    + "' are filled");
        }
        if (tokens.size() != items.size()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sizes are different");
            }
            return false;
        }
        for (InputItem item : items) {
            final String name = item.getName();
            if (!tokens.contains(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("'" + name + "' is not present in namelist");
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if any of the tokens are contained in the just filled items.
     * @param tokens tokens to be processed.
     * @param items the just filled input items
     * @return <code>true</code> if any input items are filled
     * @since 0.7.3
     */
    private boolean isAnyFilled(final TokenList tokens,
            final Collection<InputItem> items) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if any input items of '" + tokens
                    + "' are filled");
        }
        for (InputItem item : items) {
            final String name = item.getName();
            if (tokens.contains(name)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("'" + name + "' is present in namelist");
                }
                return true;
            }
        }
        return false;
    }
}
