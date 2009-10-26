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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;

/**
 * Test cases for {@link TextReceiverThread}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestTextReceiverThread
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
     * Set up the test environment.
     * @exception Exception
     *            error setting up the test environment
     * @exception ErrorEvent
     *            error setting up the test environment
     */
    @Before
    public void setUp() throws Exception, ErrorEvent {
        server = new TextServer(PORT);
        server.start();
        final InetAddress address = InetAddress.getLocalHost();
        final SocketAddress socketAddress =
            new InetSocketAddress(address, PORT);
        final Socket socket = new Socket();
        socket.connect(socketAddress);
        server.waitConnected();
        final TextTelephony telephony = new TextTelephony();
        receiver = new TextReceiverThread(socket, telephony);
        receiver.start();
        receiver.waitStarted();
        final TextSpokenInput input = new TextSpokenInput();
        input.addListener(this);
        input.startRecognition();
        receiver.setSpokenInput(input);
    }

    /**
     * Tear down the test environment.
     * @exception Exception
     *            error tearing down the test environment
     */
    @After
    public void tearDown() throws Exception {
        receiver.interrupt();
        server.stopServer();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextReceiverThread#run()}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testRun() throws Exception {
        final String userInput = "test1";
        server.sendInput(userInput);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        Assert.assertEquals(userInput, utterance);
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
