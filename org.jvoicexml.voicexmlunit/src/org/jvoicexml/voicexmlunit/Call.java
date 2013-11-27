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


import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.OutputMessage;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Call simulates a real telephony call. This is done with creation of a new
 * JVoiceXML session and a TextServer that can be used to notice all events.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public final class Call  {
    /** URI of the dialog to call. */
    private URI dialog;
    /** The text server. */
    private TextServer server;
    /** Reference to JVoiceXml. */
    private VoiceXmlAccessor vxml;
    /** Buffered messages from JVoiceXml. */
    private final OutputMessageBuffer outputBuffer;
    /** Monitor to wait until JVoiceXML is ready to accept input. */
    private InputMonitor inputMonitor;

    public static int SERVER_PORT = 6000; // port number must be greater than
                                          // 1024
    public static int SERVER_PORT_RANDOMIZE_COUNT = 100; // 0 means a fixed port
                                                         // number
    public static long SERVER_WAIT = 5000;

    /**
     * Constructs a new call.
     */
    public Call() {
        final int port = randomizePortForServer();
        server = new TextServer(port);
        outputBuffer = new OutputMessageBuffer();
        server.addTextListener(outputBuffer);
        inputMonitor = new InputMonitor();
        server.addTextListener(inputMonitor);
    }

    /**
     * Creates a random port number for the text server.
     * @return random part number
     */
    private int randomizePortForServer() {
        return (int) ((Math.random() * SERVER_PORT_RANDOMIZE_COUNT)
                + SERVER_PORT);
    }

    /**
     * Adds the given listener of messages received from the JVoiceXML.
     * @param listener the listener to add
     */
    public void addTextListener(final TextListener listener) {
        server.addTextListener(listener);
    }

    /**
     * Set a custom Voice object.
     *
     * @param voice
     *            the new Voice object
     */
    public void setVoice(final VoiceXmlAccessor voice) {
        vxml = voice;
    }

    /**
     * Calls the application identified by the given uri.
     * @param uri URI of the application to call
     */
    public void call(final URI uri) {
        dialog = uri;

        server.start();
        try {
            // wait for the server
             server.waitStarted();
             // run the dialog
             vxml = new VoiceXmlAccessor();
             vxml.call(server, dialog);
        } catch (Exception | ErrorEvent e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Retrieves the next output.
     * @return the next output that has been captured
     */
    public SsmlDocument getNextOutput() {
        OutputMessage message = null;
        try {
            message = outputBuffer.nextMessage();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        if (message instanceof Output) {
            final Output output = (Output) message;
            return output.getDocument();
        }
        throw new AssertionError("next message is no output message");
    }

    /**
     * Checks if the next output matches the given utterance.
     * @param utterance the expected utterance
     */
    public void hears(final String utterance) {
        final SsmlDocument document = getNextOutput();
        final Speak speak = document.getSpeak();
        final String output = speak.getTextContent();
        Assert.assertEquals(utterance, output);
    }

    /**
     * Sends the given utterance to JVoiceXML.
     * @param utterance the utterance to send
     */
    public void say(final String utterance) {
        try {
        inputMonitor.waitUntilExpectingInput();
        server.sendInput(utterance);
        } catch (InterruptedException | IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Sends the given utterance to JVoiceXML.
     * @param digits the digits to enter
     */
    public void enter(final String digits) {
        try {
            inputMonitor.waitUntilExpectingInput();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        final Session session = vxml.getSession();
        CharacterInput input = null;
        try {
            input = session.getCharacterInput();
        } catch (NoresourceError | ConnectionDisconnectHangupEvent e) {
            throw new AssertionError(e);
        }
        for (int i = 0; i < digits.length(); i++) {
            final char ch = digits.charAt(i);
            input.addCharacter(ch);
        }
    }

    /**
     * Issues a hangup event.
     * @throws JVoiceXMLEvent 
     *         error hanging up
     */
    public void hangup() throws JVoiceXMLEvent {
        final Session session = vxml.getSession();
        session.hangup();
    }
}
