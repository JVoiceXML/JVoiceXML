/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.CallControl;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.PromptAccumulator;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * An implementation of a {@link PromptAccumulator}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
class JVoiceXmlPromptAccumulator implements PromptAccumulator {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlPromptAccumulator.class);

    /** The implementation platform to use. */
    private final ImplementationPlatform platform;

    /** The prompt timeout. */
    private long timeout;

    /** The accumulated prompts. */
    private final List<SpeakableText> prompts;

    /**
     * Constructs a new object.
     * @param implementationPlatform the implementation platform to use
     */
    public JVoiceXmlPromptAccumulator(
            final ImplementationPlatform implementationPlatform) {
        platform = implementationPlatform;
        prompts = new java.util.ArrayList<SpeakableText>();
        timeout = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPromptTimeout(final long promptTimeout) {
        timeout = promptTimeout;
        prompts.clear();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initial timeout after prompt queuing is " + timeout);
        }
    }

    /**
     * Retrieves the prompt timeout.
     * @return the prompt timeout
     */
    public long getPromptTimeout() {
        return timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queuePrompt(final SpeakableText speakable) {
        prompts.add(speakable);
    }

    /**
     * Retrieves the last queued prompt.
     * @return the last prompt of the queue
     */
    public SpeakableText getLastSpeakableText() {
        final int size = prompts.size();
        if (size == 0) {
            return null;
        }
        return prompts.get(size - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderPrompts(final String sessionId,
            final DocumentServer server)
            throws BadFetchError, NoresourceError,
                ConnectionDisconnectHangupEvent {
        final SystemOutput output = platform.getSystemOutput();
        final CallControl call = platform.getCallControl();
        for (SpeakableText speakable : prompts) {
            if (speakable instanceof SpeakableSsmlText) {
                final SpeakableSsmlText ssmlSpeakable =
                        (SpeakableSsmlText) speakable;
                final long currentTimeout = ssmlSpeakable.getTimeout();
                if (currentTimeout >= 0) {
                    timeout = currentTimeout;
                }
            }
            try {
                call.play(output, null);
            } catch (IOException e) {
                throw new BadFetchError("error playing to calling device", e);
            }
            output.queueSpeakable(speakable, sessionId, server);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("timeout after prompt queuing: " + timeout);
        }
    }
}
