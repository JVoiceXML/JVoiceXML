/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * A prompt accumulator collects all the prompts that are to be rendered in the
 * {@link org.jvoicexml.SystemOutput}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
public interface PromptAccumulator {
    /**
     * Sets the default timeout to use.
     * <p>
     * This method is intended to be called prior to queuing prompts
     * via {@link #queuePrompt(SpeakableText)}, so it also clears the list of
     * queued prompts.
     * </p>
     * @param timeout default timeout.
     */
    void setPromptTimeout(final long timeout);

    /**
     * Queues the given prompt without rendereing it.
     * <p>
     * After all prompts have been queued, the end of prompt queing must be
     * indicated by {@link #renderPrompts()}.
     * </p>
     * @param speakable the prompt to queue.
     */
    void queuePrompt(final SpeakableText speakable);

    /**
     * Notifies the implementation platform about the end of the prompt
     * queing that has been started by {@link #setPromptTimeout(long)}.
     * <p>
     * It is assumed that the {@link PromptAccumulator} has knowledge about
     * the {@link ImplementationPlatform} to render the output.
     * </p>
     * @param server the document server to use.
     * @exception BadFetchError
     *            error queuing the prompt
     * @exception NoresourceError
     *            Output device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     */
    void renderPrompts(final DocumentServer server)
            throws BadFetchError, NoresourceError,
                ConnectionDisconnectHangupEvent;
}
