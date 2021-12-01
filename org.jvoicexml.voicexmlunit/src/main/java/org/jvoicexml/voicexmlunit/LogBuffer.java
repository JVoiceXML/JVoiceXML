/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.voicexmlunit;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;

/**
 * A buffer of log events per call
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class LogBuffer {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(LogBuffer.class);

    /** Time to wait before checking again if the waiting timeout expired. */
    private static final int WAIT_TIME = 100;

    /** The logical name of this log buffer. */
    private final String name;

    /** Captured logs. */
    private final List<LogEvent> events;

    /** Lock to sync on addition of events. */
    private final Object lock;

    /** Last read position. */
    private int position;

    /** 
     * {@code true} if this buffer has been configured in log4j and is ready to
     * use.
     */
    private boolean configured;
    
    /**
     * Constructs a new object
     * @param logicalName logicagl name of this buffer
     */
    LogBuffer(final String logicalName) {
        events = new java.util.ArrayList<LogEvent>();
        name = logicalName;
        lock = new Object();
    }

    /**
     * Checks if this log buffer is configured in the log4j configuration.
     * @return {@code true} if it was configured and can be used
     */
    public boolean isConfigured() {
        return configured;
    }

    /**
     * Marks this log buffer as configured
     * @param isConfigured {@code true} if this buffer is configured
     */
    public void setConfigured(boolean isConfigured) {
        configured = isConfigured;
    }

    /**
     * Prepares this log buffer for the next call.
     */
    public void init() {
        if (!configured) {
            LOGGER.warn("log buffer '" + name + "' is not configured in log4j");
        }
        synchronized (events) {
            events.clear();
            position = 0;
        }
    }

    /**
     * Waits until the provided log message is seen.
     * @param message the log message to look for
     * @throws InterruptedException error waiting for the next log
     */
    public void waitForLog(final String message) throws InterruptedException {
        do {
            final LogEvent current;
            synchronized (events) {
                current = events.get(position);
                position ++;
            }
            final Message currentMesage = current.getMessage();
            final String formattedMessage = currentMesage.getFormattedMessage();
            if (message.equals(formattedMessage)) {
                LOGGER.info("'" + name + "' saw log message '" + message + "'");
                return;
            }
            if (position >= events.size()) {
                synchronized (lock) {
                    lock.wait();
                }
            }
        } while (true);
    }

    /**
     * Waits until the provided log message is seen.
     * @param message the log message to look for
     * @param timeout the max timeout in msecs
     * @throws InterruptedException error waiting for the next log
     * @throws TimeoutException
     *             waiting time exceeded
     */
    public void waitForLog(final String message, final long timeout)
            throws InterruptedException, TimeoutException {
        final long maxEndTime = System.currentTimeMillis() + timeout;
        do {
            final LogEvent current;
            synchronized (events) {
                current = events.get(position);
                position ++;
            }
            final Message currentMesage = current.getMessage();
            final String formattedMessage = currentMesage.getFormattedMessage();
            if (message.equals(formattedMessage)) {
                LOGGER.info("'" + name + "' saw log message '" + message + "'");
                return;
            }
            if (position >= events.size()) {
                synchronized (lock) {
                    lock.wait(WAIT_TIME);
                }
            }
        } while (System.currentTimeMillis() < maxEndTime);
        throw new TimeoutException("timeout of '" + timeout
                + "' msec exceeded while waiting for '" + message + "'");
    }
    
    /**
     * Adds the given event to the list of known events.
     * @param event the event to add
     */
    public void add(final LogEvent event) {
        synchronized (events) {
            events.add(event);
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
    
    /**
     * Retrieves a copy of the list of captured events.
     * @return captured events
     */
    public List<LogEvent> getEvents() {
        final List<LogEvent> copy = new java.util.ArrayList<LogEvent>();
        synchronized (events) {
            copy.addAll(events);
        }
        return copy;
    }
    
}
