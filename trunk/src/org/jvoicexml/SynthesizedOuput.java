/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/SynthesizedOuput.java $
 * Version: $LastChangedRevision: 424 $
 * Date:    $Date: 2007-09-03 21:56:14 +0200 (Mo, 03 Sep 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml;

import java.net.URI;

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
 * If an audio output resource is not available, an
 * <code>error.noresource</code> event is thrown.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 424 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface SynthesizedOuput extends ExternalResource {
    /**
     * Obtains an URI that can be used as an input source for a
     * {@link CallControl} object. This method is called each time, before
     * an output is requested from this object.
     * @return URI of the input source, maybe <code>null</code> if the
     * streaming uses other means of audio output.
     * @throws NoresourceError
     *         Error accessing the device.
     */
    URI getUriForNextSynthesisizedOutput() throws NoresourceError;


    /**
     * The Speakable object is added to the end of the speaking queue and will
     * be spoken once it reaches the top of the queue.
     *
     * <p>
     * Objects implementing {@link SynthesizedOuput} are requested to use
     * {@link AudioFileOutput} instances to sequence audio files and synthesized
     * speech in SSML outputs.
     * </p>
     *
     * @param speakable
     *        Text to be spoken.
     * @param bargein
     *        <code>true</code> if the output can be canceled.
     * @param documentServer
     *        The document server to use.
     * @exception NoresourceError
     *            The output resource is not available.
     * @exception BadFetchError
     *            A URI within the speakable could not be obtained or a parsing
     *            error occurred.
     */
    void queueSpeakable(final SpeakableText speakable, final boolean bargein,
            final DocumentServer documentServer) throws NoresourceError,
            BadFetchError;

    /**
     * Speaks a plain text string.
     * @param text
     *        String contains plain text to be spoken.
     * @exception NoresourceError
     *            No recognizer allocated.
     * @exception BadFetchError
     *            Recognizer in wrong state.
     */
    void queuePlaintext(final String text)
        throws NoresourceError, BadFetchError;

    /**
     * Activates this synthesized output, when it is retrieved from the pool.
     * @since 0.5.5
     */
    void activate();

    /**
     * Passivates this synthesized output, when it is returned to the pool.
     * @since 0.5.5
     */
    void passivate();

    /**
     * Sets the reference to the {@link AudioFileOutput} that can be used to
     * output SSML.
     *
     * @param fileOutput the audio file output to use.
     */
    void setAudioFileOutput(final AudioFileOutput fileOutput);

    /**
     * Cancels the current output.
     * @exception NoresourceError
     *            Error accessing the resource.
     */
    void cancelOutput() throws NoresourceError;
}
