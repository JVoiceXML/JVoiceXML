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

import junit.framework.TestCase;

import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SpokenInputEvent;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.test.implementationplatform.DummySystemOutput;
import org.jvoicexml.test.implementationplatform.DummyUserInput;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test case for {@link TextTelephony}.
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

public final class TestTextTelephony extends TestCase
    implements TextListener, SpokenInputListener {
    /** Maximal number of milliseconds to wait for a receipt. */
    private static final int MAX_WAIT = 1000;

    /** Port number to use. */
    private static final int PORT = 4244;

    /** Text server to receive the data. */
    private TextServer server;

    /** The telephony object to test. */
    private TextTelephony telephony;

    /** Object lock to wait for a result. */
    private final Object lock;

    /** Last received object. */
    private Object receivedObject;

    /**
     * Creates a new object.
     */
    public TestTextTelephony() {
        lock = new Object();
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        server = new TextServer(PORT);
        server.start();
        server.addTextListener(this);

        final RemoteClient client = server.getRemoteClient();
        telephony = new TextTelephony();
        telephony.connect(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        final RemoteClient client = server.getRemoteClient();
        telephony.disconnect(client);

        server.stopServer();
        super.tearDown();
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextTelephony#play(org.jvoicexml.SystemOutput, java.util.Map)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed.
     */
    public void testPlay() throws Exception, JVoiceXMLEvent {
        final TextSynthesizedOutput textOutput = new TextSynthesizedOutput();
        final SystemOutput output = new DummySystemOutput(textOutput);
        final String prompt = "testPlay";
        final SpeakableText speakable = new SpeakablePlainText(prompt);
        textOutput.queueSpeakable(speakable, false, null);
        telephony.play(output, null);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        assertEquals(prompt, receivedObject);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextTelephony#record(org.jvoicexml.UserInput, java.util.Map)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed.
     */
    public void testRecord() throws Exception, JVoiceXMLEvent {
        final TextSpokenInput textInput = new TextSpokenInput();
        textInput.startRecognition();
        textInput.addListener(this);
        final UserInput input = new DummyUserInput(textInput);
        final String utterance = "testRecord";
        telephony.record(input, null);
        assertTrue(telephony.isBusy());
        server.sendInput(utterance);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        assertEquals(utterance, receivedObject);
    }

    /**
     * {@inheritDoc}
     */
    public void outputSsml(final SsmlDocument document) {
        receivedObject = document;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void outputText(final String text) {
        receivedObject = text;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void inputStatusChanged(final SpokenInputEvent event) {
        final int id = event.getEvent();
        if (id == SpokenInputEvent.RESULT_ACCEPTED) {
            receivedObject = event.getParam();
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
}
