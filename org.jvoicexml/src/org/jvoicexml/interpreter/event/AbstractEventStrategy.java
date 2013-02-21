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

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Catch;

/**
 * Basic methods of an {@link EventStrategy} that can be processed by the
 * {@link JVoiceXmlEventHandler}.
 *
 * <p>
 * Typically, an {@link AbstractEventStrategy} is responsible to handle
 * events for a single {@link FormItem}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
abstract class AbstractEventStrategy implements EventStrategy {
    /** Base hash code. */
    private static final int HASH_CODE_BASE = 3;

    /** Multiplier for hash code generation. */
    private static final int HASH_CODE_MULTIPLIER = 5;

    /** The VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private final VoiceXmlInterpreter interpreter;

    /** The current FIA. */
    private FormInterpretationAlgorithm fia;

    /** The current form item. */
    private FormItem item;

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
     * Constructs a new object.
     *
     * @param ctx
     *        the VoiceXML interpreter context.
     * @param ip
     *        the VoiceXML interpreter.
     * @param algorithm
     *        the FIA, maybe <code>null</code>. If a <code>null</code> value is
     *        provided the strategy obtains the current FIA from the
     *        interpreter in the processing state.
     * @param formItem
     *        the current form item, maybe <code>null</code>. If a
     *        <code>null</code> value is provided, the strategy tries to obtain
     *        the current item from the FIA in the processing state.
     * @param n
     *        the child node with which to continue.
     * @param type
     *        the event type.
     */
    protected AbstractEventStrategy(final VoiceXmlInterpreterContext ctx,
                                    final VoiceXmlInterpreter ip,
                                    final FormInterpretationAlgorithm algorithm,
                                    final FormItem formItem,
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
        if (fia == null) {
            if (interpreter == null) {
                return null;
            }
            fia = interpreter.getFormInterpretationAlgorithm();
        }
        return fia;
    }

    /**
     * Retrieves the tag strategy executor. If a FIA is present the executor is
     * obtained from the FIA, a new one is created otherwise.
     * @return the tag strategy executor.
     * @since 0.7
     */
    protected TagStrategyExecutor getTagStrategyExecutor() {
        if (fia == null) {
            return new TagStrategyExecutor();
        } else {
            return fia.getTagStrategyExecutor();
        }
    }

    /**
     * Retrieves the current form item.
     *
     * @return The current form item.
     */
    protected FormItem getFormItem() {
        if (item == null) {
            FormInterpretationAlgorithm algorithm =
                getFormInterpretationAlgorithm();
            if (algorithm == null) {
                return null;
            }
            item = algorithm.getFormItem();
        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isActive() throws SemanticError {
        if (!(node instanceof AbstractCatchElement)) {
            return true;
        }
        final AbstractCatchElement catchElement = (AbstractCatchElement) node;
        final String cond = catchElement.getCond();
        if (cond == null) {
            return true;
        }
        final ScriptingEngine scripting = context.getScriptingEngine();
        final Object result = scripting.eval(cond);
        if (!(result instanceof Boolean)) {
            throw new SemanticError("condition '" + cond
                    + "' does evaluate to a boolean value");
        }
        return Boolean.TRUE.equals(result);
    }

    /**
     * {@inheritDoc}
     * @since 0.7
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AbstractEventStrategy)) {
            return false;
        }

        final AbstractEventStrategy strategy = (AbstractEventStrategy) obj;

        if (context != strategy.context) {
            if ((context != null) && !context.equals(strategy.context)) {
                return false;
            }
        }
        if (interpreter != strategy.interpreter) {
            if ((interpreter != null)
                    && !interpreter.equals(strategy.interpreter)) {
                return false;
            }
        }
        if (fia != strategy.fia) {
            if ((fia != null) && !fia.equals(strategy.fia)) {
                return false;
            }
        }
        if (item != strategy.item) {
            if ((item != null) && !item.equals(strategy.item)) {
                return false;
            }
        }
        if (node != strategy.node) {
            if ((node != null) && !node.equals(strategy.node)) {
                return false;
            }
        }
        if (event != strategy.event) {
            if ((event != null) && !event.equals(strategy.event)) {
                return false;
            }
        }

        return count == strategy.count;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.6
     */
    @Override
    public int hashCode() {
        int hash = HASH_CODE_BASE;
        hash *= HASH_CODE_MULTIPLIER;
        if (context != null) {
            hash += context.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (interpreter != null) {
            hash += interpreter.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (fia != null) {
            hash += fia.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (item != null) {
            hash += item.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (node != null) {
            hash += node.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        return hash + count;
    }
}
