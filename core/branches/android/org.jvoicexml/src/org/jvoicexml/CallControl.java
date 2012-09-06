/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/CallControl.java $
 * Version: $LastChangedRevision: 2913 $
 * Date:    $Date: 2012-01-30 02:41:09 -0600 (lun, 30 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.event.error.NoresourceError;


/**
 * Call control.
 *
 * <p>
 * Objects that implement this interface are able to support making a third
 * party connection through a communications network, such as the telephone.
 * </p>
 *
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2913 $
 */
public interface CallControl {
    /**
     * Plays a stream from the given output device.
     *
     * <p>
     * The play method is expected to run asynchronously.
     * </p>
     *
     * @param output the output device delivering the output.
     * @param props parameters to use for playing.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void play(final SystemOutput output, final CallControlProperties props)
        throws NoresourceError, IOException;

    /**
     * Starts recording to the given input device.
     * @param input input device to use for recording.
     * @param props parameters to use for the recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void record(final UserInput input, final CallControlProperties props)
        throws NoresourceError, IOException;

    /**
     * Retrieves the audio format that should be used for file recording.
     * @return audio format to use for recording.
     */
    AudioFormat getRecordingAudioFormat();

    /**
     * Starts recording to the given output stream.
     * @param input input device to use for recording. This can be used e.g. to
     *        recognize the recorded data.
     * @param stream the stream where to store the recording.
     * @param props parameters to use for the recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void startRecording(final UserInput input, final OutputStream stream,
            final CallControlProperties props)
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

    /**
     * Generate an application driven hangup.
     * @since 0.7.5
     */
    void hangup();
}
