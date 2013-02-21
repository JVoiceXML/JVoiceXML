/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.test.implementationplatform;

import org.jvoicexml.CallControl;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.implementation.SynthesizedOutputListener;

/**
 * This class provides a dummy {@link ImplementationPlatform} for testing
 * purposes.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class DummyImplementationPlatform
        implements ImplementationPlatform {
    /** Borrowed user input. */
    private UserInput input;

    /** Borrowed system output. */
    private SystemOutput output;

    /** Output listener to add once the system output is obtained. */
    private SynthesizedOutputListener outputListener;

    /** Borrowed call control. */
    private CallControl call;

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    public CallControl borrowCallControl() throws NoresourceError {
        call = new DummyCallControl();
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
    public void returnCallControl(final CallControl callControl) {
        call = null;
    }

    /**
     * {@inheritDoc}
     */
    public CharacterInput getCharacterInput() throws NoresourceError {
        return null;
    }

    /**
     * Sets the output listener to add once the system output is obtained.
     * @param listener the listener.
     */
    public void setSystemOutputListener(final SynthesizedOutputListener listener) {
        outputListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    public SystemOutput borrowSystemOutput() throws NoresourceError {
        DummySystemOutput dummyOutput = new DummySystemOutput();
        dummyOutput.addListener(outputListener);
        output = dummyOutput;
        return output;
    }

    /**
     * Retrieves a previously borrowed system output.
     * @return the borrowed system output.
     */
    public SystemOutput getBorrowedSystemOutput() {
        return output;
    }

    /**
     * {@inheritDoc}
     */
    public void returnSystemOutput(final SystemOutput systemOutput) {
        output = null;
    }

    /**
     * {@inheritDoc}
     */
    public void waitOutputQueueEmpty() {
    }

    /**
     * {@inheritDoc}
     */
    public UserInput borrowUserInput() throws NoresourceError {
        input = new DummyUserInput();
        return input;
    }

    /**
     * {@inheritDoc}
     */
    public void setEventHandler(final EventObserver observer) {
    }

    /**
     * {@inheritDoc}
     */
    public void returnCharacterInput(final CharacterInput charachterInput) {
    }

    /**
     * {@inheritDoc}
     */
    public void returnUserInput(final UserInput userIinput) {
        input = null;
    }

    /**
     * {@inheritDoc}
     */
    public UserInput getBorrowedUserInput() {
        return input;
    }
}
