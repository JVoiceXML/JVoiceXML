/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.resources;

import java.util.Map;

/**
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @created 31-Dez-2008 17:00:28
 */
public interface PromptQueueResource extends Resource {

    /**
     * Immediately cancels any prompt or fetch audio that is playing and clears
     * the queue.
     */
    void cancel();

    /**
     * Deletes any queued fetch audio. Also cancels any fetch audio that is
     * already playing, unless fetchAudioMin has been specified and not yet
     * reached.
     */
    void cancelFetchAudio();

    /**
     * Sets the value of paramName to newValue, which may be either an absolute
     * or relative value. The new setting takes effect immediately, even if
     * there is already a prompt playing.
     *
     * @param newValue new value for the parameter
     * @param paramName name of the parameter
     */
    void changeParameter(final String paramName, final Object newValue);

    /**
     * Causes any queued prompts or fetch audio to be played.
     */
    void play();

    /**
     * Adds fetch audio to queue, removing any existing fetch audio from queue.
     * Does not cause it to be played.
     *
     * @param prompt prompt to be played
     */
    void queueFetchAudio(final Object prompt);

    /**
     * Adds prompt to queue, but does not cause it to be played.
     *
     * @param prompt prompt to be queued
     * @param properties properties
     */
    void queuePrompt(final Object prompt, final Map<?, ?> properties);
}
