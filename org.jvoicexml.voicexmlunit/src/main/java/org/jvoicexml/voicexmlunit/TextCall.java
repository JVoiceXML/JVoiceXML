/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Call simulates a real telephone call. This is done with creation of a new
 * JVoiceXML session and a TextServer that can be used to notice all events.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public final class TextCall implements Call {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(TextCall.class);
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
    /** The last captured output. */
    private SsmlDocument lastOutput;
    /** The last observed error. */
    private JVoiceXMLEvent lastError;

    /**
     * Server port number to use. Port number must be greater than 1024.
     */
    public static final int DEFAULT_SERVER_PORT = 6000;
    /** The active session. */
    private Session session;

    /**
     * Constructs a new object with the default server port.
     * 
     * @throws InterruptedException
     *             error initializing the output buffer
     */
    public TextCall() throws InterruptedException {
        this(DEFAULT_SERVER_PORT);
    }

    /**
     * Constructs a new call.
     * 
     * @param hostname
     *            the host name to use for the {@link TextServer}
     * @param port
     *            number to use for the {@link TextServer}.
     * @throws InterruptedException
     *             error initializing the output buffer
     */
    public TextCall(final String hostname, final int port)
            throws InterruptedException {
        portNumber = port;
        server = new TextServer(hostname, portNumber);
        server.setAutoAcknowledge(false);
        outputBuffer = new OutputMessageBuffer(server);
        server.addTextListener(outputBuffer);
        inputMonitor = new InputMonitor();
        server.addTextListener(inputMonitor);
        listeners = new java.util.ArrayList<CallListener>();
    }

    /**
     * Constructs a new call.
     * 
     * @param port
     *            number to use for the {@link TextServer}.
     * @throws InterruptedException
     *             error initializing the output buffer
     */
    public TextCall(final int port) throws InterruptedException {
        this(null, port);
    }

    /**
     * Adds the given listener of messages received from the JVoiceXML. This
     * allows for further investigation of the behavior.
     * 
     * @param listener
     *            the listener to add
     */
    public void addTextListener(final TextListener listener) {
        server.addTextListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(final File file) {
        final URI uri = file.toURI();
        call(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(final URI uri) {
        LOGGER.info("calling '" + uri + "'");
        try {
            lastError = null;
            final Context context = new InitialContext();
            final JVoiceXml jvxml = (JVoiceXml) context.lookup("JVoiceXml");

            // Start the text server
            server.start();
            server.waitStarted();

            // run the dialog
            final ConnectionInformation info = server
                    .getConnectionInformation();
            final SessionIdentifier id = new UuidSessionIdentifier();
            session = jvxml.createSession(info, id);
            session.call(uri);
            for (CallListener listener : listeners) {
                listener.called(uri);
            }
        } catch (Exception | ErrorEvent e) {
            final AssertionError error = new AssertionError(e.getMessage(), e);
            notifyError(error);
            throw error;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlDocument getNextOutput() {
        Assert.assertNotNull("no active session", session);
        try {
            lastOutput = outputBuffer.nextMessage();
            for (CallListener listener : listeners) {
                listener.heard(lastOutput);
            }
            LOGGER.info("heard '" + lastOutput + "'");
            return lastOutput;
        } catch (InterruptedException | JVoiceXMLEvent | IOException e) {
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
     * {@inheritDoc}
     */
    @Override
    public SsmlDocument getNextOutput(final long timeout) {
        Assert.assertNotNull("no active session", session);
        try {
            lastOutput = outputBuffer.nextMessage(timeout);
            Assert.assertNotNull("Received SSML must not be null", lastOutput);
            for (CallListener listener : listeners) {
                listener.heard(lastOutput);
            }
            LOGGER.info("heard '" + lastOutput + "'");
            return lastOutput;
        } catch (InterruptedException | TimeoutException | JVoiceXMLEvent
                | IOException e) {
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
     * {@inheritDoc}
     */
    @Override
    public SsmlDocument getLastOutput() {
        return lastOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hears(final String utterance) {
        Assert.assertNotNull("no active session", session);
        final SsmlDocument document = getNextOutput();
        Assert.assertNotNull("Received SSML must not be null", document);
        final Speak speak = document.getSpeak();
        final String output = speak.getTextContent();
        Assert.assertEquals(utterance, output);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hears(final String utterance, final long timeout) {
        Assert.assertNotNull("no active session", session);
        final SsmlDocument document = getNextOutput(timeout);
        Assert.assertNotNull("Received SSML must not be null", document);
        final Speak speak = document.getSpeak();
        final String output = speak.getTextContent();
        Assert.assertEquals(utterance, output);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hearsAudio(final URI uri) {
        Assert.assertNotNull("no active session", session);
        final SsmlDocument document = getNextOutput();
        Assert.assertNotNull("Received SSML must not be null", document);
        assertAudioSource(document, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hearsAudio(final URI uri, final long timeout) {
        Assert.assertNotNull("no active session", session);
        final SsmlDocument document = getNextOutput(timeout);
        Assert.assertNotNull("Received SSML must not be null", document);
        assertAudioSource(document, uri);
    }

    /**
     * Checks if the given URI is in the source attribute of the received audio.
     * 
     * @param document
     *            the document to check
     * @param uri
     *            the expected audio source URI
     */
    private void assertAudioSource(final SsmlDocument document, final URI uri) {
        final VoiceXmlUnitNamespaceContext context =
                new VoiceXmlUnitNamespaceContext();
        context.addPrefix("ssml", Speak.DEFAULT_XMLNS);
        try {
            XPathAssert.assertEquals(context, document,
                    "/ssml:speak/ssml:audio/@src", uri.toString());
        } catch (XPathExpressionException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void say(final String utterance) {
        say(utterance, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void enter(final String digits) {
        Assert.assertNotNull("no active session", session);
        DtmfInput input = null;
        try {
            inputMonitor.waitUntilExpectingInput();
            input = session.getDtmfInput();
        } catch (JVoiceXMLEvent | InterruptedException | TimeoutException e) {
            throw new AssertionError(e);
        }
        for (int i = 0; i < digits.length(); i++) {
            final char ch = digits.charAt(i);
            input.addDtmf(ch);
        }
        for (CallListener listener : listeners) {
            listener.entered(digits);
        }
        LOGGER.info("entered '" + digits + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(final String digits, final long timeout) {
        Assert.assertNotNull("no active session", session);
        DtmfInput input = null;
        try {
            if (timeout == 0) {
                inputMonitor.waitUntilExpectingInput();
            } else {
                inputMonitor.waitUntilExpectingInput(timeout);
            }
            input = session.getDtmfInput();
        } catch (JVoiceXMLEvent | InterruptedException | TimeoutException e) {
            throw new AssertionError(e);
        }
        for (int i = 0; i < digits.length(); i++) {
            final char ch = digits.charAt(i);
            input.addDtmf(ch);
        }
        for (CallListener listener : listeners) {
            listener.entered(digits);
        }
        LOGGER.info("entered '" + digits + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitUnitExpectingInput() {
        Assert.assertNotNull("no active session", session);
        try {
            inputMonitor.waitUntilExpectingInput();
        } catch (InterruptedException | TimeoutException | JVoiceXMLEvent e) {
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
     * {@inheritDoc}
     */
    @Override
    public void waitUnitExpectingInput(final long timeout) {
        Assert.assertNotNull("no active session", session);
        try {
            if (timeout == 0) {
                inputMonitor.waitUntilExpectingInput();
            } else {
                inputMonitor.waitUntilExpectingInput(timeout);
            }
        } catch (InterruptedException | TimeoutException | JVoiceXMLEvent e) {
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
     * Notifies all listeners about the given error.
     * 
     * @param error
     *            the caught error
     */
    private void notifyError(final AssertionError error) {
        for (CallListener listener : listeners) {
            listener.error(error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hangup() {
        if (outputBuffer.hasReceivedDisconnect()) {
            try {
                outputBuffer.acknowledgeBye();
            } catch (InterruptedException | IOException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("error acknowledging a BYE", e);
                }
            }
        }
        if (session != null) {
            session.hangup();
            for (CallListener listener : listeners) {
                listener.hungup();
            }
            LOGGER.info("remote hungup");
            session = null;
        }

        server.stopServer();
        LOGGER.info("server stopped");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JVoiceXMLEvent getLastError() {
        if (session != null) {
            try {
                return session.getLastError();
            } catch (ErrorEvent e) {
                return lastError;
            }
        }
        return lastError;
    }
}
