/*
 * File:    $RCSfile: EventHandler.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.event.AbstractEventStrategy;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;

/**
 * Event handler to catch events generated from the
 * <code>ImplementationPlatform</code>.
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
 * being thrown or whose cond evaluates to false after conversion to boolean.
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
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface EventHandler
        extends EventObserver {
    /**
     * Add all event handlers defined in the given field.
     * @param context The current <code>VoiceXmlInterpreterContext</code>
     * @param interpreter The current <code>VoiceXmlInterpreter</code>
     * @param fia The current FIA.
     * @param field The field.
     */
    void collect(final VoiceXmlInterpreterContext context,
                 final VoiceXmlInterpreter interpreter,
                 final FormInterpretationAlgorithm fia,
                 final FieldFormItem field);

    /**
     * Wait until an event was generated in the implementation platform.
     * @return The caught event.
     */
    JVoiceXMLEvent waitEvent();

    /**
     * Process the last received event.
     * @param input The current input item.
     * @exception JVoiceXMLEvent
     *            Error or event processing the event.
     */
    void processEvent(final InputItem input)
            throws JVoiceXMLEvent;

    /**
     * Add a strategy for the given event type.
     *
     * @param strategy The strategy to add.
     */
    void addStrategy(final AbstractEventStrategy strategy);

    /**
     * Retrieve the event.
     * @return The caught event.
     */
    JVoiceXMLEvent getEvent();
}
