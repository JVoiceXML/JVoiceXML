/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.CatchContainer;
import org.jvoicexml.interpreter.EventStrategy;

/**
 * Filters all events whose condition does not evaluate to <code>true</code>.
 * @author Dirk Schnelle-Walka
 * @since 0.7.1
 */
final class ConditionEventTypeFilter implements EventFilter {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(ConditionEventTypeFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(final Collection<EventStrategy> strategies,
            final JVoiceXMLEvent event, final CatchContainer item)
        throws SemanticError {
        final Collection<EventStrategy> removeStractegies =
                new java.util.ArrayList<EventStrategy>();
        for (EventStrategy strategy : strategies) {
            final boolean active = strategy.isActive();
            if (!active) {
                removeStractegies.add(strategy);
            }
        }
        strategies.removeAll(removeStractegies);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + strategies.size()
                    + " event strategies whose condition evaluate to true");
        }
    }

}
