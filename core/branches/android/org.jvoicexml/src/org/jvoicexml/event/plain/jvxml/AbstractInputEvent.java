/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/event/plain/jvxml/AbstractInputEvent.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain.jvxml;

import org.jvoicexml.event.PlainEvent;

/**
 * Base class for all events that propagate input events.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 * @since 0.6
 */
public abstract class AbstractInputEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = -8834314899605323439L;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     */
    public AbstractInputEvent() {
        super();
    }

    /**
     * Retrieves the result of the input process.
     *
     * @return result.
     */
    public abstract Object getInputResult();
}
