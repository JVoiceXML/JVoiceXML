/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.interpreter.formitem;

import java.util.Collection;
import java.util.Map;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventCountable;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Basic implementation of an {@link EventCountable}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.interpreter.formitem.InputItem
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
final class EventCounter implements EventCountable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(EventCounter.class);

    /** Event counter. */
    private final Map<String, Integer> counter;

    /**
     * Constructss a new object.
     */
    public EventCounter() {
        counter = new java.util.HashMap<String, Integer>();
    }

    /**
     * {@inheritDoc}
     */
    public void incrementEventCounter(final JVoiceXMLEvent event) {
        final Collection<String> prefixes = getPrefixes(event.getEventType());
        for (String prefix : prefixes) {
            Integer count = counter.get(prefix);
            if (count == null) {
                count = new Integer(0);
            }

            counter.put(prefix, count.intValue() + 1);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("incremented count of '" + prefix + "' to "
                             + counter.get(prefix));
            }
        }
    }

    /**
     * Get a collection of all prefixes for the given envent type.
     * @param type Event type
     * @return Collection of prefixes, including the name of the
     * event.
     *
     * @see org.jvoicexml.event.JVoiceXMLEvent#getEventType()
     */
    private Collection<String> getPrefixes(final String type) {
        final Collection<String> prefixes = new java.util.ArrayList<String>();

        prefixes.add(type);

        String prefix = type;
        int dot = prefix.lastIndexOf(".");
        while (dot > 0) {
            prefix = prefix.substring(0, dot);
            prefixes.add(prefix);
            dot = prefix.lastIndexOf(".");
        }

        return prefixes;
    }

    /**
     * {@inheritDoc}
     */
    public int getEventCount(final String type) {
        final Integer count = counter.get(type);
        if (count == null) {
            return 0;
        }

        return count.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public void resetEventCounter() {
        counter.clear();
    }
}

