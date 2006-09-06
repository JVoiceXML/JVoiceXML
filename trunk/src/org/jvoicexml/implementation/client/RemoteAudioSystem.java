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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

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
public final class RemoteAudioSystem
        implements Runnable {
    /** The port for the server socket. */
    private final int port;

    /** The address of the server socket. */
    private InetAddress address;

    /**
     * Sempaphore to control the sync the start of the server with the retrieval
     * of the {@link CallControl}.
     */
    private final Semaphore sem;

    /** The output thread. */
    private AudioOutputThread outputThread;

    /** The output stream. */
    private ObjectOutputStream out;

    /**
     * Constructs a new object.
     * @param prt Port number of the server.
     */
    public RemoteAudioSystem(final int prt) {
        port = prt;

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
        try {
            final ServerSocket server = new ServerSocket(port);
            address = InetAddress.getLocalHost();

            outputThread = new AudioOutputThread(this);
            outputThread.start();

            sem.release();

            final Socket client = server.accept();
            final InputStream in = client.getInputStream();
            final OutputStream outStream = client.getOutputStream();

            final ObjectInputStream input = new ObjectInputStream(in);
            out = new ObjectOutputStream(outStream);
            communicate(input);
            input.close();

            client.close();
            server.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Communicate with the VoiceXML interpreter.
     * @param in Input stream from the server.
     * @throws IOException
     *         Error accessing the stream.
     */
    private void communicate(final ObjectInputStream in)
            throws IOException {
        do {
            final Object object;
            try {
                object = in.readObject();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();

                return;
            }

            outputThread.addMessage(object);
        } while (true);
    }

    /**
     * Sends a message back to the VoiceXML interpreter.
     * @param message The message to send.
     */
    void sendMessage(final Object message) {
        try {
            out.writeObject(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
