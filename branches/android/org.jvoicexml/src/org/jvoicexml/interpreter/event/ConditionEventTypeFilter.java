/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/event/ConditionEventTypeFilter.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
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
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.CatchContainer;
import org.jvoicexml.interpreter.EventStrategy;

/**
 * Filters all events whose condition does not evaluate to <code>true</code>.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.7.1
 */
final class ConditionEventTypeFilter implements EventFilter {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(ConditionEventTypeFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(final Collection<EventStrategy> strategies,
            final JVoiceXMLEvent event, final CatchContainer item)
        throws SemanticError {
        for (EventStrategy strategy : strategies) {
            final boolean active = strategy.isActive();
            if (!active) {
                strategies.remove(strategy);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + strategies.size()
                    + " event strategies whose condition evaluate to true");
        }
    }

}
