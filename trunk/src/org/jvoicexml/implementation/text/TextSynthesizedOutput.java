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
import org.jvoicexml.implementation.ObservableSystemOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SystemOutputListener;

/**
 * Text based implementation for a {@link SynthesizedOuput}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class TextSynthesizedOutput
    implements SynthesizedOutput, ObservableSystemOutput {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TextSynthesizedOutput.class);

    /** Queued texts. */
    private final BlockingQueue<SpeakableText> texts;

    /** Registered output listener. */
    private final Collection<SystemOutputListener> outputListener;

    /**
     * Constructs a new object.
     */
    public TextSynthesizedOutput() {
        texts = new java.util.concurrent.LinkedBlockingQueue<SpeakableText>();
        outputListener = new java.util.ArrayList<SystemOutputListener>();
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
    public void setAudioFileOutput(final AudioFileOutput fileOutput) {
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return !texts.isEmpty();
    }

    /**
     * Reads the next text to send to the client.
     * @return next text.
     */
    SpeakableText getNextText() {
        SpeakableText speakable = null;
        try {
            speakable = texts.take();
        } catch (InterruptedException e) {
            return speakable;
        }
        return speakable;
    }

    void notifySpeakableSent(final SpeakableText speakable) {
        fireOutputEnded(speakable);
        if (texts.isEmpty()) {
            fireQueueEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void waitQueueEmpty() {
        while (!texts.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSystemOutputListener(final SystemOutputListener listener) {
        synchronized (outputListener) {
            outputListener.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeSystemOutputListener(
        final SystemOutputListener listener) {
        synchronized (outputListener) {
            outputListener.remove(listener);
        }
    }

    /**
     * Notifies all listeners that output has started.
     * @param speakable the current speakable.
     */
    private void fireOutputStarted(final SpeakableText speakable) {
        synchronized (outputListener) {
            final Collection<SystemOutputListener> copy =
                new java.util.ArrayList<SystemOutputListener>();
            copy.addAll(outputListener);
            for (SystemOutputListener current : copy) {
                current.outputStarted(speakable);
            }
        }
    }

    /**
     * Notifies all listeners that output has ended.
     * @param speakable the current speakable.
     */
    private void fireOutputEnded(final SpeakableText speakable) {
        synchronized (outputListener) {
            final Collection<SystemOutputListener> copy =
                new java.util.ArrayList<SystemOutputListener>();
            copy.addAll(outputListener);
            for (SystemOutputListener current : copy) {
                current.outputEnded(speakable);
            }
        }
    }

    /**
     * Notifies all listeners that output queue is empty.
     */
    private void fireQueueEmpty() {
        synchronized (outputListener) {
            final Collection<SystemOutputListener> copy =
                new java.util.ArrayList<SystemOutputListener>();
            copy.addAll(outputListener);
            for (SystemOutputListener current : copy) {
                current.outputQueueEmpty();
            }
        }
    }
}
