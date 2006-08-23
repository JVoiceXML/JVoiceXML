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

import java.io.InputStream;
import java.net.InetSocketAddress;
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
    private InetSocketAddress address;

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
            address = new InetSocketAddress("localhost", port);

            sem.release();

            final Socket client = server.accept();
            final InputStream in = client.getInputStream();

            final int length = 4096;
            line.open(format, length);
            line.start();

            final byte[] buffer = new byte[length];
            int cnt;
            do {
                cnt = in.read(buffer, 0, length);
                if (cnt > 0) {
                    line.write(buffer, 0, cnt);
                }
            } while (cnt > 0);

            in.close();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            lue.printStackTrace();
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
        call.setAddress(address.getAddress());

        sem.release();

        return call;
    }
}
