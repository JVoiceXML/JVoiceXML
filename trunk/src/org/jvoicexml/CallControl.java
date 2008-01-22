/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.jvoicexml.event.error.NoresourceError;


/**
 * Call control.
 *
 * <p>
 * Objects that implement this interface are able to support making a third
 * party connection through a communications network, such as the telephone.
 * </p>
 *
 * <p>
 * Audio streaming is supported via URIs.
 * </p>
 *
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface CallControl
        extends ExternalResource {
    /**
     * Plays a stream from the given URI. The URI is obtained as a result of a
     * {@link SynthesizedOuput#getUriForNextSynthesisizedOutput()} or
     * {@link AudioFileOutput#getUriForNextFileOutput()} call.
     *
     * <p>
     * The play method is expected to run asynchronously.
     * </p>
     *
     * @param uri URI with audio data.
     * @param parameters parameters to use for playing.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void play(final URI uri, final Map<String, String> parameters)
        throws NoresourceError, IOException;

    /**
     * Starts recording to the given URI.
     * @param uri destination URI for recording.
     * @param parameters parameters to use for the recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void record(final URI uri, final Map<String, String> parameters)
        throws NoresourceError, IOException;

    /**
     * Stops a previously started recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @since 0.6
     */
    void stopRecord() throws NoresourceError;

    /**
     * Stops a previously started play.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @since 0.6
     */
    void stopPlay() throws NoresourceError;


    /**
     * Transfers the current call.
     * @param dest Platform specific destination address
     * @throws NoresourceError
     *         Error transferring the call.
     */
    void transfer(String dest) throws NoresourceError;
}
