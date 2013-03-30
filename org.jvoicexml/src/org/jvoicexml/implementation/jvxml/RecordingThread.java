/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/jvxml/RecordingThread.java $
 * Version: $LastChangedRevision: 2851 $
 * Date:    $LastChangedDate: 2011-11-09 03:42:40 -0600 (mié, 09 nov 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jvxml;

import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link org.jvoicexml.implementation.Telephony} based on JSAPI 1.0.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2851 $
 * @since 0.6
 */
final class RecordingThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(RecordingThread.class);

    /** Read buffer size when reading from the microphone. */
    private static final int BUFFER_SIZE = 512;

    /** The output stream where to write the recording. */
    private final OutputStream out;

    /** The line used for recording. */
    private TargetDataLine line;

    /** The recording audio format. */
    private final AudioFormat format;

    /**
     * Constructs a new object.
     * @param stream the stream where to write the recording.
     * @param recordingFormat the audio format of the recording 
     */
    public RecordingThread(final OutputStream stream,
            final AudioFormat recordingFormat) {
        out = stream;
        format = recordingFormat;
        setDaemon(true);
        setName("RecordingThread");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recording started");
        }
        try {
            line = AudioSystem.getTargetDataLine(format);
            line.open();
            line.start();
            final byte[] buffer = new byte[BUFFER_SIZE];
            while (!isInterrupted()) {
                final int count = line.read(buffer, 0, buffer.length);
                if (count > 0) {
                    out.write(buffer, 0, count);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recording stopped");
        }
    }

    /**
     * Stops the recording.
     */
    public void stopRecording() {
        line.stop();
        line.close();
        interrupt();
    }
}
