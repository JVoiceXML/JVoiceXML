/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/EventCountable.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Counter to allow counting of different occurrences of the same event.
 *
 * <p>
 * Each <code>&lt;form&gt;</code>, <code>&lt;menu&gt;</code>, and form item
 * maintains a counter for each event that occurs while it is being visited.
 * Item-level event counters are used for events thrown while visiting
 * individual form items and while executing <code>&lt;filled&gt;</code>
 * elements contained within those items. Form-level and menu-level counters
 * are used for events thrown during dialog initialization and while executing
 * form-level <code>&lt;filled&gt;</code> elements.
 * </p>
 *
 * <p>
 * Form-level and menu-level event counters are reset each time the
 * <code>&lt;menu&gt;</code> or <code>&lt;form&gt;</code> is re-entered.
 * Form-level and menu-level event counters are not reset by the
 * <code>&lt;clear&gt;</code> element.
 * </p>
 *
 * <p>
 * Item-level event counters are reset each time the <code>&lt;form&gt;</code>
 * containing the item is re-entered. Item-level event counters are also reset
 * when the item is reset with the <code>&lt;clear&gt;</code> element. An
 * item's event counters are not reset when the item is re-entered without
 * leaving the <code>&lt;form&gt;</code>.
 * </p>
 *
 * <p>
 * Counters are incremented against the full event name and every prefix
 * matching event name; for example, occurrence of the event
 * <code>event.foo.1</code> increments the counters for
 * <code>event.foo.1</code> plus <code>event.foo</code> and
 * <code>event</code>.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 2129 $
 * @since 0.6
 *
 * @see org.jvoicexml.interpreter.formitem.AbstractInputItem
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface EventCountable {
    /**
     * Increment counters for all events that have the same name as the
     * given event or have a name that is a prefix of the given event.
     * @param event Event to increment.
     */
    void incrementEventCounter(final JVoiceXMLEvent event);

    /**
     * Retrieve the counter for the given event type.
     * @param type Event type.
     * @return Count for the given event type.
     */
     int getEventCount(final String type);

    /**
     * Reset the counter for all events.
     */
    void resetEventCounter();
}
