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
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Call simulates a real telephony call. This is done with creation of a new
 * JVoiceXML session and a TextServer that can be used to notice all events.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public final class Call  {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Call.class);
    /** Known call listeners. */
    private final Collection<CallListener> listeners;
    /** The text server. */
    private TextServer server;
    /** Used port number. */
    private int portNumber;
    /** Buffered messages from JVoiceXml. */
    private OutputMessageBuffer outputBuffer;
    /** Monitor to wait until JVoiceXML is ready to accept input. */
    private InputMonitor inputMonitor;

    /**
     * Server port number to use. Port number must be greater than 1024.
     */
    public static final int DEFAULT_SERVER_PORT = 6000;
    /** The active session. */
    private Session session;

    /**
     * Constructs a new object with the default server port.
     */
    public Call() {
        this(DEFAULT_SERVER_PORT);
    }

    /**
     * Constructs a new call.
     * @param hostname the hostname to use for the {@link TextServer}
     * @param port number to use for the {@link TextServer}.
     */
    public Call(final String hostname, final int port) {
        portNumber = port;
        server = new TextServer(hostname, portNumber);
        outputBuffer = new OutputMessageBuffer();
        server.addTextListener(outputBuffer);
        inputMonitor = new InputMonitor();
        server.addTextListener(inputMonitor);
        listeners = new java.util.ArrayList<CallListener>();
    }

    /**
     * Constructs a new call.
     * @param port number to use for the {@link TextServer}.
     */
    public Call(final int port) {
        this(null, port);
    }

    /**
     * Adds the given listener of messages received from the JVoiceXML.
     * This allows for further investigation of the behavior.
     * @param listener the listener to add
     */
    public void addTextListener(final TextListener listener) {
        server.addTextListener(listener);
    }

    /**
     * Calls the application identified by the given uri.
     * @param uri URI of the application to call
     */
    public void call(final URI uri) {
        LOGGER.info("calling '" + uri + "'");
        try {
            final Context context = new InitialContext();
            final JVoiceXml jvxml = (JVoiceXml) context.lookup("JVoiceXml");

            // Start the text server
            server.start();
            server.waitStarted();

            // run the dialog
            final ConnectionInformation info =
                   server.getConnectionInformation();
            session = jvxml.createSession(info);
            session.call(uri);
            for (CallListener listener : listeners) {
                listener.called(uri);
            }
        } catch (Exception | ErrorEvent e) {
            final AssertionError error = new AssertionError(e);
            notifyError(error);
            throw error;
        }
    }

    /**
     * Retrieves the next output. This method is useful if the output should be
     * examined in more detail
     * @return the next output that has been captured
     */
    public SsmlDocument getNextOutput() {
        Assert.assertNotNull("no active session", session);
        try {
            final SsmlDocument output = outputBuffer.nextMessage();
            for (CallListener listener : listeners) {
                listener.heard(output);
            }
            LOGGER.info("heard '" + output + "'");
            return output;
        } catch (InterruptedException | JVoiceXMLEvent e) {
            JVoiceXMLEvent lastError;
            try {
                lastError = session.getLastError();
            } catch (ErrorEvent ex) {
                final AssertionError error = new AssertionError(ex);
                notifyError(error);
                throw error;
            }
            if (lastError != null) {
                final AssertionError error = new AssertionError(lastError);
                notifyError(error);
                throw error;
            }
            final AssertionError error = new AssertionError(e);
            notifyError(error);
            throw error;
        }
    }

    /**
     * Retrieves the next output. This method is useful if the output should be
     * examined in more detail
     * @param timeout the timeout to wait at max in msec, waits forever, if
     *          timeout is zero
     * @return the next output that has been captured
     */
    public SsmlDocument getNextOutput(final long timeout) {
        Assert.assertNotNull("no active session", session);
        try {
            final SsmlDocument output = outputBuffer.nextMessage(timeout);
            for (CallListener listener : listeners) {
                listener.heard(output);
            }
            LOGGER.info("heard '" + output + "'");
            return output;
        } catch (InterruptedException | TimeoutException e) {
            final AssertionError error = new AssertionError(e);
            notifyError(error);
            throw error;
        }
    }

    /**
     * Waits for the next output and checks if this output matches the given
     * utterance.
     * @param utterance the expected utterance
     */
    public void hears(final String utterance) {
        Assert.assertNotNull("no active session", session);
        final SsmlDocument document = getNextOutput();
        final Speak speak = document.getSpeak();
        final String output = speak.getTextContent();
        Assert.assertEquals(utterance, output);
    }

    /**
     * Waits for the next output and checks if this output matches the given
     * utterance. The output is expected to arrive in max timeout msec.
     * @param utterance the expected utterance
     * @param timeout the timeout to wait at max in msec, waits forever, if
     *          timeout is zero
     */
    public void hears(final String utterance, final long timeout) {
        Assert.assertNotNull("no active session", session);
        final SsmlDocument document = getNextOutput(timeout);
        final Speak speak = document.getSpeak();
        final String output = speak.getTextContent();
        Assert.assertEquals(utterance, output);
    }

    /**
     * Waits until an input is expected and then sends the given utterance to
     * JVoiceXML.
     * @param utterance the utterance to send
     */
    public void say(final String utterance) {
        say(utterance, 0);
    }

    /**
     * Waits until an input is expected and then sends the given utterance to
     * JVoiceXML.
     * @param utterance the utterance to send
     * @param timeout max timeout to wait in msec, waits forever, if timeout is
     *                  zero
     */
    public void say(final String utterance, final long timeout) {
        Assert.assertNotNull("no active session", session);
        try {
            if (timeout == 0) {
                inputMonitor.waitUntilExpectingInput();
            } else {
                inputMonitor.waitUntilExpectingInput(timeout);
            }
            server.sendInput(utterance);
            for (CallListener listener : listeners) {
                listener.said(utterance);
            }
            LOGGER.info("say '" + utterance + "'");
        } catch (InterruptedException | IOException | TimeoutException
                | JVoiceXMLEvent e) {
            JVoiceXMLEvent lastError;
            try {
                lastError = session.getLastError();
            } catch (ErrorEvent ex) {
                final AssertionError error = new AssertionError(ex);
                notifyError(error);
                throw error;
            }
            if (lastError != null) {
                final AssertionError error = new AssertionError(lastError);
                notifyError(error);
                throw error;
            }
            final AssertionError error = new AssertionError(e);
            notifyError(error);
            throw error;
        }
    }

    /**
     * Sends the given utterance to JVoiceXML.
     * @param digits the digits to enter
     */
    public void enter(final String digits) {
        Assert.assertNotNull("no active session", session);
        CharacterInput input = null;
        try {
            inputMonitor.waitUntilExpectingInput();
            input = session.getCharacterInput();
        } catch (JVoiceXMLEvent | InterruptedException e) {
            throw new AssertionError(e);
        }
        for (int i = 0; i < digits.length(); i++) {
            final char ch = digits.charAt(i);
            input.addCharacter(ch);
        }
        for (CallListener listener : listeners) {
            listener.entered(digits);
        }
        LOGGER.info("entered '" + digits + "'");
    }

    /**
     * Sends the given utterance to JVoiceXML.
     * @param digits the digits to enter
     * @param timeout the timeout to wait at max in msec, waits forever, if
     *          timeout is zero
     */
    public void enter(final String digits, final long timeout) {
        Assert.assertNotNull("no active session", session);
        CharacterInput input = null;
        try {
            if (timeout == 0) {
                inputMonitor.waitUntilExpectingInput();
            } else {
                inputMonitor.waitUntilExpectingInput(timeout);
            }
            input = session.getCharacterInput();
        } catch (JVoiceXMLEvent | InterruptedException | TimeoutException e) {
            throw new AssertionError(e);
        }
        for (int i = 0; i < digits.length(); i++) {
            final char ch = digits.charAt(i);
            input.addCharacter(ch);
        }
        for (CallListener listener : listeners) {
            listener.entered(digits);
        }
        LOGGER.info("entered '" + digits + "'");
    }

    /**
     * Notifies all listeners about the given error.
     * @param error the caught error
     */
    private void notifyError(final AssertionError error) {
        for (CallListener listener : listeners) {
            listener.error(error);
        }
    }

    /**
     * Issues a hangup event.
     */
    public void hangup() {
        if (session != null) {
            session.hangup();
            for (CallListener listener : listeners) {
                listener.hungup();
            }
            LOGGER.info("hungup");
            session = null;
        }

        server.stopServer();
        LOGGER.info("server stopped");
    }
}
