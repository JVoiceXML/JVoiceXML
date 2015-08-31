/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $LastChangedDate $, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.mary;

import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import marytts.client.AudioFormatOutputStream;

import org.apache.log4j.Logger;

/**
 * Outputstream that retreives the audio format to use from mary.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public final class LineAudioFormatOutputStream extends AudioFormatOutputStream {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(LineAudioFormatOutputStream.class);

    /** The source data line to use for audio output. */
    private SourceDataLine line;

    /**
     * Constructs a new object.
     */
    public LineAudioFormatOutputStream() {
    }

    /**
     * Sets the format and starts a new line with it.
     * 
     * @param format
     *            the format to use.
     * @throws IOException
     *             error opening the line
     */
    @Override
    public void setFormat(final AudioFormat format) throws IOException {
        LOGGER.info("using audio format: " + format);
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                format);

        // Checks if line is supported
        if (!AudioSystem.isLineSupported(info)) {
            throw new IOException("Cannot open the requested line: "
                    + info.toString());
        }

        // Obtain, open and start the line.
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, AudioSystem.NOT_SPECIFIED);
            line.start();
        } catch (LineUnavailableException e) {
            throw new IOException(e.getMessage(), e);
        }

        // Set the output stream
        final OutputStream out = new LineOutputStream(line);
        setOutputStream(out);

        super.setFormat(format);
    }

    /**
     * Stops the current output.
     */
    public void cancel() {
        if (line != null) {
            line.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (line != null) {
            if (line.isRunning()) {
                line.drain();
                line.stop();
            }
            line.close();
        }
        super.close();
    }
}
