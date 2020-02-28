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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Reaper for external resources to return them after a timeout when the
 * session has closed.
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
class ImplementationPlatformReaper extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(ImplementationPlatformReaper.class);

    /** Delay to wait before returning the platform. */
    private static final long REAPING_DELAY = 120 * 1000;
    
    /** The platform. */
    private final JVoiceXmlImplementationPlatform platform;

    /** The waiting lock. */
    private Object wait;
    
    /** Flag if the platform closed normally. */
    private boolean stopReaping;

    /** The used input. */
    private final SpokenInput input;
    /** The used output. */
    private final SynthesizedOutput output;
    
    /**
     * Creates a new object.
     * @param impl the implementation platform
     */
    ImplementationPlatformReaper(
            final JVoiceXmlImplementationPlatform impl, final JVoiceXmlUserInput in,
            final JVoiceXmlSystemOutput out) {
        platform = impl;
        if (in != null) {
            input = in.getSpokenInput();
        } else {
            input = null;
        }
        if (out != null) {
            output = out.getSynthesizedOutput();
        } else {
            output = null;
        }
        setName("platform-reaper");
        setDaemon(true);
    }

    @Override
    public void run() {
        LOGGER.info("implementation platform reaper started");
        synchronized (wait) {
            try {
                wait.wait(REAPING_DELAY);
                if (!stopReaping) {
                    LOGGER.info("delay exceeded: cleaning up");
                    forceReturnResources();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        LOGGER.info("reaper stopped");
    }
    
    /**
     * Force return of the external resources.
     * 
     * @since 0.7.9
     */
    private void forceReturnResources() {
        LOGGER.warn("force returning resouces");
        if ((input != null) && input.isBusy()) {
            input.stopRecognition();
        }
        if ((output != null) && output.isBusy()) {
            try {
                output.cancelOutput(BargeInType.SPEECH);
            } catch (NoresourceError e) {
                LOGGER.warn("error canceling output while reaping", e);
            }
        }
        // Try again...
        platform.telephonyCallHungup(null);
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
