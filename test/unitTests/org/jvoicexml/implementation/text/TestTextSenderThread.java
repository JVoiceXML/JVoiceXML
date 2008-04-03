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

import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test cases for {@link TextSenderThread}.
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

public final class TestTextSenderThread extends TestCase
    implements TextListener {
    /** Maximal number of milliseconds to wait for a receipt. */
    private static final int MAX_WAIT = 1000;

    /** Port number to use. */
    private static final int PORT = 4244;

    /** Text server to receive the data. */
    private TextServer server;

    /** The sender to test. */
    private TextSenderThread sender;

    /** Mutex to wait for a receipt. */
    private final Object lock = new Object();

    /** Last received object. */
    private Object receivedObject;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        server = new TextServer(PORT);
        server.start();
        server.addTextListener(this);

        final InetAddress address = InetAddress.getLocalHost();
        final SocketAddress socketAddress =
            new InetSocketAddress(address, PORT);
        final Socket socket = new Socket();
        socket.connect(socketAddress);
        final TextTelephony telephony = new TextTelephony();
        sender = new TextSenderThread(socket, telephony);
        sender.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        sender.sendBye();
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        server.stopServer();
        super.tearDown();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextSenderThread#sendData(org.jvoicexml.SpeakableText)}.
     * @exception Exception test failed.
     */
    public void testSendData() throws Exception {
        final String test1 = "test1";
        final SpeakableText speakable1 = new SpeakablePlainText(test1);
        sender.sendData(speakable1);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        assertEquals(test1, receivedObject);

        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("test2");
        final SpeakableText speakable2 = new SpeakableSsmlText(document);
        sender.sendData(speakable2);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
//        assertEquals(document, receivedObject);

        for (int i=0; i<10; i++) {
            final String test3 = "test" + i;
            final SpeakableText speakable3 = new SpeakablePlainText(test3);
            sender.sendData(speakable3);
        }
        int i = 0;
        while (!receivedObject.equals("test9")) {
            synchronized (lock) {
                lock.wait(MAX_WAIT);
            }
            ++i;
            if (i > 100) {
                fail("last object not received");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void outputSsml(final SsmlDocument document) {
        receivedObject = document;
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void outputText(final String text) {
        receivedObject = text;
        synchronized (lock) {
            lock.notify();
        }
    }
}
