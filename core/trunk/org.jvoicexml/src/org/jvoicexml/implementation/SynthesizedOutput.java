/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Facade for easy access to the system output.
 *
 * <p>
 * Objects that implement this interface support audio output text-to-speech
 * (TTS).
 * </p>
 *
 * <p>
 * Objects implementing this interface are requested to
 * use {@link org.jvoicexml.implementation.AudioFileOutput} instances to
 * sequence audio files and synthesized speech from this object in SSML
 * outputs. From the JVoiceXml perspective, the {@link SynthesizedOutput} is
 * considered to be the master and the {@link AudioFileOutput} to be a
 * dependent resource.
 * </p>
*
 * <p>
 * If an audio output resource is not available, an
 * <code>error.noresource</code> event is thrown.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public interface SynthesizedOutput
    extends ExternalResource, OutputDevice, ObservableSynthesizedOutput {
    /**
     * Obtains an URI that can be used as an input source for a
     * {@link org.jvoicexml.CallControl} object. This method is called each
     * time, before an output is requested from this object.
     * @return URI of the input source, maybe <code>null</code> if the
     * streaming uses other means of audio output.
     * @throws NoresourceError
     *         Error accessing the device.
     * @throws URISyntaxException
     *         error creating the URI
     */
    URI getUriForNextSynthesisizedOutput()
        throws NoresourceError, URISyntaxException;

    /**
     * The Speakable object is added to the end of the speaking queue and will
     * be spoken once it reaches the top of the queue.
     *
     * <p>
     * Objects implementing {@link SynthesizedOutput} are requested to use
     * {@link AudioFileOutput} instances to sequence audio files and synthesized
     * speech in SSML outputs.
     * </p>
     *
     * @param speakable
     *        Text to be spoken.
     * @param documentServer
     *        The document server to use.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            A URI within the speakable could not be obtained or a parsing
     *            error occurred.
     */
    void queueSpeakable(final SpeakableText speakable,
            final DocumentServer documentServer) throws NoresourceError,
            BadFetchError;

    /**
     * Delays until all prompts are played that do not allow for barge-in.
     * @since 0.7.1
     */
    void waitNonBargeInPlayed();

    /**
     * Convenient method to wait until all output is being played.
     */
    void waitQueueEmpty();
}
