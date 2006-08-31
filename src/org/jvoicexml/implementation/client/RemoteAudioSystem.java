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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.jvoicexml.implementation.CallControl;

/**
 * Audio system to be used remotely by the VoiceXML interpreter.
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
public class RemoteAudioSystem
        implements Runnable {
    /** The port for the server socket. */
    private final int port;

    /** The address of the server socket. */
    private InetAddress address;

    /** The used line. */
    private SourceDataLine line;

    /** The used audio format. */
    private AudioFormat format;

    /**
     * Sempaphore to control the sync the start of the server with the retrieval
     * of the {@link CallControl}.
     */
    private final Semaphore sem;

    /**
     * Constructs a new object.
     * @param prt Port number of the server.
     */
    public RemoteAudioSystem(final int prt) {
        port = prt;

        format = new AudioFormat(16000, 16, 1, true, true);
        final DataLine.Info dataLineInfo =
                new DataLine.Info(SourceDataLine.class, format);

        try {
            line = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            lue.printStackTrace();

            line = null;
        }

        sem = new Semaphore(1);
        try {
            sem.acquire();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Starts this audio system.
     */
    public void run() {
        if (line == null) {
            return;
        }

        try {
            final ServerSocket server = new ServerSocket(port);
            address = InetAddress.getLocalHost();

            sem.release();

            final Socket client = server.accept();
            final InputStream in = client.getInputStream();
            final OutputStream out = client.getOutputStream();

            final int length = 4096;
            line.open(format, length);
            line.start();

            final ObjectInputStream input = new ObjectInputStream(in);
            final ObjectOutputStream output = new ObjectOutputStream(out);
            communicate(input, output);
            input.close();

            client.close();
            server.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            lue.printStackTrace();
        }
    }

    /**
     * Communicate with the VoiceXML interpreter.
     * @param in Input stream from the server.
     * @param out Output stream to the server.
     * @throws IOException
     *         Error accessing the stream.
     */
    private void communicate(final ObjectInputStream in,
                             final ObjectOutputStream out)
            throws IOException {
        do {
            final Object object;
            try {
                object = in.readObject();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();

                return;
            }

            if (object instanceof AudioMessage) {
                final AudioMessage msg = (AudioMessage) object;
                playAudio(msg);
            } else if (object instanceof AudioStartMessage) {
                // Ignore the start message.
            } else if (object instanceof AudioEndMessage) {
                line.drain();
                out.writeObject(object);
            } else {
                System.err.println("cannot handle object " + object);
            }
        } while (true);
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
     * Retrieves the <code>CallControl</code> to use for communication
     * with this client.
     * @return <code>CallControl</code> to use.
     */
    public CallControl getCallControl() {
        try {
            sem.acquire();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        final SimpleCallControl call = new SimpleCallControl();

        call.setPort(port);
        call.setAddress(address);

        sem.release();

        return call;
    }
}
