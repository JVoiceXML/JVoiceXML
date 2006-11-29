/*
 * File:    $RCSfile: JVoiceXmlEventHandler.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.AbstractCatchElement;

/**
 * Event handler to catch events generated from the
 * <code>ImplementationPlatform</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlEventHandler
        implements EventHandler {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlEventHandler.class);

    /** The caught event. */
    private JVoiceXMLEvent event;

    /**
     * The strategies to execute, if the corresponding event type occured.
     *
     * @todo This has to be a scoped container.
     */
    private final Collection<AbstractEventStrategy> strategies;

    /** Semaphor to handle the wait/notify mechanism. */
    private final Object semaphor;

    /**
     * Construct a new object.
     */
    public JVoiceXmlEventHandler() {
        strategies = new java.util.ArrayList<AbstractEventStrategy>();
        semaphor = new Object();
    }

    /**
     * {@inheritDoc}
     */
    public void collect(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FieldFormItem field) {

        final Collection<AbstractCatchElement> catches = field
                .getCatchElements();
        for (AbstractCatchElement catchElement : catches) {
            final TokenList events = catchElement.getEventList();
            for (String eventType : events) {
                addCustomEvents(context, interpreter, fia, field, catchElement,
                                eventType);
            }
        }
    }

    /**
     * Add an event handler defined for the current field.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter
     *        The current <code>VoiceXmlInterpreter</code>
     * @param fia
     *        The <code>FormInterpretationAlgorithm</code>
     * @param field
     *        The visited field.
     * @param catchElement
     *        The node where the catch is defined.
     * @param eventType
     *        Name of the event to find a suitable strategy.
     * @see org.jvoicexml.xml.VoiceXmlNode
     * @todo Check how to ensure that tagClass extends VoiceXmlNode.
     */
    private void addCustomEvents(final VoiceXmlInterpreterContext context,
                                 final VoiceXmlInterpreter interpreter,
                                 final FormInterpretationAlgorithm fia,
                                 final FieldFormItem field,
                                 final AbstractCatchElement catchElement,
                                 final String eventType) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding custom event handler for '" + eventType
                         + "'...");
        }

        final AbstractEventStrategy strategy =
                new CatchEventStrategy(context, interpreter, fia, field,
                                       catchElement, eventType);
        addStrategy(strategy);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("done adding custom event handlers for '" + eventType
                         + "'...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addStrategy(final AbstractEventStrategy strategy) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding strategy:");
            LOGGER.debug(strategy);
        }

        strategies.add(strategy);
    }

    /**
     * {@inheritDoc}
     */
    public JVoiceXMLEvent waitEvent() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("waiting for an event...");
        }

        while (event == null) {
            try {
                synchronized (semaphor) {
                    semaphor.wait();
                }
            } catch (InterruptedException ie) {
                LOGGER.error("wait event was interrupted", ie);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received event: " + event);
        }

        return event;
    }

    /**
     * {@inheritDoc}
     */
    public void processEvent(final InputItem input)
            throws JVoiceXMLEvent {
        input.incrementEventCounter(event);

        final String type = event.getEventType();

        final Collection<AbstractEventStrategy> matchingStrategies =
                getMatchingEvents(type);

        if (matchingStrategies.isEmpty()) {
            LOGGER.info("no matching strategy for type '" + type + "'");

            event = null;

            return;
        }

        /** @todo Evaluate the cond condition. */

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event of type '" + type + "'...");
        }

        final Collection<AbstractEventStrategy> filteredStrategies =
                filterCount(input,
                            matchingStrategies);
        final int max = getHighestCount(filteredStrategies);

        final Collection<AbstractEventStrategy> correctCountStrategies =
                getStrategiesWithCount(filteredStrategies, max);

        final Iterator<AbstractEventStrategy> iterator =
                correctCountStrategies.iterator();
        final AbstractEventStrategy strategy = iterator.next();

        strategy.process(event);

        event = null;
    }

    /**
     * Get all strategies that match the given type. Match is
     * <ul>
     * <li>an exact match,</li>
     * <li>a prefix match or</li>
     * <li>if the catch attribute is not specified.</li>
     * </ul>
     *
     * @param type
     *        Event typeto look for
     * @return Strategies that match the event type.
     */
    private Collection<AbstractEventStrategy> getMatchingEvents(final String
            type) {
        final Collection<AbstractEventStrategy> matchingStrategies =
                new java.util.ArrayList<AbstractEventStrategy>();

        for (AbstractEventStrategy strategy : strategies) {
            final String currentType = strategy.getEventType();
            if (currentType == null) {
                matchingStrategies.add(strategy);
            } else {
                if (currentType.startsWith(type)) {
                    matchingStrategies.add(strategy);
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + matchingStrategies.size()
                         + " matching event strategies for type '" + type
                         + "'");
        }

        return matchingStrategies;
    }

    /**
     * Remove all strategies that have a higher count than the current count.
     *
     * @param input
     *        The current inputitem
     * @param col
     *        All event strategies matching the current event.
     * @return Collection of all strategies with a higher count than the current
     *         count.
     */
    private Collection<AbstractEventStrategy> filterCount(final InputItem input,
            final Collection<
                    AbstractEventStrategy> col) {
        final Collection<AbstractEventStrategy> filteredStrategies =
                new java.util.ArrayList<AbstractEventStrategy>();

        for (AbstractEventStrategy strategy : col) {
            final String type = strategy.getEventType();
            final int count = input.getEventCount(type);
            if (count >= strategy.getCount()) {
                filteredStrategies.add(strategy);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reducing event strategies by count from "
                         + col.size() + " to " + filteredStrategies.size());
        }

        return filteredStrategies;
    }

    /**
     * Find the highest count of all strategies.
     *
     * @param col
     *        Collection of strateies.
     * @return Highest count of all strategies.
     */
    private int getHighestCount(final Collection<AbstractEventStrategy> col) {
        int max = 0;

        for (AbstractEventStrategy strategy : col) {
            final int count = strategy.getCount();
            if (count > max) {
                max = count;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("highest count for event strategies is " + max);
        }

        return max;
    }

    /**
     * Get all strategies with the given count.
     *
     * @param col
     *        Strategies to filter.
     * @param count
     *        Count to find.
     * @return Collection of strategies with the given count.
     */
    private Collection<AbstractEventStrategy> getStrategiesWithCount(
            final Collection<AbstractEventStrategy> col, final int count) {
        final Collection<AbstractEventStrategy> filteredStrategies =
                new java.util.ArrayList<AbstractEventStrategy>();

        for (AbstractEventStrategy strategy : col) {
            if (strategy.getCount() == count) {
                filteredStrategies.add(strategy);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + filteredStrategies.size()
                         + " event strategies with count " + count);
        }

        return filteredStrategies;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void notifyEvent(final JVoiceXMLEvent e) {
        // Allow for only one event.
        if (event != null) {
            return;
        }

        event = e;

        synchronized (semaphor) {
            semaphor.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    public JVoiceXMLEvent getEvent() {
        return event;
    }
}
