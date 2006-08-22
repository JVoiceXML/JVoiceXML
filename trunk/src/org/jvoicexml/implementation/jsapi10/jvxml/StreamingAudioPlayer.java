/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;

import com.sun.speech.freetts.audio.AudioPlayer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An <code>AudioPlayer</code> that uses an <code>OutputStream</code> for
 * the audio output.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class StreamingAudioPlayer
        implements AudioPlayer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(StreamingAudioPlayer.class);

    /** The output for the <code>TTSEngine</code>. */
    private final OutputStream out;

    /** The audio format to use. */
    private AudioFormat defaultFormat =
            new AudioFormat(16000f, 16, 1, true, true);

    /**
     * Construct a new object.
     *
     * @param output
     * The output for the <code>TTSEngine</code>.
     */
    public StreamingAudioPlayer(final OutputStream output) {
        out = output;
    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFormat(final AudioFormat format) {
    }

    /**
     * {@inheritDoc}
     */
    public AudioFormat getAudioFormat() {
        return defaultFormat;
    }

    /**
     * {@inheritDoc}
     */
    public void pause() {
    }

    /**
     * {@inheritDoc}
     */
    public void resume() {
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean drain() {
        try {
            out.flush();
        } catch (IOException ioe) {
            LOGGER.error("Error flushing AudioPlayer", ioe);
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void begin(final int bytes) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean end() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void cancel() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        try {
            out.close();
        } catch (IOException ioe) {
            LOGGER.error("Error closing AudioPlayer", ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    public float getVolume() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public void setVolume(final float volume) {
    }

    /**
     * {@inheritDoc}
     */
    public long getTime() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public void resetTime() {
    }

    /**
     * {@inheritDoc}
     */
    public void startFirstSampleTimer() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean write(final byte[] bytes) {
        return write(bytes, 0, bytes.length);
    }

    /**
     * {@inheritDoc}
     */
    public boolean write(final byte[] bytes, final int start, final int end) {
        try {
            out.write(bytes, start, end);
            out.flush();
        } catch (IOException ioe) {
            LOGGER.error("Error writing to AudioPlayer", ioe);
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void showMetrics() {
    }
}
