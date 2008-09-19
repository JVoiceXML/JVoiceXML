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

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedCollection;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.AbstractCatchElement;

/**
 * Event handler to catch events generated from the
 * {@link org.jvoicexml.ImplementationPlatform}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
 */
public final class JVoiceXmlEventHandler
        implements EventHandler {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlEventHandler.class);

    /** Input item strategy factory. */
    private final InputItemEventStrategyDecoratorFactory inputItemFactory;

    /** The caught event. */
    private JVoiceXMLEvent event;

    /**
     * The strategies to execute, if the corresponding event type occurred.
     */
    private final ScopedCollection<EventStrategy> strategies;

    /** Semaphore to handle the wait/notify mechanism. */
    private final Object semaphore;

    /**
     * Construct a new object.
     * @param observer the scope observer.
     */
    public JVoiceXmlEventHandler(final ScopeObserver observer) {
        strategies = new ScopedCollection<EventStrategy>(observer);
        inputItemFactory = new InputItemEventStrategyDecoratorFactory();
        semaphore = new Object();
    }

    /**
     * Retrieves the strategies to execute.
     * @return the strategies to execute.
     */
    Collection<EventStrategy> getStrategies() {
        return strategies;
    }

    /**
     * {@inheritDoc}
     */
    public void collect(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final Dialog dialog) {
        // Add the default catch elements.
        final Collection<AbstractCatchElement> catches = dialog
                .getCatchElements();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + catches.size()
                    + " catch elements in dialog '" + dialog.getId() + "'");
        }

        // Add custom catch elements.
        final FormInterpretationAlgorithm fia =
            interpreter.getFormInterpretationAlgorithm();
        for (AbstractCatchElement catchElement : catches) {
            final TokenList events = catchElement.getEventList();
            for (String eventType : events) {
                addCustomEvents(context, interpreter, fia, null, catchElement,
                                eventType);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void collect(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final InputItem item) {
        // Add the default catch elements.
        final Collection<AbstractCatchElement> catches = item
                .getCatchElements();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + catches.size() + " catch elements in item '"
                    + item.getName() + "'");
        }

        // Add custom catch elements.
        for (AbstractCatchElement catchElement : catches) {
            final TokenList events = catchElement.getEventList();
            for (String eventType : events) {
                addCustomEvents(context, interpreter, fia, item, catchElement,
                                eventType);
            }
        }

        final AbstractInputItemEventStrategy<?> inputItemStrategy =
            inputItemFactory.getDecorator(context, interpreter, fia, item);
        if (inputItemStrategy instanceof CollectiveEventStrategy) {
            final String type = inputItemStrategy.getEventType();
            final EventStrategy strategy = getStrategy(type);
            if (strategy == null) {
                addStrategy(inputItemStrategy);
            } else {
                @SuppressWarnings("unchecked")
                CollectiveEventStrategy<InputItem> collectiveStrategy =
                    (CollectiveEventStrategy<InputItem>) strategy;
                collectiveStrategy.addItem(item);
            }
        } else {
            addStrategy(inputItemStrategy);
        }
    }

    /**
     * Adds an event handler defined for the current input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter
     *        The current <code>VoiceXmlInterpreter</code>
     * @param fia
     *        The <code>FormInterpretationAlgorithm</code>
     * @param item
     *        The visited input item.
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
                                 final InputItem item,
                                 final AbstractCatchElement catchElement,
                                 final String eventType) {
        final EventStrategy strategy =
                new CatchEventStrategy(context, interpreter, fia, item,
                                       catchElement, eventType);
        addStrategy(strategy);
    }

    /**
     * {@inheritDoc}
     */
    public void addStrategy(final EventStrategy strategy) {
        if (strategy == null) {
            LOGGER.debug("can not add a null strategy");
            return;
        }

        if (strategies.contains(strategy)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("strategy: '" + strategy.getClass()
                        + "' for event type '" + strategy.getEventType() + "'"
                        + " ignored since it is already registered");
            }
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding strategy: '" + strategy.getClass()
                    + "' for event type '" + strategy.getEventType() + "'");
        }

        strategies.add(strategy);
    }

    /**
     * Retrieves the first {@link EventStrategy} with the given type.
     * @param type event type to look for.
     * @return found strategy, <code>null</code> if no strategy was found.
     */
    private EventStrategy getStrategy(final String type) {
        for (EventStrategy strategy : strategies) {
            final String currentType = strategy.getEventType();
            if (currentType.equals(type)) {
                return strategy;
            }
        }
        return null;
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
                synchronized (semaphore) {
                    if (event == null) {
                        semaphore.wait();
                    }
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
        if (event == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no event: nothing to do");
            }
            return;
        }

        if (input != null) {
            input.incrementEventCounter(event);
        }

        final String type = event.getEventType();

        final Collection<EventStrategy> matchingStrategies =
                getMatchingEvents(type);

        if (matchingStrategies.isEmpty()) {
            LOGGER.info("no matching strategy for type '" + type + "'");

            final JVoiceXMLEvent copy = event;
            event = null;

            throw copy;
        }

        /** @todo Evaluate the cond condition. */

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event of type '" + type + "'...");
        }

        final Collection<EventStrategy> remainingStrategies;
        if (input == null) {
            remainingStrategies = matchingStrategies;
        } else {
            final Collection<EventStrategy> filteredStrategies =
                filterCount(input, matchingStrategies);
            final int max = getHighestCount(filteredStrategies);

            remainingStrategies =
                getStrategiesWithCount(filteredStrategies, max);
        }

        final Iterator<EventStrategy> iterator =
            remainingStrategies.iterator();
        final EventStrategy strategy = iterator.next();

        strategy.process(event);

        event = null;
    }

    /**
     * Retrieves all strategies that match the given type. Match is
     * <ul>
     * <li>an exact match,</li>
     * <li>a prefix match or</li>
     * <li>if the catch attribute is not specified.</li>
     * </ul>
     *
     * @param type
     *        event type to look for
     * @return strategies that match the event type.
     */
    private Collection<EventStrategy> getMatchingEvents(final String
            type) {
        final Collection<EventStrategy> matchingStrategies =
                new java.util.ArrayList<EventStrategy>();

        for (EventStrategy strategy : strategies) {
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
     *        The current input item
     * @param col
     *        All event strategies matching the current event.
     * @return Collection of all strategies with a higher count than the current
     *         count.
     */
    private Collection<EventStrategy> filterCount(final InputItem input,
            final Collection<EventStrategy> col) {
        final Collection<EventStrategy> filteredStrategies =
                new java.util.ArrayList<EventStrategy>();

        for (EventStrategy strategy : col) {
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
     *        Collection of strategies.
     * @return Highest count of all strategies.
     */
    private int getHighestCount(final Collection<EventStrategy> col) {
        int max = 0;

        for (EventStrategy strategy : col) {
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
    private Collection<EventStrategy> getStrategiesWithCount(
            final Collection<EventStrategy> col, final int count) {
        final Collection<EventStrategy> filteredStrategies =
                new java.util.ArrayList<EventStrategy>();

        for (EventStrategy strategy : col) {
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
        if ((event != null) && (e != null)) {
            LOGGER.info("ignoring second event " + e.getEventType()
                    + " current is " + event.getEventType());
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            if (e != null) {
                LOGGER.debug("notifying event " + e.getEventType());
            }
        }

        synchronized (semaphore) {
            event = e;
            semaphore.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    public JVoiceXMLEvent getEvent() {
        return event;
    }
}
