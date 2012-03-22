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

package org.jvoicexml.interpreter.formitem;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.CatchContainer;
import org.jvoicexml.interpreter.EventCountable;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.PromptCountable;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * This element controls the initial interaction in a mixed initiative form.
 * Its prompts should be written to encourage the user to say something matching
 * a form level grammar. When at least one input item variable is filled as a
 * result of recognition during an <code>&lt;initial&gt;</code> element, the
 * form item variable of <code>&lt;initial&gt;</code> becomes <code>true</code>,
 * thus removing it as an alternative for the FIA.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class InitialFormItem
        extends AbstractControlItem 
        implements CatchContainer, PromptCountable, EventCountable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(InitialFormItem.class);

    /** The maintained prompt counter. */
    private int promptCounter;

    /** The maintained event counter. */
    private final EventCountable eventCounter;


    /**
     * Create a new initial form item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding xml node in the VoiceXML document.
     */
    public InitialFormItem(final VoiceXmlInterpreterContext context,
                           final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
        eventCounter = new EventCounter();
        promptCounter = 1;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        visitor.visitInitialFormItem(this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModal() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPromptCount() {
        return promptCounter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementPromptCount() {
        ++promptCounter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPromptCount() {
        promptCounter = 1;
    }

    /**
     * Retrieve the counter for the given event type.
     *
     * @param type
     *        Event type.
     * @return Count for the given event type.
     */
    public int getEventCount(final String type) {
        return eventCounter.getEventCount(type);
    }

    /**
     * Increment counters for all events that have the same name as the given
     * event or have a name that is a prefix of the given event.
     *
     * @param event
     *        Event to increment.
     */
    public void incrementEventCounter(final JVoiceXMLEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("incrementing event counter for '" + getName()
                         + "'...");
        }

        eventCounter.incrementEventCounter(event);
    }

    /**
     * Reset the event counter.
     */
    public void resetEventCounter() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resetting event counter for initial item '"
                    + getName() + "'...");
        }

        eventCounter.resetEventCounter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ScriptingEngine scripting) throws SemanticError,
            BadFetchError {
        final String name = getName();
        final Object expression = evaluateExpression(scripting);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing form item '" + name + "'");
        }
        scripting.setVariable(name, expression);
        LOGGER.info("initialized initial form item '" + name + "' with '"
                + expression + "'");

        resetPromptCount();
        resetEventCounter();
    }
}
