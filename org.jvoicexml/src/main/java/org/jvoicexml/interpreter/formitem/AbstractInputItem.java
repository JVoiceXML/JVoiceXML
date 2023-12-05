/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.EventCountable;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Filled;

/**
 * Base methods of an {@link InputItem}.
 *
 * @author Dirk Schnelle-Walka
 */
abstract class AbstractInputItem extends AbstractFormItem implements InputItem {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(AbstractInputItem.class);

    /** The maintained event counter. */
    private final EventCountable eventCounter;

    /** The maintained prompt counter. */
    private int promptCounter;

    /**
     * Constructs a new object as a template.
     */
    AbstractInputItem() {
        super();
        eventCounter = null;
        promptCounter = 0;
    }

    /**
     * Create a new input item.
     *
     * @param context
     *            The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *            The corresponding XML node in the VoiceXML document.
     */
    AbstractInputItem(final VoiceXmlInterpreterContext context,
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
     *            Event to increment.
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
        eventCounter.resetEventCounter();
        if (LOGGER.isDebugEnabled()) {
            final String name = getName();
            LOGGER.debug("resetted event counter for input item '" + name
                    + "'...");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getPromptCount() {
        return promptCounter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void incrementPromptCount() {
        ++promptCounter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void resetPromptCount() {
        promptCounter = 1;
        if (LOGGER.isDebugEnabled()) {
            final String name = getName();
            LOGGER.debug("initialized prompt counter for input item '" + name
                    + "'");
        }
    }

    /**
     * Retrieve the counter for the given event type.
     *
     * @param type
     *            Event type.
     * @return Count for the given event type.
     */
    public final int getEventCount(final String type) {
        return eventCounter.getEventCount(type);
    }

    /**
     * Retrieves the name of the corresponding shadow var container name.
     * 
     * @return Name of the shadow var container.
     *
     * @since 0.3.1
     */
    protected final String getShadowVarContainerName() {
        final StringBuilder str = new StringBuilder();
        final String name = getName();
        str.append(name);
        str.append('$');
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Collection<Filled> getFilledElements() {
        final VoiceXmlNode node = getNode();
        if (node == null) {
            return null;
        }

        return node.getChildNodes(Filled.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final DataModel model)
            throws SemanticError, BadFetchError {
        // Create the variable
        final String name = getName();
        final Object expression = evaluateExpression(model);
        int rc = model.createVariable(name, expression);
        if (rc != DataModel.NO_ERROR) {
            LOGGER.warn("error creating variable '" + name + "'"
                    + model.errorCodeToString(rc));
        }
        final String shadowVariableName = getShadowVarContainerName();
        rc = model.createVariable(shadowVariableName);
        if (rc != DataModel.NO_ERROR) {
            LOGGER.warn("error creating shadowvariable '" + shadowVariableName
                    + "'" + model.errorCodeToString(rc));
        }
        LOGGER.info("initialized input form item '" + name + "' with '"
                + expression + "'");

        // Reset the counters
        resetPromptCount();
        resetEventCounter();
    }

}
