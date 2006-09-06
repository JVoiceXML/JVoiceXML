/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $LastChangedBy$
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

import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Audio output.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
final class AudioOutputThread
        extends Thread {
    /** The capacity of the queue. */
    private static final int QUEUE_CAPACITY = 50;

    /** The message queue. */
    private final BlockingQueue<Object> queue;

    /** The used line. */
    private SourceDataLine line;

    /** The used audio format. */
    private AudioFormat format;

    /** The central instance. */
    private final RemoteAudioSystem remote;

    /**
     * Constructs a new object.
     * @param remoteAudio The central instance.
     */
    public AudioOutputThread(final RemoteAudioSystem remoteAudio) {
        setDaemon(true);

        queue = new java.util.concurrent.ArrayBlockingQueue<Object>(
                QUEUE_CAPACITY);
        remote = remoteAudio;
    }

    /**
     * Adds the given message to the queue.
     * @param message The message to add.
     */
    public void addMessage(final Object message) {
        try {
            queue.put(message);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        while (true) {
            final Object object;
            try {
                object = queue.take();
            } catch (InterruptedException ie) {
                ie.printStackTrace();

                return;
            }

            if (object instanceof AudioMessage) {
                final AudioMessage msg = (AudioMessage) object;
                playAudio(msg);
            } else if (object instanceof AudioStartMessage) {
                // Ignore the start message.
            } else if (object instanceof MarkerMessage) {
                line.drain();
                remote.sendMessage(object);
            } else if (object instanceof AudioFormatMessage) {
                final AudioFormatMessage msg = (AudioFormatMessage) object;
                final AudioFormat fmt = msg.getAudioFormat();
                openLine(fmt);
            } else if (object instanceof AudioEndMessage) {
                line.drain();
                remote.sendMessage(object);
            } else {
                System.err.println("cannot handle object " + object);
            }

        }
    }

    /**
     * Plays the received audio in the local audio system.
     * @param message The audio data to play.
     */
    private void playAudio(final AudioMessage message) {
        byte[] buffer = message.getBuffer();
        line.write(buffer, 0, buffer.length);
    }

    /**
     * Opens the line in the given format.
     * @param fmt The format to use.
     */
    private void openLine(final AudioFormat fmt) {
        if (line != null) {
            line.drain();
            line.stop();
            line.close();
        }

        line = null;

        format = fmt;
        System.out.println(format);
        final DataLine.Info dataLineInfo =
                new DataLine.Info(SourceDataLine.class, format);

        try {
            line = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            lue.printStackTrace();

            line = null;
        }
        try {
            line.open(format, 4096);
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();

            return;

        }
        line.start();
    }
}
