/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mrcpv2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.mrcpv2.Mrcpv2ConnectionInformation;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.implementation.MarkerReachedEvent;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SessionManager;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Audio output that uses the MRCPv2 to address the TTS engine.
 * 
 * <p>
 * Handle all MRCPv2 calls to the TTS engine.
 * </p>
 * 
 * @author Spencer Lord
 * @author Dirk Schnelle-Walka
 * @author Patrick L. Lange
 * @since 0.7
 */
public final class Mrcpv2SynthesizedOutput
        implements SynthesizedOutput, SpeechEventListener {
    // SpeakableListener, SynthesizerListener {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(Mrcpv2SynthesizedOutput.class);

    /** The system output listener. */
    private final Collection<SynthesizedOutputListener> listeners;

    /** Type of this resources. */
    private String type;

    /**
     * Flag to indicate that TTS output and audio can be canceled.
     * 
     * @todo Replace this by a solution that does not cancel output without
     *       bargein, if there is mixed output.
     */
    private boolean enableBargeIn;

    /** The session manager. */
    private SessionManager sessionManager;

    /** The speech client. */
    private SpeechClient speechClient;

    /** Number of queued prompts. */
    private int queueCount;

    /** Synchronisation of speech events from the MRCPv2 server. */
    private final Object lock;

    /**
     * Constructs a object.
     */
    public Mrcpv2SynthesizedOutput() {
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        lock = new Object();
        // TODO Should there be a queue here on the client side too? There is
        // one on the server.
        // queuedSpeakables = new java.util.ArrayList<SpeakableText>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.add(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SynthesizedOutputListener outputListener) {
        synchronized (listeners) {
            listeners.remove(outputListener);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * Checks the type of the given speakable and forwards it either as for SSML
     * output or for plain text output.
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final String sessionId, final DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
        String speakText = null;
        boolean urlPrompt = false;
        queueCount++;
        LOGGER.info("Queue count incremented,, now " + queueCount);
        try {
            // TODO Pass on the entire SSML doc (and remove the code that
            // extracts the text)
            // The following code extract the text from the SSML since
            // the mrcp server (cairo) does not support SSML yet
            // (really the tts engine needs to support it i.e freetts)
            if (speakable instanceof SpeakableSsmlText) {
                InputStream is = null;
                String temp = speakable.getSpeakableText();
                byte[] b = temp.getBytes();
                is = new ByteArrayInputStream(b);
                InputSource src = new InputSource(is);
                SsmlDocument ssml = new SsmlDocument(src);
                speakText = ssml.getSpeak().getTextContent();

                LOGGER.info("Text content is " + speakText);

                // TODO Implement a better way of detecting and extracting
                // audio URLs
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder
                        .parse(new InputSource(new StringReader(temp)));
                NodeList list = document.getElementsByTagName("audio");
                if (list != null && list.getLength() > 0) {
                    Element audioTag = (Element) list.item(0);
                    String url = audioTag.getAttribute("src");
                    try {
                        new URI(url);
                        speakText = url;
                        urlPrompt = true;
                    } catch (URISyntaxException e) {
                        LOGGER.error("'src' attribute is not a valid URI");
                    }
                }
            }

            if (urlPrompt) {
                LOGGER.info(String.format("Using URL: %s", speakText));

                // HALEF Event logging
                final String hevent = String.format(
                        "INSERT INTO haleflogs"
                                + " (databasedate, machineIP, machinedate, class, level,"
                                + " message) VALUES(%s, \"%s\", %s,"
                                + " \"%s\", \"%s\", \"%s\")",
                        "now()", System.getenv("IP"), "now()",
                        "implementation.mrcpv2.Mrcpv2SynthesizedOutput", "INFO",
                        "Using URL!: " + speakText);
                //HalefDbWriter.execute(hevent);
            } else {
                LOGGER.info(String.format("Using TTS!: %s", speakText));

                // HALEF Event logging
                final String hevent = String.format(
                        "INSERT INTO haleflogs"
                                + " (databasedate, machineIP, machinedate, class, level,"
                                + " message) VALUES(%s, \"%s\", %s,"
                                + " \"%s\", \"%s\", \"%s\")",
                        "now()", System.getenv("IP"), "now()",
                        "implementation.mrcpv2.Mrcpv2SynthesizedOutput", "INFO",
                        "Using TTS!: " + speakText);
                //HalefDbWriter.execute(hevent);
            }

            speechClient.queuePrompt(urlPrompt, speakText);

        } catch (ParserConfigurationException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (MrcpInvocationException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IOException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (NoMediaControlChannelException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *            the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputStartedEvent(this, null,
                speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that the given marker has been reached.
     * 
     * @param mark
     *            the reached marker.
     */
    private void fireMarkerReached(final String mark) {
        final SynthesizedOutputEvent event = new MarkerReachedEvent(this, null,
                mark);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output has started.
     * 
     * @param speakable
     *            the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event = new OutputEndedEvent(this, null,
                speakable);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * Notifies all listeners that output queue us empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this, null);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy = new java.util.ArrayList<SynthesizedOutputListener>(
                    listeners);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput(final BargeInType bargeInType)
            throws NoresourceError {
        LOGGER.warn("cancelOutput not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        synchronized (lock) {
            while (queueCount > 0) {
                try {
                    checkInterrupted();
                    lock.wait();
                } catch (InterruptedException e) {
                    LOGGER.warn("q count " + queueCount);
                }
            }
        }
    }

    /**
     * Convenient method to wait until all output is being played.
     */
    @Override
    public void waitQueueEmpty() {
        synchronized (lock) {
            while (queueCount > 0) {
                try {
                    checkInterrupted();
                    lock.wait();
                } catch (InterruptedException e) {
                    LOGGER.warn("q count " + queueCount);
                }
            }
        }
    }

    private void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        listeners.clear();
        queueCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation client) throws IOException {
        // If the connection is already established, use this connection.

        final Mrcpv2ConnectionInformation mrcpv2Client =
                (Mrcpv2ConnectionInformation) client;
        LOGGER.info("connecting to '" + mrcpv2Client + "'");

        speechClient = mrcpv2Client.getTtsClient();
        if (speechClient == null) {
            throw new IOException("No TTS client");
        }
        speechClient.addListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation client) {
        // If the connection is already established, do not touch this
        // connection.
        if (client instanceof Mrcpv2ConnectionInformation) {
            speechClient = null;
            return;
        }
        // disconnect the mrcp channel
        try {
            speechClient.shutdown();
        } catch (MrcpInvocationException e) {
            LOGGER.warn(e, e);
        } catch (IOException e) {
            LOGGER.warn(e, e);
        } catch (InterruptedException e) {
            LOGGER.warn(e, e);
        } finally {
            speechClient = null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "Disconnected the synthesizedoutput mrcpv2 client form the server");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this resource.
     * 
     * @param resourceType
     *            type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        // TODO query server to determine if queue is non-empty
        LOGGER.info("Is busy : " + queueCount);
        return queueCount > 0;
    }

    // Cairo Client Speech event methods (from SpeechEventListener i/f)

    /**
     * {@inheritDoc}
     */
    @Override
    public void speechSynthEventReceived(final SpeechEventType event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Speech synth event " + event);
        }
        if (event == SpeechEventType.SPEAK_COMPLETE) {
            // TODO get the speakable object from the event?
            // fireOutputStarted(new SpeakablePlainText());
            // TODO Should there be a queue here in the client or over on the
            // server or both?
            queueCount--;
            LOGGER.info("Queue count decremented, now " + queueCount);
            synchronized (lock) {
                lock.notifyAll();
            }
            if (queueCount == 0) {
                fireQueueEmpty();
            }
            // TODO Handle speech markers
            // } else if
            // (MrcpEventName.SPEECH_MARKER.equals(event.getEventName())) {
            // fireMarkerReached(mark);
        } else {
            LOGGER.warn("Unhandled mrcp speech synth event " + event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recognitionEventReceived(final SpeechEventType event,
            final RecognitionResult result) {
        LOGGER.warn("mrcpv2synthesized output received a recog event."
                + "Discarding it.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characterEventReceived(final String c,
            final DtmfEventType status) {
        LOGGER.warn("characterEventReceived not implemented");
    }

    /**
     * @return the sessionManager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * @param manager
     *            the sessionManager to set
     */
    public void setSessionManager(final SessionManager manager) {
        sessionManager = manager;
    }
}
