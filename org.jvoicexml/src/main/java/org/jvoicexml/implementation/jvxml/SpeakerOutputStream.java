/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * A simple {@link OutputStream} that writes the data to the local speaker.
 *
 * @author Dirk Schnelle-Walka
 *
 */
final class SpeakerOutputStream extends OutputStream
        implements LineListener {
    /** The current source data line. */
    private final SourceDataLine line;

    /**
     * Constructs a new object.
     * 
     * @param format
     *            the audio format to use
     * @throws LineUnavailableException
     *             error opening the line with the given format
     */
    SpeakerOutputStream(final AudioFormat format)
            throws LineUnavailableException {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.addLineListener(this);
        line.open(format);
    }

    /**
     * Opens the line that can be used to stream the audio to the speaker.
     * 
     * @throws IOException
     *             error opening the line with the current format
     */
    public void open() throws IOException {
        line.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int b) throws IOException {
        final byte[] bytes = new byte[1];
        bytes[0] = (byte) b;
        line.write(bytes, 0, bytes.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] bytes, final int offset, final int len)
            throws IOException {
        line.write(bytes, offset, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] bytes) throws IOException {
        line.write(bytes, 0, bytes.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        line.drain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (line != null) {
            line.close();
        }
        super.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final LineEvent event) {
        if ((event.getType() == LineEvent.Type.CLOSE)
                || (event.getType() == LineEvent.Type.STOP)) {
            // TODO evaluate the events
        }
    }
}
