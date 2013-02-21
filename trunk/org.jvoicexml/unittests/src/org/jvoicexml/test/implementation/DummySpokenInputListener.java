/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.test.implementation;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;

/**
 * Dummy implementation of a {@link SpokenInputListener}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class DummySpokenInputListener implements SpokenInputListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(DummySpokenInputListener.class);

    /** Received events. */
    private final List<SpokenInputEvent> events;

    /**
     * Constructs a new object.
     */
    public DummySpokenInputListener() {
        events = new java.util.ArrayList<SpokenInputEvent>();
    }

    /**
     * Retrieves the number of caught events.
     * @return number of events.
     */
    public int size() {
        return events.size();
    }

    /**
     * Waits until the given number of events have been collected.
     * @param size number of events
     * @param timeout max. timeout to wait for events
     * @throws InterruptedException
     *         error waiting
     */
    public void waitSize(final int size, final long timeout)
        throws InterruptedException {
        long start = System.currentTimeMillis();
        while (size > events.size()) {
            synchronized (events) {
                events.wait(timeout);
                long now = System.currentTimeMillis();
                if (size < events.size() || (now - start > timeout)) {
                    Assert.fail(size + " not reached within " + timeout
                            + "msec (current: " + events.size() + ")");
                }
            }
        }
    }

    /**
     * Retrieves the event at the given position.
     * @param index the position of the event to retrieve
     * @return event at the given position
     */
    public SpokenInputEvent get(final int index) {
        return events.get(index);
    }

    /**
     * Removes all collected events.
     */
    public void clear() {
        events.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void inputStatusChanged(final SpokenInputEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received: " + event);
        }
        events.add(event);
        synchronized (events) {
            events.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputError(final ErrorEvent error) {
        LOGGER.error(error.getMessage(), error);
    }
}
