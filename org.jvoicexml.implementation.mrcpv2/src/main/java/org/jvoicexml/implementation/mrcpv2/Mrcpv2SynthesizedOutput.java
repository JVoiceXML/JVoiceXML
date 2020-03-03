/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.util.Collection;

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
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SessionManager;
import org.speechforge.cairo.client.SpeechClient;
import org.speechforge.cairo.client.SpeechEventListener;
import org.speechforge.cairo.client.recog.RecognitionResult;

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

    /** The session manager. */
    private SessionManager sessionManager;

    /** The speech client. */
    private SpeechClient speechClient;

    /** Number of queued prompts. */
    private int queueCount;

    /** Synchronization of speech events from the MRCPv2 server. */
    private final Object lock;

    /**
     * Constructs a object.
     */
    public Mrcpv2SynthesizedOutput() {
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        lock = new Object();
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
        try {
            if (speakable instanceof SpeakableSsmlText) {
                final SpeakableSsmlText text = (SpeakableSsmlText) speakable;
                queuePrompts(text);
            }
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
     * Queues the given speakable to the audio stream.
     * @param text the text to be queued
     * @throws MrcpInvocationException
     *          error invoking the MRCP
     * @throws IOException
     *          error opening a file
     * @throws InterruptedException
     *          execution was interrupted
     * @throws NoMediaControlChannelException
     *          no media accessible
     * @since 0.7.9
     */
    private void queuePrompts(final SpeakableSsmlText speakable) 
            throws MrcpInvocationException, IOException, InterruptedException,
                NoMediaControlChannelException {
        // TODO Pass on the entire SSML doc (and remove the code that
        // extracts the text)
        // The following code extract the text from the SSML since
        // the mrcp server (cairo) does not support SSML yet
        // (really the tts engine needs to support it i.e freetts)
        final SsmlDocument ssml = speakable.getDocument();
        final Speak speak = ssml.getSpeak();
        final Collection<XmlNode> children = speak.getChildren();
        for (XmlNode node : children) {
            if (node instanceof Text) {
                final Text text = (Text) node;
                queuePrompt(text);
            } else if (node instanceof Audio) {
                final Audio audio = (Audio) node;
                queuePrompt(audio);
            }
        }
    }
    
    /**
     * Queues the given text prompt to the audio stream.
     * @param text the text to be queued
     * @throws MrcpInvocationException
     *          error invoking the MRCP
     * @throws IOException
     *          error opening a file
     * @throws InterruptedException
     *          execution was interrupted
     * @throws NoMediaControlChannelException
     *          no media accessible
     * @since 0.7.9
     */
    private void queuePrompt(final Text text)
            throws MrcpInvocationException, IOException, InterruptedException,
                NoMediaControlChannelException {
        final String value = text.getNodeValue();
        final String prompt = value.trim();
        if (prompt.isEmpty()) {
            return;
        }
        LOGGER.info("queueing URL '" + prompt + "'");
        speechClient.queuePrompt(false, prompt);
        synchronized (lock) {
            queueCount++;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("queue size " + queueCount);
            }
        }
    }

    /**
     * Queues the given text prompt to the audio stream.
     * @param text the text to be queued
     * @throws MrcpInvocationException
     *          error invoking the MRCP
     * @throws IOException
     *          error opening a file
     * @throws InterruptedException
     *          execution was interrupted
     * @throws NoMediaControlChannelException
     *          no media accessible
     * @since 0.7.9
     */
    private void queuePrompt(final Audio audio)
            throws MrcpInvocationException, IOException, InterruptedException,
                NoMediaControlChannelException {
        final String src = audio.getSrc();
        LOGGER.info("queueing URL '" + src + "'");
        speechClient.queuePrompt(true, src);
        synchronized (lock) {
            queueCount++;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("queue size " + queueCount);
            }
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
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this, null);

        synchronized (listeners) {
            final Collection<SynthesizedOutputListener> copy =
                    new java.util.ArrayList<SynthesizedOutputListener>(
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
        waitQueueEmpty();
    }

    /**
     * Convenient method to wait until all output is being played.
     */
    @Override
    public void waitQueueEmpty() {
        LOGGER.info("waiting for empty queue...");
        synchronized (lock) {
            while (queueCount > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    LOGGER.warn("waiting interrupted", e);
                    return;
                }
            }
        }
        LOGGER.info("...queue empty");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() {
        listeners.clear();
        synchronized (lock) {
            queueCount = 0;
            lock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        queueCount = 0;
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
        synchronized (lock) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("is busy : " + queueCount);
            }
            return queueCount > 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void speechSynthEventReceived(final SpeechEventType event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Speech synth event " + event);
        }
        if (event == SpeechEventType.SPEAK_COMPLETE) {
            processSpeakComplete(event);
        } else {
            LOGGER.warn("Unhandled mrcp speech synth event " + event);
        }
        // TODO Handle speech markers
        // } else if
        // (MrcpEventName.SPEECH_MARKER.equals(event.getEventName())) {
        // fireMarkerReached(mark);
    }

    /**
     * A {@code SpeechEventType.SPEAK_COMPLETE} event has been received.
     * @param event the received event.
     * @since 0.7.9
     */
    private void processSpeakComplete(final SpeechEventType event) {
        // TODO get the speakable object from the event?
        // fireOutputStarted(new SpeakablePlainText());
        // TODO Should there be a queue here in the client or over on the
        // server or both?
        LOGGER.info("speakable completed");
        synchronized (lock) {
            queueCount--;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("queue size " + queueCount);
            }
            
            if (queueCount == 0) {
                fireQueueEmpty();
            }
            lock.notifyAll();
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
