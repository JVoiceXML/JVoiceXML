/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import junit.framework.TestCase;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;

/**
 * Test cases for {@link TextReceiverThread}.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestTextReceiverThread extends TestCase
    implements SpokenInputListener {
    /** Maximal number of milliseconds to wait for a receipt. */
    private static final int MAX_WAIT = 1000;

    /** Port number to use. */
    private static final int PORT = 4244;

    /** Text server to send the data. */
    private TextServer server;

    /** The receiver to test. */
    private TextReceiverThread receiver;

    /** Object lock to wait for a result. */
    private final Object lock;

    /** The last received utterance. */
    private String utterance;

    /**
     * Constructs a new object.
     */
    public TestTextReceiverThread() {
        lock = new Object();
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        server = new TextServer(PORT);
        server.start();

        final InetAddress address = InetAddress.getLocalHost();
        final SocketAddress socketAddress =
            new InetSocketAddress(address, PORT);
        final Socket socket = new Socket();
        socket.connect(socketAddress);
        final TextTelephony telephony = new TextTelephony();
        receiver = new TextReceiverThread(socket, telephony);
        receiver.start();
        receiver.waitStarted();
        final TextSpokenInput input = new TextSpokenInput();
        input.addListener(this);
        try {
            input.startRecognition();
        } catch (NoresourceError e) {
            fail(e.getMessage());
        } catch (BadFetchError e) {
            fail(e.getMessage());
        }
        receiver.setSpokenInput(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        receiver.interrupt();
        server.stopServer();

        super.tearDown();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextReceiverThread#run()}.
     * @exception Exception
     *            test failed.
     */
    public void testRun() throws Exception {
        final String userInput = "test1";
        server.sendInput(userInput);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        assertEquals(userInput, utterance);
    }

    /**
     * {@inheritDoc}
     */
    public void inputStatusChanged(final SpokenInputEvent event) {
        final int id = event.getEvent();
        if (id == SpokenInputEvent.RESULT_ACCEPTED) {
            final RecognitionResult result =
                (RecognitionResult) event.getParam();
            utterance = result.getUtterance();
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
}
