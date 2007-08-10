/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/jvxml/SystemOutputFactory.java $
 * Version: $LastChangedRevision: 172 $
 * Date:    $LastChangedDate: 2006-12-14 09:35:30 +0100 (Do, 14 Dez 2006) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.media.protocol.PullSourceStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sun.speech.freetts.audio.AudioPlayer;

/**
 * FreeTTS {@link AudioPlayer} for the RTP protocol.
 *
 * @author Dirk Schnelle
 * @version $Revision: 172 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public final class RtpAudioPlayer implements AudioPlayer {
    /** The audio format to use. */
    private AudioFormat currentFormat;

    /** Type of the audio format to send over RTP. */
    private AudioFileFormat.Type outputType;

    /** RTP source stream to send the data. */
    private FreeTTSPullSourceStream stream;

    /** Buffer to capture the FreeTTS output. */
    private ByteArrayOutputStream out;

    /**
     * Constructs a new object.
     * @param ds the data source.
     */
    public RtpAudioPlayer(final FreeTTSDataSource ds) {
        PullSourceStream[] streams = ds.getStreams();
        stream = (FreeTTSPullSourceStream) streams[0];
        outputType = AudioFileFormat.Type.WAVE;
    }

    /**
     * {@inheritDoc}
     */
    public void begin(final int num) {
        out = new ByteArrayOutputStream(num);
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
    }

    /**
     * {@inheritDoc}
     */
    public boolean drain() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean end() {
        // This algorithm is not very efficient. Needs some cleanup.
        byte[] bytes = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        AudioInputStream ais = new AudioInputStream(in,
                currentFormat, bytes.length / currentFormat.getFrameSize());

        out = new ByteArrayOutputStream();
        try {
            AudioSystem.write(ais, outputType, out);
        } catch (IOException e) {
            return false;
        }
        byte[] waveBytes = out.toByteArray();
        in = new ByteArrayInputStream(waveBytes);
        stream.setInstream(in);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public AudioFormat getAudioFormat() {
        return currentFormat;
    }

    /**
     * {@inheritDoc}
     */
    public long getTime() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public float getVolume() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void pause() {
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
    }

    /**
     * {@inheritDoc}
     */
    public void resetTime() {
    }

    /**
     * {@inheritDoc}
     */
    public void resume() {
    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFormat(final AudioFormat format) {
        currentFormat = format;
    }

    /**
     * {@inheritDoc}
     */
    public void setVolume(final float level) {
    }

    /**
     * {@inheritDoc}
     */
    public void showMetrics() {
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
        write(bytes, 0, bytes.length);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean write(final byte[] bytes, final int start,
            final int offset) {
        out.write(bytes, start, offset);
        return true;
    }
}
