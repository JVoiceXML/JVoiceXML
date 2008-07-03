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

package org.jvoicexml.implementation;

import java.net.URI;

import org.jvoicexml.DocumentServer;
import org.jvoicexml.Session;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Facade for easy access to the system output.
 *
 * <p>
 * Objects that implement this interface support audio output using audio files.
 * It is up to the {@link SynthesizedOutput} if this resource is needed. It is
 * acquired, if {@link SynthesizedOutput#requiresAudioFileOutput()} returns
 * <code>true</code>.
 * </p>
 *
 * <p>
 * If an audio output resource is not available, an
 * <code>error.noresource</code> event is thrown. Audio files are referred to
 * by a URI. The language specifies a required set of audio file formats which
 * must be supported; additional audio file formats may also be supported.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public interface AudioFileOutput
    extends ExternalResource, OutputDevice {
    /**
     * Obtains an URI that can be used as an input source for a
     * {@link org.jvoicexml.CallControl} object. This method is called each time,
     * before an output is requested from this object.
     * @return URI of the input source, maybe <code>null</code> if the
     * streaming uses other means of audio output.
     * @throws NoresourceError
     *         Error accessing the device.
     */
    URI getUriForNextFileOutput() throws NoresourceError;

    /**
     * The audio, delivered by the <code>audio</code> stream is queued after
     * the last element in the speaking queue.
     *
     * <p>
     * If barge-in can be used while queuing the audio depends on the
     * surrounding <code>&lt;prompt&gt;</code>.
     * </p>
     *
     * @param audio
     *        URI of the audio file to play.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            Error reading from the <code>AudioStream</code>.
     *
     * @since 0.3
     */
    void queueAudio(final URI audio) throws NoresourceError,
            BadFetchError;

    /**
     * Sets the document server to acquire further documents, e.g. for the
     * <code>&lt;audio&gt;</code> tag to retrieve the audio file to play.
     * @param server the document server.
     */
    void setDocumentServer(final DocumentServer server);

    /**
     * The document server needs the current session when retrieving documents.
     * @param session the current session.
     * @since 0.7
     */
    void setSession(final Session session);

    /**
     * Sets the reference to the {@link SynthesizedOutput} that is linked
     * to this file output.
     *
     * @param synthesizedOutput the linked synthesized output.
     */
    void setSynthesizedOutput(final SynthesizedOutput synthesizedOutput);
}

