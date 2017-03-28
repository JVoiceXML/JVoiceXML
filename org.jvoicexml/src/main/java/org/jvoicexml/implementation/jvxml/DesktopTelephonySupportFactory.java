/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.Telephony;

/**
 * Implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link Telephony} interface for desktop/like environments.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5.5
 */
public final class DesktopTelephonySupportFactory
        implements ResourceFactory<Telephony> {
    /** Number of instances that this factory will create. */
    private int instances;

    /** Teh audio format to use for recording. */
    private AudioFormat recordingAudioFormat;

    /**
     * Constructs a new object.
     */
    public DesktopTelephonySupportFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Telephony createResource() throws NoresourceError {
        return new DesktopTelephonySupport(recordingAudioFormat);
    }

    /**
     * Sets the number of instances that this factory will create.
     * 
     * @param number
     *            Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInstances() {
        return instances;
    }

    /**
     * Sets the audio format to use for recording.
     * 
     * @param format
     *            the audio format
     * @since 0.7.8
     */
    public void setRecordingAudioFormat(final AudioFormat format) {
        recordingAudioFormat = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "desktop";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Telephony> getResourceType() {
        return Telephony.class;
    }
}
