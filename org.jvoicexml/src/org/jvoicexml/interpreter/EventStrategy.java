/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/EventHandler.java $
 * Version: $LastChangedRevision: 965 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Base strategy to process an event coming from the implementation platform.
 *
 * @author Dirk Schnelle
 * @version $Revision: 571 $
 *
 * @see org.jvoicexml.ImplementationPlatform
 */
public interface EventStrategy {
    /**
     * Retrieves the event type.
     *
     * @return the event type.
     */
    String getEventType();

    /**
     * retrieves the count of different occurrences of the event type returned
     * by {@link #getEventType()}.
     *
     * @return The count.
     */
    int getCount();

    /**
     * Processes the event.
     *
     * @param event
     *        the caught event.
     * @exception JVoiceXMLEvent
     *            Error or event processing the current tag.
     */
    void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent;
}
