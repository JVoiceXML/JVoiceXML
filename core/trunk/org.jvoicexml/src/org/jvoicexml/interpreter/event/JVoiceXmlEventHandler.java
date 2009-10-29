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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.CatchContainer;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.EventCountable;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedCollection;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Help;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.Nomatch;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Event handler to catch events generated from the
 * {@link org.jvoicexml.ImplementationPlatform}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @see org.jvoicexml.ImplementationPlatform
 */
public final class JVoiceXmlEventHandler
        implements EventHandler {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlEventHandler.class);

    /** Input item strategy factory. */
    private final EventStrategyDecoratorFactory inputItemFactory;

    /** The caught event. */
    private JVoiceXMLEvent event;

    /** Event filter chain to determine the relevant event strategy. */
    private final Collection<EventFilter> filters;

    /** 
     * Event filter chain to determine the relevant event strategy if no input
     * item is given.
     */
    private final Collection<EventFilter> filtersNoinput;

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
        inputItemFactory = new EventStrategyDecoratorFactory();
        semaphore = new Object();
        filters = new java.util.ArrayList<EventFilter>();
        filters.add(new EventTypeFilter());
        filters.add(new ConditionEventTypeFilter());
        filters.add(new EventCountTypeFilter());
        filters.add(new HighestCountEventTypeFilter());
        filtersNoinput = new java.util.ArrayList<EventFilter>();
        filtersNoinput.add(new EventTypeFilter());
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
                        final VoiceXmlDocument document) {
        final Vxml vxml = document.getVxml();
        final Collection<AbstractCatchElement> catches =
            new java.util.ArrayList<AbstractCatchElement>();
        final NodeList children = vxml.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof AbstractCatchElement) {
                final AbstractCatchElement catchElement =
                    (AbstractCatchElement) child;
                catches.add(catchElement);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + catches.size()
                    + " catch elements in document");
        }

        // Transform them into event handlers.
        final FormInterpretationAlgorithm fia;
        if (interpreter == null) {
            fia = null;
        } else {
            fia = interpreter.getFormInterpretationAlgorithm();
        }
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
                        final Dialog dialog) {
        // Retrieve the specified catch elements.
        final Collection<AbstractCatchElement> catches = dialog
                .getCatchElements();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + catches.size()
                    + " catch elements in dialog '" + dialog.getId() + "'");
        }

        // Transform them into event handlers.
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
    @Override
    public Collection<EventStrategy> collect(
            final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final CatchContainer item) {
        final Collection<EventStrategy> added =
            new java.util.ArrayList<EventStrategy>();
        // Retrieve the specified catch elements.
        final Collection<AbstractCatchElement> catches =
            item.getCatchElements();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + catches.size() + " catch elements in item '"
                    + item.getName() + "'");
        }

        // Transform them into event handlers.
        for (AbstractCatchElement catchElement : catches) {
            final TokenList events = catchElement.getEventList();
            for (String eventType : events) {
                if (eventType.equals(Filled.TAG_NAME)
                        && (item instanceof InitialFormItem)) {
                    // TODO The spec does not tell what to do in this case,
                    // so we simply ignore it.
                    LOGGER.warn("Initial form items must not have catches for "
                            + "filled: ignoring...");
                } else {
                    final EventStrategy strategy =
                        addCustomEvents(context, interpreter, fia, item,
                            catchElement, eventType);
                    added.add(strategy);
                }
            }
        }

        // Add an input item strategy
        if (item instanceof InputItem) {
            final InputItem inputItem = (InputItem) item;

            // Add the default strategies for input items.
            Collection<EventStrategy> defaultStrategies =
                addDefaultStrategies(context, interpreter, fia, inputItem);
            added.addAll(defaultStrategies);
        }

        final EventStrategy itemStrategy =
            inputItemFactory.getDecorator(context, interpreter, fia,
                    item);
        boolean add = addStrategy(itemStrategy);
        if (add) {
            added.add(itemStrategy);
        }
        return added;
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
     * @return added strategy.
     */
    private EventStrategy addCustomEvents(
            final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,
            final FormItem item,
            final AbstractCatchElement catchElement,
            final String eventType) {
        final EventStrategy strategy =
                new CatchEventStrategy(context, interpreter, fia, item,
                                       catchElement, eventType);
        addStrategy(strategy);
        return strategy;
    }

    /**
     * Adds the missing event handlers that are defined by default.
     * <p>
     * The default event handlers are specified at
     * <a href="http://www.w3.org/TR/2004/REC-voicexml20-20040316#dml5.2.5">
     * http://www.w3.org/TR/2004/REC-voicexml20-20040316#dml5.2.5</a>
     * </p>
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter
     *        The current <code>VoiceXmlInterpreter</code>
     * @param fia
     *        The <code>FormInterpretationAlgorithm</code>
     * @param item
     *        The visited form item.
     * @since 0.7
     * @return added strategies
     */
    private Collection<EventStrategy> addDefaultStrategies(
            final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,
            final InputItem item) {
        final Collection<EventStrategy> added =
            new java.util.ArrayList<EventStrategy>();
        if (!containsStrategy(Noinput.TAG_NAME)) {
            final EventStrategy strategy =
                new DefaultRepromptEventStrategy(context, interpreter,
                        fia, item, Noinput.TAG_NAME);
            final boolean add = addStrategy(strategy);
            if (add) {
                added.add(strategy);
            }
        }
        if (!containsStrategy(Nomatch.TAG_NAME)) {
            final EventStrategy strategy =
                new DefaultRepromptEventStrategy(context, interpreter,
                        fia, item, Nomatch.TAG_NAME);
            final boolean add = addStrategy(strategy);
            if (add) {
                added.add(strategy);
            }
        }
        if (!containsStrategy(Help.TAG_NAME)) {
            final EventStrategy strategy =
                new DefaultRepromptEventStrategy(context, interpreter,
                        fia, item, Help.TAG_NAME);
            final boolean add = addStrategy(strategy);
            if (add) {
                added.add(strategy);
            }
        }
        if (!containsStrategy("cancel")) {
            final EventStrategy strategy =
                new DefaultCancelEventStrategy(context, interpreter,
                        fia, item, "cancel");
            final boolean add = addStrategy(strategy);
            if (add) {
                added.add(strategy);
            }
        }
        return added;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addStrategy(final EventStrategy strategy) {
        if (strategy == null) {
            LOGGER.debug("can not add a null strategy");
            return false;
        }

        if (strategies.contains(strategy)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("strategy: '" + strategy.getClass()
                        + "' for event type '" + strategy.getEventType() + "'"
                        + " ignored since it is already registered");
            }
            return false;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding strategy: '" + strategy.getClass()
                    + "' for event type '" + strategy.getEventType() + "'");
        }

        return strategies.add(strategy);
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
     * Checks if there exists an {@link EventStrategy} for the given type.
     * @param type event type to look for.
     * @return <code>true</code> if there is a strategy.
     * @since 0.7
     */
    private boolean containsStrategy(final String type) {
        final EventStrategy strategy = getStrategy(type);
        return strategy != null;
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
     * The relevant {@link EventStrategy} is determined via a chaining of
     * {@link EventFilter}s.
     */
    @Override
    public void processEvent(final CatchContainer item)
            throws JVoiceXMLEvent {
        if (event == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no event: nothing to do");
            }
            return;
        }

        if (item instanceof EventCountable) {
            final EventCountable countable = (EventCountable) item;
            countable.incrementEventCounter(event);
        }

        final String type = event.getEventType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("processing event of type '" + type + "'...");
        }

        final Collection<EventFilter> eventFilters;
        final Collection<EventStrategy> matchingStrategies =
                new java.util.ArrayList<EventStrategy>(strategies);
        if (item == null) {
            eventFilters = filtersNoinput;
        } else {
            eventFilters = filters;
        }
        for (EventFilter filter : eventFilters) {
            filter.filter(matchingStrategies, event, item);
            if (matchingStrategies.isEmpty()) {
                LOGGER.info("no matching strategy for type '" + type + "'");

                final JVoiceXMLEvent copy = event;
                event = null;

                throw copy;
            }
        }

        final Iterator<EventStrategy> iterator =
            matchingStrategies.iterator();
        final EventStrategy strategy = iterator.next();

        try {
            strategy.process(event);
        } finally {
            // Be prepared that an event is thrown while processing the current
            // event,
            event = null;
        }
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
                LOGGER.debug("notifying event '" + e.getEventType() + "'...");
            }
        }

        synchronized (semaphore) {
            event = e;
            semaphore.notify();
        }

        if (e != null) {
            LOGGER.info("notified event '" + e.getEventType() + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    public JVoiceXMLEvent getEvent() {
        return event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeStrategies(
            final Collection<EventStrategy> strats) {
        if (strats == null) {
            return false;
        }
        return strategies.removeAll(strats);
    }
}
