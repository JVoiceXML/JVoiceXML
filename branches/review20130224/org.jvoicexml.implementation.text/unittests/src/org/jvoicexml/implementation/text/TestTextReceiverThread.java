/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/unittests/org/jvoicexml/implementation/text/TestTextReceiverThread.java $
 * Version: $LastChangedRevision: 2564 $
 * Date:    $Date: 2011-02-04 09:08:55 +0100 (Fr, 04 Feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2011-02-04 09:08:55 +0100 (Fr, 04 Feb 2011) $, Dirk Schnelle-Walka, project lead
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

import java.net.Socket;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.client.text.TextConnectionInformation;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;

/**
 * Test cases for {@link TextReceiverThread}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2564 $
 * @since 0.6
 */
public final class TestTextReceiverThread
    implements SpokenInputListener {
    /** Maximal number of milliseconds to wait for a receipt. */
    private static final int MAX_WAIT = 9000;

    /** Time to wait for the socket. */
    private static final long SOCKET_WAIT = 100;

    /** Port number to use. */
    private static final int PORT = 4242;

    /** Text server to send the data. */
    private TextServer server;

    /** The receiver to test. */
    private TextReceiverThread receiver;

    /** The text spoken input. */
    private TextSpokenInput input;

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
        TextConnectionInformation info = (TextConnectionInformation) server.getConnectionInformation();
        final Socket socket = new Socket();
        Thread.sleep(SOCKET_WAIT); // give time to create the socket
        info.connectClient(socket);
        server.waitConnected();
        final TextTelephony telephony = new TextTelephony();
        receiver = new TextReceiverThread(socket, telephony);
        receiver.start();
        receiver.waitStarted();
        input = new TextSpokenInput();
        input.addListener(this);
        receiver.setSpokenInput(input);
    }

    /**
     * Tear down the test environment.
     * @exception Exception
     *            error tearing down the test environment
     */
    @After
    public void tearDown() throws Exception {
        if (receiver != null) {
            receiver.interrupt();
        }
        server.stopServer();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextReceiverThread#run()}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testRun() throws Exception, JVoiceXMLEvent {
        final String userInput = "test1";
        input.mockGrammar(userInput);
        input.startRecognition(null, null);
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

    @Override
    public void inputError(final ErrorEvent error) {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
