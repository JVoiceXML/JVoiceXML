/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.client;

import java.io.Serializable;

import javax.sound.sampled.AudioFormat;

/**
 * Marker for the start of a new audio stream to be delivered to the client's
 * audio device.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public class AudioFormatMessage
        implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -1699492305666412845L;

    /** The audio format. */
    private transient AudioFormat format;

    /** The audio encoding technique used by this format. */
    private String encoding;

    /**
     * The number of samples played or recorded per second, for sounds that
     * have this format.
     */
    private float sampleRate;

    /**
     * The number of bits in each sample of a sound that has this format.
     */
    private int sampleSizeInBits;

    /**
     * The number of audio channels in this format (1 for mono, 2 for stereo).
     */
    private int channels;

    /**
     * The number of bytes in each frame of a sound that has this format.
     */
    private int frameSize;

    /**
     * The number of frames played or recorded per second, for sounds that have
     * this format.
     */
    private float frameRate;

    /**
     * Indicates whether the audio data is stored in big-endian or
     * little-endian order.
     */
    private boolean bigEndian;

    /**
     * Constructs a new object.
     */
    public AudioFormatMessage() {
    }


    /**
     * Constructs a new object with the given audio format.
     * @param fmt AudioFormat
     */
    public AudioFormatMessage(final AudioFormat fmt) {
        setAudioFormat(fmt);
    }

    /**
     * Retrieves the audio format.
     * @return The audio format.
     */
    public AudioFormat getAudioFormat() {
        if (format != null) {
            return format;
        }

        final AudioFormat.Encoding enc = new AudioFormat.Encoding(encoding);

        format = new AudioFormat(enc, sampleRate, sampleSizeInBits, channels,
                                 frameSize, frameRate, bigEndian);

        return format;
    }

    /**
     * Sets the audio format.
     * @param fmt The audio format.
     */
    public void setAudioFormat(final AudioFormat fmt) {
        format = fmt;

        bigEndian = format.isBigEndian();
        channels = format.getChannels();
        final AudioFormat.Encoding enc = format.getEncoding();
        encoding = enc.toString();
        frameRate = format.getFrameRate();
        frameSize = format.getFrameSize();
        sampleRate = format.getSampleRate();
        sampleSizeInBits = format.getSampleSizeInBits();
    }
}
