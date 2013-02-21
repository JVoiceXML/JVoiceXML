/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
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

package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Event handler to catch events generated from the
 * {@link org.jvoicexml.ImplementationPlatform} and propagated via
 * {@link #notifyEvent(org.jvoicexml.event.JVoiceXMLEvent)}.
 *
 * <p>
 * When an event is thrown, the scope in which the event is handled and its
 * enclosing scopes are examined to find the best qualified catch element,
 * according to the following algorithm:
 *
 * <ol>
 * <li>
 * Form an ordered list of catches consisting of all catches in the current
 * scope and all enclosing scopes (form item, form, document, application root
 * document, interpreter context), ordered first by scope (starting with the
 * current scope), and then within each scope by document order.
 * </li>
 * <li>
 * Remove from this list all catches whose event name does not match the event
 * being thrown or whose <code>cond</code> evaluates to false after conversion
 * to boolean.
 * </li>
 * <li>
 * Find the <em>correct count</em>: the highest count among the catch elements
 * still on the list less than or equal to the current count value.
 * </li>
 * <li>
 * Select the first element in the list with the <em>correct count</em>.
 * </li>
 * </ol>
 *
 * <p>
 * The name of a thrown event matches the catch element event name if it is
 * an exact match, a prefix match or if the catch event attribute is not
 * specified (note that the event attribute cannot be specified as an empty
 * string - event="" is syntactically invalid). A prefix match occurs when the
 * catch element event attribute is a token prefix of the name of the event
 * being thrown, where the dot is the token separator, all trailing dots are
 * removed, and a remaining empty string matches everything.
 * </p>
 *
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @see org.jvoicexml.ImplementationPlatform
 */
public interface EventHandler
        extends EventObserver {
    /**
     * Adds all event handlers defined in the given document.
     * @param context the current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter the current <code>VoiceXmlInterpreter</code>
     * @param document the document to inspect.
     */
    void collect(final VoiceXmlInterpreterContext context,
                 final VoiceXmlInterpreter interpreter,
                 final VoiceXmlDocument document);

    /**
     * Adds all event handlers defined in the given dialog.
     * @param context the current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter the current <code>VoiceXmlInterpreter</code>
     * @param dialog the dialog to inspect.
     */
    void collect(final VoiceXmlInterpreterContext context,
                 final VoiceXmlInterpreter interpreter,
                 final Dialog dialog);

    /**
     * Adds all event handlers defined in the given input item.
     * @param context the current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter the current <code>VoiceXmlInterpreter</code>
     * @param fia the current FIA.
     * @param item the form item to inspect.
     * @return added event handlers
     */
    Collection<EventStrategy> collect(final VoiceXmlInterpreterContext context,
                 final VoiceXmlInterpreter interpreter,
                 final FormInterpretationAlgorithm fia,
                 final CatchContainer item);

    /**
     * Removes all event handlers that were collected for the given form item.
     * @param item the form item
     * @since 0.7.4
     */
    void clean(final FormItem item);

    /**
     * Waits until an event was generated in the implementation platform.
     * @return the caught event.
     */
    JVoiceXMLEvent waitEvent();

    /**
     * Processes the last received event.
     * @param item The current form item.
     * @exception JVoiceXMLEvent
     *            Error or event processing the event or there was no handler
     *            to process the current event
     */
    void processEvent(final CatchContainer item)
            throws JVoiceXMLEvent;

    /**
     * Adds a strategy for the given event type.
     *
     * @param strategy the strategy to add.
     * @return <code>true</code> if the strategy was added.
     */
    boolean addStrategy(final EventStrategy strategy);

    /**
     * Removes the given strategies.
     * @param strategies strategies to remove
     * @return <code>true<</code> if at least one strategy was removed
     * @since 0.7.2
     */
    boolean removeStrategies(final Collection<EventStrategy> strategies);

    /**
     * Retrieves the event.
     * @return the caught event.
     */
    JVoiceXMLEvent getEvent();
}
