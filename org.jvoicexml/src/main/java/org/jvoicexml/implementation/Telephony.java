/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.CallControlProperties;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Telephony support as an external resource.
 *
 * <p>
 * Objects that implement this interface are able to support making a third
 * party connection through a communications network, such as the telephone.
 * </p>
 *
 * <p>
 * In fact this is a bridge to use speech synthesis (via
 * {@link SynthesizedOutput} and spoken input (via {@link SpokenInput} on a
 * client, which may be a PBX. Hence, it is able to handle the communication
 * between the client and the JVoiceXML server. The architecture is kept open at
 * this point so that it is also possible to hook other clients, like the
 * console or a PDA.
 * </p>
 * <p>
 * It is guaranteed that the session remains the same between the calls to
 * {@link org.jvoicexml.RemoteConnectable#connect(org.jvoicexml.ConnectionInformation)}
 * and
 * {@link org.jvoicexml.RemoteConnectable#disconnect(org.jvoicexml.ConnectionInformation)}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
public interface Telephony extends ExternalResource {
    /**
     * Creates the {@link CallControlProperties} to use.
     * @return new instance of a {@link CallControlProperties} or 
     * {@code null} in case the default should be used.
     * @since 0.7.9
     */
    CallControlProperties createCallControlProperties();

    /**
     * Plays a stream from the given output device.
     * This method gets called
     *  prior to calling
     * {@link SystemOutput#queueSpeakable(org.jvoicexml.SpeakableText, org.jvoicexml.SessionIdentifier, org.jvoicexml.DocumentServer)}
     * to prepare streaming from the synthesizer. Implementations may use this, 
     * method, e.g., to propagate {@link java.io.OutputStream}s to the
     * {@link SystemOutput} implementation.
     *
     * <p>
     * The play method is expected to run asynchronously.
     * </p>
     *
     * @param output
     *            the output device delivering the output.
     * @param props
     *            parameters to use for playing.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     * @since 0.6
     */
    void play(SynthesizedOutput output, CallControlProperties props)
            throws NoresourceError, IOException;

    /**
     * Stops a previously started play.
     * 
     * @exception NoresourceError
     *                Error accessing the terminal
     * @since 0.6
     */
    void stopPlay() throws NoresourceError;

    /**
     * Starts recording to the given input device. This method gets called
     *  prior to calling
     * {@link SpokenInput#startRecognition(org.jvoicexml.interpreter.datamodel.DataModel, org.jvoicexml.SpeechRecognizerProperties, org.jvoicexml.DtmfRecognizerProperties)}
     * to prepare streaming to the recognizer. Implementations may use this, 
     * method, e.g., to propagate {@link java.io.InputStream}s to the
     * {@link SpokenInput} implementation. 
     * 
     * @param input
     *            input device to use for recording.
     * @param props
     *            parameters to use for the recording.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     * @since 0.6
     */
    void record(SpokenInput input, CallControlProperties props)
            throws NoresourceError, IOException;

    /**
     * Retrieves the audio format that should be used for file recording.
     * 
     * @return audio format to use for recording.
     */
    AudioFormat getRecordingAudioFormat();

    /**
     * Starts recording to the given output stream.
     * 
     * @param input
     *            input device that can be used in parallel to recognize the
     *            input.
     * @param stream
     *            the stream where to send the recorded audio to.
     * @param props
     *            parameters to use for the recording.
     * @exception NoresourceError
     *                Error accessing the terminal
     * @exception IOException
     *                Error accessing the given URI.
     * @since 0.6
     */
    void startRecording(SpokenInput input, OutputStream stream,
            CallControlProperties props) throws NoresourceError, IOException;

    /**
     * Stops a previously started recording, e.g. after a recognition process
     * has been stopped.
     * 
     * @exception NoresourceError
     *                Error accessing the terminal
     * @since 0.6
     */
    void stopRecording() throws NoresourceError;

    /**
     * Transfers the current call.
     * 
     * @param dest
     *            Platform specific destination address
     * @throws NoresourceError
     *             Error transferring the call.
     */
    void transfer(String dest) throws NoresourceError;

    /**
     * Checks if this instance can actively be used to play back output or
     * record input.
     * 
     * @return {@code true} if this instance is active
     * @since 0.7.7
     */
    boolean isActive();

    /**
     * Hangs up the current call. Error hanging up the call.
     */
    void hangup();

    /**
     * Adds the given listener to the list of known listeners.
     * 
     * @param listener
     *            TelephonyListener
     */
    void addListener(TelephonyListener listener);

    /**
     * Removes the given listener from the list of known listeners.
     * 
     * @param listener
     *            TelephonyListener
     */
    void removeListener(TelephonyListener listener);
}
