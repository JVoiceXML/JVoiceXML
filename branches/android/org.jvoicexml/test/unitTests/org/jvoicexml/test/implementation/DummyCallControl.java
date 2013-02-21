/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/test/implementation/DummyCallControl.java $
 * Version: $LastChangedRevision: 2913 $
 * Date:    $Date: 2012-01-30 02:41:09 -0600 (lun, 30 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.test.implementation;

import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;

/**
 * This class provides a dummy {@link CallControl} for testing
 * purposes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2913 $
 * @since 0.6
 */
public final class DummyCallControl implements CallControl {

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioFormat getRecordingAudioFormat() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void play(final SystemOutput output,
            final CallControlProperties props)
            throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(final UserInput input,
            final CallControlProperties props)
            throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecording(
            final UserInput input,
            final OutputStream stream,
            final CallControlProperties props)
        throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopPlay() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecord() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transfer(final String dest) throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hangup() {
    }
}
