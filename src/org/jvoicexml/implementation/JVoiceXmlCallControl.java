/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/JVoiceXmlUserInput.java $
 * Version: $LastChangedRevision: 639 $
 * Date:    $LastChangedDate: 2008-01-29 10:14:00 +0100 (Di, 29 Jan 2008) $
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

import org.jvoicexml.CallControl;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Basic wrapper for {@link CallControl}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 636 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class JVoiceXmlCallControl implements CallControl, ObservableCallControl,
    TelephonyProvider {
    /** The encapsulated telephony object. */
    private final Telephony telephony;

    /**
     * Constructs a new object.
     * @param tel encapsulated telephony object.
     */
    public JVoiceXmlCallControl(final Telephony tel) {
        telephony = tel;
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SystemOutput output,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        telephony.play(output, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        telephony.stopPlay();
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        telephony.record(input, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input, final OutputStream stream,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        telephony.record(input, stream, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecord() throws NoresourceError {
        telephony.stopRecord();
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
        telephony.transfer(dest);
    }

    /**
     * Retrieves the encapsulated telephony object.
     * @return the encapsulated telephony object.
     */
    public Telephony getTelephony() {
        return telephony;
    }

    /**
     * {@inheritDoc}
     */
    public void addCallControlListener(final CallControlListener listener) {
        if (telephony instanceof ObservableCallControl) {
            final ObservableCallControl observable =
                (ObservableCallControl) telephony;
            observable.addCallControlListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeCallControlListener(final CallControlListener listener) {
        if (telephony instanceof ObservableCallControl) {
            final ObservableCallControl observable =
                (ObservableCallControl) telephony;
            observable.removeCallControlListener(listener);
        }
    }

    /**
     * Checks if the corresponding telephony device is busy.
     * @return <code>true</code> if the telephony devices is busy.
     */
    public boolean isBusy() {
        return telephony.isBusy();
    }
}
