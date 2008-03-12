/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/CallControl.java $
 * Version: $LastChangedRevision: 641 $
 * Date:    $Date: 2008-01-29 10:15:10 +0100 (Di, 29 Jan 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;


/**
 * Telephony support.
 *
 * <p>
 * Objects that implement this interface are able to support making a third
 * party connection through a communications network, such as the telephone.
 * </p>
 *
 * <p>
 * In fact this is a bridge to use speech synthesis (via {@link SystemOutput}
 * and spoken input (via {@link UserInput} on a client, which may be a PBX.
 * The architecture is kept open at this point so that it is also possible
 * to hook other clients, like the console or a PDA.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 641 $
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface Telephony
        extends ExternalResource {
    /**
     * Plays a stream from the given output device.
     *
     * <p>
     * The play method is expected to run asynchronously.
     * </p>
     *
     * @param output the output device delivering the output.
     * @param parameters parameters to use for playing.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void play(final SystemOutput output, final Map<String, String> parameters)
        throws NoresourceError, IOException;

    /**
     * Stops a previously started play.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @since 0.6
     */
    void stopPlay() throws NoresourceError;

    /**
     * Starts recording to the given input device.
     * @param input input device to use for recording.
     * @param parameters parameters to use for the recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void record(final UserInput input, final Map<String, String> parameters)
        throws NoresourceError, IOException;

    /**
     * Starts recording to the given output stream.
     * @param input input device that can be used in parallel to recognize
     *              the input.
     * @param stream the stream where to send the recorded audio to.
     * @param parameters parameters to use for the recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void startRecording(final UserInput input, final OutputStream stream,
            final Map<String, String> parameters)
        throws NoresourceError, IOException;

    /**
     * Stops a previously started recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @since 0.6
     */
    void stopRecording() throws NoresourceError;


    /**
     * Transfers the current call.
     * @param dest Platform specific destination address
     * @throws NoresourceError
     *         Error transferring the call.
     */
    void transfer(String dest) throws NoresourceError;
}
