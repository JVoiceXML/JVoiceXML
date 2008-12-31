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
public interface PromptQueueResource {

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
     * @param newValue
     * @param paramName
     */
    void changeParameter(Object newValue, String paramName);

    /**
     * Causes any queued prompts or fetch audio to be played
     */
    void play();

    /**
     * adds fetch audio to queue, removing any existing fetch audio from queue.
     * Does not cause it to be played.
     * 
     * @param prompt
     */
    void queueFetchAudio(Object prompt);

    /**
     * adds prompt to queue, but does not cause it to be played
     * 
     * @param prompt
     * @param properties
     */
    void queuePrompt(Object prompt, Map properties);

}