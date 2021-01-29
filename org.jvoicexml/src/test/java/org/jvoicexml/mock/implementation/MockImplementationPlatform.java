/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mock.implementation;

import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * This class provides a dummy {@link ImplementationPlatform} for testing
 * purposes.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public final class MockImplementationPlatform
        implements ImplementationPlatform {
    /** Borrowed user input. */
    private UserInput input;

    /** Borrowed system output. */
    private SystemOutput output;

    /** Output listener to add once the system output is obtained. */
    private SynthesizedOutputListener outputListener;

    /** Borrowed call control. */
    private CallControl call;
    
    /** The session. */
    private Session session;

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public CallControl getCallControl() throws NoresourceError {
        if (call == null) {
            call = new MockCallControl();
        }
        return call;
    }

    /**
     * {@inheritDoc}
     */
    public CallControl getBorrowedCallControl() {
        return call;
    }

    /**
     * {@inheritDoc}
     */
    public DtmfInput getCharacterInput() throws NoresourceError {
        return null;
    }

    /**
     * Sets the output listener to add once the system output is obtained.
     * @param listener the listener.
     */
    public void setSystemOutputListener(
            final SynthesizedOutputListener listener) {
        outputListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    public SystemOutput getSystemOutput() throws NoresourceError {
        if (output == null) {
            MockSystemOutput mock = new MockSystemOutput();
            if (outputListener != null) {
                mock.addListener(outputListener);
            }
            output = mock;
        }
        return output;
    }

    /**
     * {@inheritDoc}
     */
    public void waitOutputQueueEmpty() {
    }

    /**
     * {@inheritDoc}
     */
    public UserInput getUserInput() throws NoresourceError {
        if (input == null) {
            input = new MockUserInput();
        }
        return input;
    }

    /**
     * {@inheritDoc}
     */
    public void setEventBus(final EventBus bus) {
    }

    /**
     * {@inheritDoc}
     */
    public UserInput getBorrowedUserInput() {
        return input;
    }

    /**
     * {@inheritDoc}
     */
    public void setSession(final Session currentSession) {
        session = currentSession;
    }

    /**
     * {@inheritDoc}
     */
    public void waitNonBargeInPlayed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserInputActive() {
        return input != null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHungup() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queuePrompt(SpeakableText speakable, DocumentServer server)
            throws BadFetchError, NoresourceError,
            ConnectionDisconnectHangupEvent {
        final SystemOutput out = getSystemOutput();
        final SessionIdentifier sessionId = session.getSessionId();
        out.queueSpeakable(speakable, sessionId, server);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playPrompts(DocumentServer server,
            CallControlProperties callProps) throws BadFetchError,
            NoresourceError, ConnectionDisconnectHangupEvent {
        final SystemOutput out = getSystemOutput();
        final SessionIdentifier sessionId = session.getSessionId();
        out.playPrompts(sessionId, server, null);
    }
}
