/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.systemtest;

import org.apache.log4j.Logger;

/**
 * Test executer timeout monitor for the active connection to JVoiceXML.
 * @author lancer
 * @author Dirk Schnelle-Walka
 *
 */
final class ConnectionTimeoutMonitor extends Thread implements StatusListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ConnectionTimeoutMonitor.class);

    /** The listener to notify in case of a timeout. */
    private final TimeoutListener executor;

    /** Max time to wait. */
    private final long time;

    /** The current status. */
    private ClientConnectionStatus status;

    /** <code>true</code> if the monitor has been stopped. */
    private boolean stopped;

    /**
     * Constructs a new object.
     * @param lst the listener to inform in case of a timeout
     * @param maxTime max time to wait
     */
    public ConnectionTimeoutMonitor(final TimeoutListener lst,
            final long maxTime) {
        setDaemon(true);
        setName("TimeoutMonitor");
        executor = lst;
        time = maxTime;
    }

    /**
     * wait lock.
     */
    private final Object waitLock = new Object();

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final ClientConnectionStatus oldStatus,
            final ClientConnectionStatus newStatus) {
        status = newStatus;
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("timeout monitor started for max time: " + time);
        long markSleepTime = System.currentTimeMillis();
        while ((status != ClientConnectionStatus.DONE) && !stopped) {
            synchronized (waitLock) {
                try {
                    waitLock.wait(300);
                } catch (InterruptedException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("wait interupted", e);
                    }
                    return;
                }
            }
            long wakeupTime = System.currentTimeMillis();
            if (wakeupTime - markSleepTime >= time) {
                executor.timeout(time);
                stopped = true;
            }
        }
        LOGGER.info("timeout monitor stopped");
    }

    /**
     * Stops the timeout monitor.
     */
    public void stopMonitor() {
        stopped = true;
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
    }
}
