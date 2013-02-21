/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A thread that sleeps for a while before exiting the JVM.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
final class TerminationThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(TerminationThread.class);
    /**
     * Constructs a new object.
     */
    public TerminationThread() {
        setName("TerminationThread");
        setDaemon(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Termination thread started");
        }
        try {
            final long timeout = 10000;
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Termination thread interrupted");
            }
            return;
        }
        LOGGER.warn("Shutdown time exceeded. Exiting JVM...");
        System.exit(0);
    }
}
