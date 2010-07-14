/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.test.implementation;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * {@link SynthesizedOutputListener} for test purposes.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class DummySynthesizedOutputListener
        implements SynthesizedOutputListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(DummySynthesizedOutputListener.class);

    /** Collected events. */
    private final List<SynthesizedOutputEvent> occur;

    /**
     * Constructs a new object.
     */
    public DummySynthesizedOutputListener() {
        occur = new java.util.ArrayList<SynthesizedOutputEvent>();
    }

    /**
     * Retrieves the caught events.
     * @return caught events.
     */
    public List<SynthesizedOutputEvent> events() {
        return occur;
    }

    /**
     * Retrieves the number of caught events.
     * @return number of events.
     */
    public int size() {
        return occur.size();
    }

    /**
     * Waits until the given number of events have been colleced.
     * @param size number of events
     * @param timeout max. timeout to wait for events
     * @throws InterruptedException
     *         error waiting
     */
    public void waitSize(final int size, final long timeout)
        throws InterruptedException {
        long start = System.currentTimeMillis();
        while (size > occur.size()) {
            synchronized (occur) {
                occur.wait(timeout);
                long now = System.currentTimeMillis();
                if (size < occur.size() || (now - start > timeout)) {
                    Assert.fail(size + " not reached within " + timeout
                            + "msec");
                }
            }
        }
    }

    /**
     * Retrieves the event at the given position.
     * @param index the position of the event to retrieve
     * @return event at the given position
     */
    public SynthesizedOutputEvent get(final int index) {
        return occur.get(index);
    }

    /**
     * Removes all collected events.
     */
    public void clear() {
        occur.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(event);
        }
        occur.add(event);
        synchronized (occur) {
            occur.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputError(final ErrorEvent error) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(error);
        }
    }
}
