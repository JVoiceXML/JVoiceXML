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

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

import org.jvoicexml.implementation.CallControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.*;
import javax.sound.sampled.spi.*;

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

    /**
     * Constructs a new object.
     * @param prt Port number of the server.
     */
    public RemoteAudioSystem(final int prt) {
        port = prt;
    }

    /**
     * {@inheritDoc}
     *
     * Starts this audio system.
     */
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(port);
            address = new InetSocketAddress("localhost", port);

            final Socket client = server.accept();

            /** @todo THis does not work. */
            final InputStream in = client.getInputStream();
            final BufferedInputStream bufin = new BufferedInputStream(in);
            final Clip clip = AudioSystem.getClip();
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            final AudioInputStream audio =
                    AudioSystem.getAudioInputStream(bufin);

            clip.open(audio);
            clip.start();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        } catch (javax.sound.sampled.LineUnavailableException lue) {
            lue.printStackTrace();
        } catch (javax.sound.sampled.UnsupportedAudioFileException uaf) {
            uaf.printStackTrace();
        }
    }

    /**
     * Retrieves the <code>CallControl</code> to use for communication
     * with this client.
     * @return <code>CallControl</code> to use.
     */
    public CallControl getCallControl() {
        // Allow the thread to start.
        try {
            Thread.sleep(300);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        final SimpleCallControl call = new SimpleCallControl();

        call.setPort(port);
        call.setAddress(address.getAddress());

        return call;
    }
}
