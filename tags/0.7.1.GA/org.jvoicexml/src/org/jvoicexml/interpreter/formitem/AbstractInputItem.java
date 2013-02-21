/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.formitem;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.EventCountable;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Filled;

/**
 * Base methods of an {@link InputItem}.
 *
 * @version $Revision$
 */
abstract class AbstractInputItem
        extends AbstractFormItem implements InputItem {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractInputItem.class);

    /** The maintained event counter. */
    private final EventCountable eventCounter;

    /** The maintained prompt counter. */
    private int promptCounter;

    /**
     * Create a new input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding XML node in the VoiceXML document.
     */
    public AbstractInputItem(final VoiceXmlInterpreterContext context,
                     final VoiceXmlNode voiceNode) {
        super(context, voiceNode);

        eventCounter = new EventCounter();
        promptCounter = 1;
    }

    /**
     * Increment counters for all events that have the same name as the given
     * event or have a name that is a prefix of the given event.
     *
     * @param event
     *        Event to increment.
     */
    public final void incrementEventCounter(final JVoiceXMLEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("incrementing event counter for '" + getName()
                         + "'...");
        }

        eventCounter.incrementEventCounter(event);
    }

    /**
     * Reset the event counter.
     */
    public final void resetEventCounter() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resetting event counter for input item '" + getName()
                         + "'...");
        }

        eventCounter.resetEventCounter();
    }

    /**
     * {@inheritDoc}
     */
    public final int getPromptCount() {
        return promptCounter;
    }

    /**
     * {@inheritDoc}
     */
    public final void incrementPromptCount() {
        ++promptCounter;
    }

    /**
     * {@inheritDoc}
     */
    public final void resetPromptCount() {
        promptCounter = 1;
    }

    /**
     * Retrieve the counter for the given event type.
     *
     * @param type
     *        Event type.
     * @return Count for the given event type.
     */
    public final int getEventCount(final String type) {
        return eventCounter.getEventCount(type);
    }

    /**
     * Retrieves the name of the corresponding shadow var container name.
     * @return Name of the shadow var container.
     *
     * @since 0.3.1
     */
    protected final String getShadowVarContainerName() {
        final String name = getName();

        final StringBuilder str = new StringBuilder();
        str.append(name);
        str.append('$');

        return str.toString();
    }

    /**
     * Retrieves the implementation of the shadow var container for this
     * input item.
     * @return Class of the shadow var container, <code>null</code> if there
     *         is no shadow var container.
     *
     * @since 0.3.1
     */
    protected abstract Class<?> getShadowVariableContainer();

    /**
     * Creates a corresponding shadow var container.
     *
     * @return The created host object.
     * @throws SemanticError
     *         Error creating a host object.
     *
     * @since 0.5.5
     */
    protected final Object createShadowVarContainer()
            throws SemanticError {
        final Class<?> shadowVarContainer = getShadowVariableContainer();
        if (shadowVarContainer == null) {
            return null;
        }
        final String shadowVarContainerName = getShadowVarContainerName();
        final VoiceXmlInterpreterContext context = getContext();
        final ScriptingEngine scripting = context.getScriptingEngine();

        return scripting.createHostObject(shadowVarContainerName,
                                          shadowVarContainer);
    }

    /**
     * {@inheritDoc}
     */
    public final Collection<Filled> getFilledElements() {
        final VoiceXmlNode node = getNode();
        if (node == null) {
            return null;
        }

        return node.getChildNodes(Filled.class);
    }
}
