/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/RecordingReceiverThread.java $
 * Version: $LastChangedRevision: 2776 $
 * Date:    $Date: 2011-08-29 06:45:09 -0500 (lun, 29 ago 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.jvxml.RecordingEvent;

/**
 * Asynchronous recording from the telephony device.
 *
 * <p>
 * This implementation simply waits until the recording time has passed and
 * creates an appropriate event, once the time has passed.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2776 $
 * @since 0.6
 */
final class RecordingReceiverThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(RecordingReceiverThread.class);

    /** The event handler to propagate the end of the recording. */
    private final EventHandler handler;

    /** Maximal recording time. */
    private final long maxTime;

    /** The output stream buffer for the recording. */
    private final ByteArrayOutputStream out;

    /**
     * Creates a new object.
     * @param eventHandler the event handler to propagate the end of the
     *          recording.
     * @param recordingTime maximal recording time.
     */
    public RecordingReceiverThread(final EventHandler eventHandler,
            final long recordingTime) {
        handler = eventHandler;
        maxTime = recordingTime;
        setDaemon(true);
        setName("RecordingReceiverThread");
        out = new ByteArrayOutputStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("waiting until record max timeout " + maxTime + " expired");
        try {
            Thread.sleep(maxTime);
        } catch (InterruptedException e) {
            final JVoiceXMLEvent event = new NoresourceError(e.getMessage(), e);
            handler.notifyEvent(event);
            return;
        }

        // Take what was recorded so far and ignore the rest.
        final byte[] buffer = out.toByteArray();
        final JVoiceXMLEvent event = new RecordingEvent(buffer);
        handler.notifyEvent(event);
    }

    /**
     * Retrieves the output stream buffer for the recording.
     * @return output stream.
     */
    public OutputStream getOutputStream() {
        return out;
    }
}
