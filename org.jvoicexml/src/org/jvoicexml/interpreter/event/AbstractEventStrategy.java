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

package org.jvoicexml.interpreter.event;

import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.AbstractFormItem;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Catch;

/**
 * Basic methods of an {@link EventStrategy} that can be processed by the
 * {@link JVoiceXmlEventHandler}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 */
public abstract class AbstractEventStrategy implements EventStrategy {
    /** The VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private final VoiceXmlInterpreter interpreter;

    /** The current FIA. */
    private final FormInterpretationAlgorithm fia;

    /** The current form item. */
    private final AbstractFormItem item;

    /** The child node with which to continue. */
    private final VoiceXmlNode node;

    /** The event type. */
    private final String event;

    /** The count. */
    private final int count;

    /**
     * Constructs a new object.
     */
    AbstractEventStrategy() {
        context = null;
        interpreter = null;
        fia = null;
        item = null;
        node = null;
        event = null;
        count = 0;
    }

    /**
     * Construct a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param ip
     *        The VoiceXML interpreter.
     * @param algorithm
     *        The FIA.
     * @param formItem
     *        The current form item.
     * @param n
     *        The child node with which to continue.
     * @param type
     *        The event type.
     */
    protected AbstractEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter ip,
                                    final FormInterpretationAlgorithm algorithm,
                                    final AbstractFormItem formItem,
                                    final VoiceXmlNode n, final String type) {
        context = ctx;
        interpreter = ip;
        fia = algorithm;
        item = formItem;
        node = n;
        event = type;

        if (node == null) {
            count = 1;
        } else {
            final String countAttribute =
                    node.getAttribute(Catch.ATTRIBUTE_COUNT);
            if (countAttribute == null) {
                count = 1;
            } else {
                count = Integer.valueOf(countAttribute);
            }
        }
    }

    /**
     * Retrieve the context property.
     *
     * @return The VoiceXML interpreter context.
     */
    protected final VoiceXmlInterpreterContext getVoiceXmlInterpreterContext() {
        return context;
    }

    /**
     * Retrieve the interpreter property.
     *
     * @return The VoiceXML interpreter.
     */
    protected final VoiceXmlInterpreter getVoiceXmlInterpreter() {
        return interpreter;
    }

    /**
     * Retrieve the FIA.
     *
     * @return The current FIA.
     */
    protected final FormInterpretationAlgorithm
            getFormInterpretationAlgorithm() {
        return fia;
    }

    /**
     * Retrieves the current form item.
     *
     * @return The current form item.
     */
    protected final AbstractFormItem getFormItem() {
        return item;
    }

    /**
     * Retrieves the child node with which to continue.
     *
     * @return The child node with which to continue.
     */
    protected final VoiceXmlNode getVoiceXmlNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    public final String getEventType() {
        return event;
    }

    /**
     * {@inheritDoc}
     */
    public final int getCount() {
        return count;
    }
}
