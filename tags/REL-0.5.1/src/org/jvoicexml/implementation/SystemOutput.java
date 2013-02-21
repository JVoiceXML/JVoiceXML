/*
 * File:    $RCSfile: SystemOutput.java,v $
 * Version: $Revision: 1.16 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.OutputStream;

import javax.sound.sampled.AudioInputStream;

import org.jvoicexml.documentserver.DocumentServer;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

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
 * @author Dirk Schnelle
 * @version $Revision: 1.16 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface SystemOutput
        extends ExternalResource {
    /**
     * Sets the listener for system output events.
     *
     * <p>
     * The implementation of this interface must notify the listener
     * about all events.
     * </p>
     *
     * @param listener The listener.
     * @since 0.5
     */
    void setSystemOutputListener(final SystemOutputListener listener);

    /**
     * Sets the output stream, where the output should be directed to.
     *
     * <p>
     * <b>Note:</b> Unfortunately this is not a feature of all TTS engines. If
     * no <code>OutputStream</code> is given, the default output of the TTS
     * engine is used. This may have consequences on the usability with a
     * calling device.
     * </p>
     *
     * @param out The output to use.
     *
     * @exception NoresourceError
     *            The output resource is not available.
     */
    void setOutputStream(final OutputStream out)
            throws NoresourceError;

    /**
     * The Speakable object is added to the end of the speaking queue and will
     * be spoken once it reaches the top of the queue.
     *
     * @param speakable
     *        Text to be spoken.
     * @param bargein
     *        <code>true</code> if the output can be cancelled.
     * @param documentServer
     *        The document server to use.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            A URI within the speakable could not be obtained or a parsing
     *            error occured.
     */
    void queueSpeakable(final SpeakableText speakable, final boolean bargein,
                        final DocumentServer documentServer)
            throws NoresourceError, BadFetchError;

    /**
     * The audio, delivered by the <code>audio</code> stream is queued after
     * the last element in the speaking queue.
     *
     * <p>
     * If bargein can be used while queuing the audio depends on the surrounding
     * <code>&lt;prompt&gt;</code>.
     * </p>
     *
     * @param audio
     *        Stream with audio data.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            Error reading from the <code>AudioStream</code>.
     *
     * @since 0.3
     */
    void queueAudio(final AudioInputStream audio)
            throws NoresourceError, BadFetchError;

    /**
     * Cancels the current output from the TTS engine and queued audio
     * for all entries in the queue that allow bargein.
     *
     * <p>
     * The implementation has to maintain a list of cancellable outputs
     * dependingon the <code>bargein</code> flag.
     * </p>
     *
     * @exception NoresourceError
     *            The output resource is not available.
     *
     * @since 0.5
     */
    void cancelOutput()
            throws NoresourceError;
}
