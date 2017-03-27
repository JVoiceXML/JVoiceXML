/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Facade for easy access to the system output as an external resource.
 *
 * <p>
 * Objects that implement this interface support audio output text-to-speech
 * (TTS).
 * </p>
 * <p>
 * It is guaranteed that the session remains the same between the calls to
 * {@link org.jvoicexml.RemoteConnectable#connect(org.jvoicexml.ConnectionInformation)}
 * and
 * {@link org.jvoicexml.RemoteConnectable#disconnect(org.jvoicexml.ConnectionInformation)}.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public interface SynthesizedOutput extends ExternalResource, OutputDevice {
    /**
     * The Speakable object is added to the end of the speaking queue and will
     * be spoken once it reaches the top of the queue.
     *
     * @param speakable
     *            Text to be spoken.
     * @param sessionId
     *            the current session id
     * @param documentServer
     *            The document server to use.
     * @exception NoresourceError
     *                The output resource is not available.
     * @exception BadFetchError
     *                A URI within the speakable could not be obtained or a
     *                parsing error occurred.
     */
    void queueSpeakable(SpeakableText speakable, String sessionId,
            DocumentServer documentServer)
            throws NoresourceError, BadFetchError;

    /**
     * Delays until all prompts are played that do not allow for barge-in.
     * 
     * @since 0.7.1
     */
    void waitNonBargeInPlayed();

    /**
     * Convenient method to wait until all output is being played.
     */
    void waitQueueEmpty();

    /**
     * Adds the listener for system output events.
     *
     * <p>
     * The implementation of this interface must notify the listener about all
     * events.
     * </p>
     *
     * @param listener
     *            the listener to add.
     */
    void addListener(SynthesizedOutputListener listener);

    /**
     * Removes the listener for system output events.
     * 
     * @param listener
     *            the listener to remove.
     *
     * @since 0.6
     */
    void removeListener(SynthesizedOutputListener listener);
}
