/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jvxml;

import org.apache.log4j.Logger;

/**
 * Reaper for external resources to return them after a timeout when the
 * session has closed.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
class ImplementationPlatformReaper extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ImplementationPlatformReaper.class);

    /** Delay to wait before returning the platform. */
    private static final long REAPING_DELAY = 120 * 1000;
    
    /** The platform. */
    private final JVoiceXmlImplementationPlatform platform;

    /** The waiting lock. */
    private Object wait;
    
    /** Flag if the platform closed normally. */
    private boolean stopReaping;

    /**
     * Creates a new object.
     * @param impl the implementation platform
     */
    ImplementationPlatformReaper(
            final JVoiceXmlImplementationPlatform impl) {
        platform = impl;
        setDaemon(true);
    }

    @Override
    public void run() {
        LOGGER.info("implementation platform reaper started");
        synchronized (wait) {
            try {
                wait.wait(REAPING_DELAY);
                if (!stopReaping) {
                    LOGGER.info("delay exceeded: triggering hangup");
                    platform.telephonyCallHungup(null);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        LOGGER.info("reaper stopped");
    }
    
    /**
     * Stops reaping if the platform closed normally.
     */
    public void stopReaping() {
        synchronized (wait) {
            LOGGER.info("stopping reaper");
            stopReaping = true;
            wait.notifyAll();
        }
    }
}
