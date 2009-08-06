/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.InputItem;

/**
 * Filters all events that match the current type.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
final class EventCountTypeFilter implements EventFilter {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(EventCountTypeFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(final Collection<EventStrategy> strategies,
            final JVoiceXMLEvent event, final InputItem input) {
        final int size = strategies.size();
        final Collection<EventStrategy> matchingStrategies =
            new java.util.ArrayList<EventStrategy>();

        for (EventStrategy strategy : strategies) {
            final String type = strategy.getEventType();
            final int count = input.getEventCount(type);
            if (count >= strategy.getCount()) {
                matchingStrategies.add(strategy);
            }
        }
        strategies.retainAll(matchingStrategies);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reducing event strategies by count from "
                    + size + " to " + matchingStrategies.size());
        }

    }
}
