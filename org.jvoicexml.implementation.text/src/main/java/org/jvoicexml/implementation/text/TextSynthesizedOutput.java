/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.client.text.TextConnectionInformation;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Text based implementation for a {@link SynthesizedOutput}.
 *
 * <p>
 * This implementation simply receives the {@link SpeakableText} objects from
 * the implementation platform and queues them. The queued elements will be
 * retrieved from the {@link TextTelephony} and sent to the client.
 * </p> 
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class TextSynthesizedOutput
    implements SynthesizedOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TextSynthesizedOutput.class);

    /** Queued texts. */
    private final BlockingQueue<SpeakableText> texts;

    /** <code>true</code> if the topmost speakable is currently processed. */
    private boolean processingSpeakable;

    /** Registered output listener. */
    private final Collection<SynthesizedOutputListener> outputListener;

    /**
     * Constructs a new object.
     */
    TextSynthesizedOutput() {
        texts = new java.util.concurrent.LinkedBlockingQueue<SpeakableText>();
        outputListener = new java.util.ArrayList<SynthesizedOutputListener>();
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
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TextConnectionInformation.TYPE;
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
    public void passivate() {
        texts.clear();
        outputListener.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
        texts.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final String sessionId, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        final Object o;
        if (speakable instanceof SpeakableSsmlText) {
            SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            o = ssml.getDocument();
        } else {
            o = speakable.getSpeakableText();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queuing object " + o);
        }
        texts.add(speakable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput(final BargeInType bargeInType)
            throws NoresourceError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("clearing all pending messages");
        }
        final Collection<SpeakableText> skipped =
            new java.util.ArrayList<SpeakableText>();
        for (SpeakableText speakable : texts) {
            if (speakable.isBargeInEnabled(bargeInType)) {
                skipped.add(speakable);
            } else {
                break;
            }
        }
        texts.removeAll(skipped);
        if (texts.isEmpty()) {
            fireQueueEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        return !texts.isEmpty() || processingSpeakable;
    }

    /**
     * Reads the next text to send to the client.
     * @return next text, <code>null</code> if there is no next output.
     */
    SpeakableText getNextText() {
        final SpeakableText speakable;
        try {
            speakable = texts.take();
            processingSpeakable = true;
            fireOutputStarted(speakable);
        } catch (InterruptedException e) {
            return null;
        }
        return speakable;
    }

    /**
     * Checks if the queue is empty after the retrieval of the given
     * speakable.
     * <p>
     * This method is a callback after the {@link TextTelephony} safely
     * obtained the speakable.
     * </p>
     * @param speakable the last retrieved speakable
     * @since 0.7.1
     */
    void checkEmptyQueue(final SpeakableText speakable) {
        if (speakable != null) {
            fireOutputEnded(speakable);
        }
        processingSpeakable = false;
        if (texts.isEmpty()) {
            fireQueueEmpty();

            // Notify the listeners that the list has changed.
            synchronized (texts) {
                texts.notifyAll();
            }
        }
    }

    /**
     * The client disconnected from JVoiceXML.
     * 
     * @since 0.7.3
     */
    void disconnected() {
        if (!isBusy()) {
            return;
        }
        LOGGER.info("client disconnected. Aborting pending requests");
        texts.clear();
        processingSpeakable = false;
        // Notify the listeners that the list has changed.
        synchronized (texts) {
            texts.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
        if (texts.isEmpty()) {
            return;
        }
        do {
            final SpeakableText speakable = texts.peek();
            if (speakable.isBargeInEnabled(BargeInType.SPEECH)
                    || speakable.isBargeInEnabled(BargeInType.HOTWORD)) {
                return;
            }
            synchronized (texts) {
                try {
                    texts.wait();
                } catch (InterruptedException e) {
                    return;
                }
            }
        } while (!texts.isEmpty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitQueueEmpty() {
        while (isBusy()) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("waiting for empty output queue...");
                }
                // Delay until the next text is removed or processing ended.
                synchronized (texts) {
                    if (isBusy()) {
                        texts.wait();
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("output queue is empty");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SynthesizedOutputListener listener) {
        synchronized (outputListener) {
            outputListener.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            new OutputStartedEvent(this, null, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        final SynthesizedOutputEvent event =
            new OutputEndedEvent(this, null, speakable);
        fireOutputEvent(event);
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        final SynthesizedOutputEvent event = new QueueEmptyEvent(this, null);
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
