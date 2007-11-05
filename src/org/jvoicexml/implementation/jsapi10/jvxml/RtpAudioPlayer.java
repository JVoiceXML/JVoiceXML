/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
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

import javax.media.MediaException;
import javax.media.protocol.PullSourceStream;
import javax.media.rtp.SessionManagerException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;
import org.jvoicexml.client.rtp.RtpConfiguration;

import com.sun.speech.freetts.audio.AudioPlayer;

/**
 * FreeTTS {@link AudioPlayer} for the RTP protocol.
 *
 * @author Dirk Schnelle
 * @version $Revision$
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
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(RtpAudioPlayer.class);

    /** The RTP remote client connection. */
    private final RtpConfiguration client;

    /** The audio format to use. */
    private AudioFormat currentFormat;

    /** Type of the audio format to send over RTP. */
    private AudioFileFormat.Type outputType;

    /** RTP source stream to send the data. */
    private FreeTTSPullSourceStream stream;

    /** The data source. */
    private FreeTTSDataSource datasource;

    /** Buffer to capture the FreeTTS output. */
    private ByteArrayOutputStream out;

    /**
     * Constructs a new object.
     * @param remoteClient the RTP remote client connection.
     */
    public RtpAudioPlayer(final RtpConfiguration remoteClient) {
        client = remoteClient;
        datasource = new FreeTTSDataSource();
        PullSourceStream[] streams = datasource.getStreams();

        stream = (FreeTTSPullSourceStream) streams[0];
        outputType = AudioFileFormat.Type.WAVE;
    }

    /**
     * {@inheritDoc}
     */
    public void begin(final int num) {
        System.out.println("*** start with " + num);
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

        final RtpServer server;
        try {
            server = RtpServerManager.getServer(client);
        } catch (IOException e) {
            LOGGER.error("error creating RTP server", e);
            return false;
        } catch (SessionManagerException e) {
            LOGGER.error("error creating RTP server", e);
            return false;
        } catch (MediaException e) {
            LOGGER.error("error creating RTP server", e);
            return false;
        }

        try {
            server.initSendStream(datasource);
            server.startSending();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MediaException e) {
            e.printStackTrace();
        }

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

    /**
     * Selector for the data source.
     * @return the data source
     */
    public FreeTTSDataSource getDatasource() {
        return datasource;
    }
}
