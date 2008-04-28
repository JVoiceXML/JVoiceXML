/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.test.implementationplatform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.CallControl;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;

/**
 * This class provides a dummy {@link CallControl} for testing
 * purposes.
 *
 * @author DS01191
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class DummyCallControl implements CallControl {

    /**
     * {@inheritDoc}
     */
    public AudioFormat getRecordingAudioFormat() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void play(final SystemOutput output,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(
            final UserInput input,
            final OutputStream stream,
            final Map<String, String> parameters)
        throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecord() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
    }
}
