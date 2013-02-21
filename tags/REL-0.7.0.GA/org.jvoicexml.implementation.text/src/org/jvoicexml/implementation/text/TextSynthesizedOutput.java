/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.text.TextRemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * Text based implementation for a {@link SynthesizedOutput}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class TextSynthesizedOutput
    implements SynthesizedOutput, ObservableSynthesizedOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TextSynthesizedOutput.class);

    /** Queued texts. */
    private final BlockingQueue<SpeakableText> texts;

    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> outputListener;

    /**
     * Constructs a new object.
     */
    public TextSynthesizedOutput() {
        texts = new java.util.concurrent.LinkedBlockingQueue<SpeakableText>();
        outputListener = new java.util.ArrayList<SynthesizedOutputListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return TextRemoteClient.TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        texts.clear();
        outputListener.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
    }

    /**
     * {@inheritDoc}
     */
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void queuePlaintext(final String text) throws NoresourceError,
            BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queuing plain text '" + text + "'...");
        }

        final SpeakablePlainText speakable = new SpeakablePlainText(text);
        texts.add(speakable);
    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakable,
            final boolean bargein, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        final Object o;
        if (speakable instanceof SpeakablePlainText) {
            SpeakablePlainText text = (SpeakablePlainText) speakable;
            o = text.getSpeakableText();
        } else {
            SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            o = ssml.getDocument();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queuing object " + o);
        }
        texts.add(speakable);
        fireOutputStarted(speakable);
    }


    /**
     * {@inheritDoc}
     */
    public boolean requiresAudioFileOutput() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("clearing all pending messages");
        }
        texts.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return !texts.isEmpty();
    }

    /**
     * Reads the next text to send to the client.
     * @return next text, <code>null</code> if there is no next output.
     */
    SpeakableText getNextText() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("retrieving next output...");
        }
        final SpeakableText speakable;
        try {
            speakable = texts.take();
        } catch (InterruptedException e) {
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("next output: " + speakable);
        }

        if (texts.isEmpty()) {
            fireQueueEmpty();

            // Notify the listeners that the list has changed.
            synchronized (texts) {
                texts.notifyAll();
            }
        }
        return speakable;
    }

    /**
     * {@inheritDoc}
     */
    public void waitQueueEmpty() {
        while (!texts.isEmpty()) {
            try {
                // Delay until the next text is removed.
                synchronized (texts) {
                    texts.wait();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SynthesizedOutputListener listener) {
        synchronized (outputListener) {
            outputListener.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(
        final SynthesizedOutputListener listener) {
        synchronized (outputListener) {
            outputListener.remove(listener);
        }
    }

    /**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.OUTPUT_STARTED, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.OUTPUT_STARTED, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event =
            new SynthesizedOutputEvent(this,
                    SynthesizedOutputEvent.QUEUE_EMPTY);
        fireOutputEvent(event);
    }

    /**
     * Notifies all registered listeners about the given event.
     * @param event the event.
     * @since 0.6
     */
    private void fireOutputEvent(final SynthesizedOutputEvent event) {
        synchronized (outputListener) {
            final Collection<SynthesizedOutputListener> copy =
                new java.util.ArrayList<SynthesizedOutputListener>();
            copy.addAll(outputListener);
            for (SynthesizedOutputListener current : copy) {
                current.outputStatusChanged(event);
            }
        }
    }
}
