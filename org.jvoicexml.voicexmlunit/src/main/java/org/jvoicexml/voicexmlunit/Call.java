/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.net.URI;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Call simulates a real telephone call.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public interface Call {

    /**
     * Calls the application identified by the given file.
     * 
     * @param file
     *            file of the application to call
     * @since 0.7.8
     */
    void call(File file);

    /**
     * Calls the application identified by the given URI.
     * 
     * @param uri
     *            URI of the application to call
     */
    void call(URI uri);

    /**
     * Retrieves the next output. This method is useful if the output should be
     * examined in more detail
     * 
     * @return the next output that has been captured
     */
    SsmlDocument getNextOutput();

    /**
     * Retrieves the next output. This method is useful if the output should be
     * examined in more detail
     * 
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     * @return the next output that has been captured
     */
    SsmlDocument getNextOutput(final long timeout);

    /**
     * Retrieves the last obtained output.
     * 
     * @return last captured output
     */
    SsmlDocument getLastOutput();

    /**
     * Waits for the next output and checks if this output matches the given
     * utterance.
     * 
     * @param utterance
     *            the expected utterance
     */
    void hears(final String utterance);

    /**
     * Waits for the next output and checks if this output matches the given
     * utterance. The output is expected to arrive in max timeout msec.
     * 
     * @param utterance
     *            the expected utterance
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     */
    void hears(final String utterance, final long timeout);

    /**
     * Waits for the next audio output and checks if this output matches the
     * given utterance.
     * 
     * @param uri
     *            the URI of the expected audio file
     * @since 0.7.8
     */
    void hearsAudio(final URI uri);

    /**
     * Waits for the next audio output and checks if this output matches the
     * given utterance. The output is expected to arrive in max timeout msec.
     * 
     * @param uri
     *            the URI of the expected audio file
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     * @since 0.7.8
     */
    void hearsAudio(final URI uri, final long timeout);

    /**
     * Waits until an input is expected and then sends the given utterance to
     * JVoiceXML.
     * 
     * @param utterance
     *            the utterance to send
     */
    void say(final String utterance);

    /**
     * Waits until an input is expected and then sends the given utterance to
     * JVoiceXML.
     * 
     * @param utterance
     *            the utterance to send
     * @param timeout
     *            max timeout to wait in msec, waits forever, if timeout is zero
     */
    void say(final String utterance, final long timeout);

    /**
     * Sends the given utterance to JVoiceXML.
     * 
     * @param digits
     *            the digits to enter
     */
    void enter(final String digits);

    /**
     * Sends the given utterance to JVoiceXML.
     * 
     * @param digits
     *            the digits to enter
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     */
    void enter(final String digits, final long timeout);

    /**
     * Delays until an input is expected, i.e. the voice browser is expecting
     * input.
     */
    void waitUnitExpectingInput();

    /**
     * Delays until an input is expected.
     * 
     * @param timeout
     *            the timeout to wait at max in msec, waits forever, if timeout
     *            is zero
     */
    void waitUnitExpectingInput(long timeout);

    /**
     * Issues a hangup event.
     */
    void hangup();

    /**
     * Retrieves the last observed error.
     * 
     * @return the last observed error, {@code null} if there was no error
     */
    JVoiceXMLEvent getLastError();
}
