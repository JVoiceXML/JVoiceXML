/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.voicexmlunit;


import java.io.File;
import java.net.URI;

import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.voicexmlunit.io.Recording;

/**
 * Call simulates a real telephony call. This is done with creation of a new
 * JVoiceXML session and a TextServer that can be used to notice all events. You
 * have to call startDialog() in the started() event handler of your
 * TextListener, otherwise the Server may fail. Your TextListener instance is
 * registered with setListener() method. Lookup of JVoiceXml is done by help
 * from Voice, you may use getVoice() to do some initialization before. In case
 * of an assertion failure, you can stop the Server, therefore use the fail()
 * and getFailure() methods.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public final class Call implements Runnable {
    private URI dialog;
    private TextServer server;
    private Voice voice;
    private AssertionError error;

    public static int SERVER_PORT = 6000; // port number must be greater than
                                          // 1024
    public static int SERVER_PORT_RANDOMIZE_COUNT = 100; // 0 means a fixed port
                                                         // number
    public static long SERVER_WAIT = 5000;

    /** Synchronization lock. */
    private final Object lock;

    /**
     * Constructs a new call.
     *
     * @param uri
     *            URI of the dialog to call call
     */
    public Call(final URI uri) {
        super();
        dialog = uri;
        final int port = randomizePortForServer();
        server = new TextServer(port);
        lock = new Object();
    }

    /**
     * Constructs a new call.
     *
     * @param path
     *            the path to a local file
     */
    public Call(final String path) {
        this((new File(path)).toURI());
    }

    private int randomizePortForServer() {
        return (int) ((Math.random() * SERVER_PORT_RANDOMIZE_COUNT) + SERVER_PORT);
    }

    public void setListener(final TextListener listener) {
        server.addTextListener(listener);
    }

    /**
     * Get the Voice object This method tries best to get a valid object.
     *
     * @return the actual Voice object
     */
    public Voice getVoice() {
        if (voice == null) {
            setVoice(new Voice());
        }
        return voice;
    }

    /**
     * Set a custom Voice object
     *
     * @param voice
     *            the new Voice object
     */
    public void setVoice(final Voice voice) {
        this.voice = voice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (dialog == null) {
            return;
        }

        error = null;
        server.start();
        try {
            /* wait for the server */
            synchronized (lock) {
                server.waitStarted();
            }
            getVoice().call(server, dialog); // run the dialog
        } catch (Exception | ErrorEvent e) {
            fail(new AssertionError(e));
        } finally {
            server.stopServer();
        }
    }

    /**
     * Start the dialog.
     */
    public void startDialog() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * Starts a transaction to send input.
     *
     * @return transaction to use for the input
     */
    public Recording record() {
        final Voice voice = getVoice();
        final Session session = voice.getSession();
        return new Recording(server, session);
    }

    /**
     * Sets the call into failure state and terminates the call process.
     *
     * @param error
     *            the error that has caused the failure
     */
    public void fail(final AssertionError error) {
        if (this.error == null) { // only the first error
            server.interrupt();
            getVoice().close();
            this.error = error;
        }
    }

    public AssertionError getFailure() {
        return error;
    }
}
