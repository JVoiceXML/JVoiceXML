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

package org.jvoicexml;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Facade for easy access to the system output.
 *
 * <p>
 * Objects that implement this interface support audio output using audio files
 * and text-to-speech (TTS). They are able to freely sequence TTS and audio
 * output.
 * </p>
 *
 * <p>
 * If an audio output resource is not available, an
 * <code>error.noresource</code> event is thrown. Audio files are referred to
 * by a URI. The language specifies a required set of audio file formats which
 * must be supported; additional audio file formats may also be supported.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
public interface SystemOutput {
    /**
     * The {@link SpeakableText} object is offered for addition to the speaking
     * queue.
     * 
     * <p>
     * Depending on the priority of the {@link SpeakableText} it will be
     * handled as follows:
     * <dl>
     * <dt>{@link org.jvoicexml.xml.vxml.PriorityType#APPEND}</dt>
     * <dd>append the {@link SpeakableText} to the end of the queue.</dd>
     * <dt>{@link org.jvoicexml.xml.vxml.PriorityType#PREPEND}</dt>
     * <dd>prepend the {@link SpeakableText} to the top of the queue.</dd>
     * <dt>{@link org.jvoicexml.xml.vxml.PriorityType#CLEAR}</dt>
     * <dd>clear the current queue and append the {@link SpeakableText} to the
     *          end of the queue.</dd>
     * </dl>
     * Not all priority types may be supported by the platform. In case the
     * behavior is not supported, the platform defaults to 
     * {@link org.jvoicexml.xml.vxml.PriorityType#APPEND}.
     * </p>
     * 
     *
     * @param speakable
     *        Text to be spoken.
     * @param sessionId
     *        the session Id
     * @param documentServer
     *        The document server to use.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            A URI within the speakable could not be obtained or a parsing
     *            error occurred.
     */
    void queueSpeakable(final SpeakableText speakable,
            final SessionIdentifier sessionId,
                final DocumentServer documentServer) throws NoresourceError,
                    BadFetchError;

    /**
     * Cancels the current output from the TTS engine and queued audio
     * for all entries in the queue that allow barge-in.
     *
     * <p>
     * The implementation has to maintain a list of cancelable outputs
     * depending on the <code>barge-in</code> flag.
     * </p>
     * @param type the bargein type to cancel
     * @exception NoresourceError
     *            The output resource is not available.
     *
     * @since 0.5
     */
    void cancelOutput(final BargeInType type) throws NoresourceError;
}
