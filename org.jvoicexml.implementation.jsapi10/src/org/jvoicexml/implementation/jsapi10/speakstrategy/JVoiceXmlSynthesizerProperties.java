/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jsapi10.speakstrategy;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

/**
 * Container for the synthesizer properties.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.2
 */
final class JVoiceXmlSynthesizerProperties implements SynthesizerProperties {
    /** The pitch of the voice. */
    private float pitch;

    /** The range of the pitch. */
    private float pitchRange;

    /** The speaking rate. */
    private float speakingRate;

    /** The voice. */
    private Voice voice;

    /** The volume of the voice. */
    private float volume;

    /**
     * Constructs a new object.
     * @param props the synthesizer properties to copy the values
     */
    public JVoiceXmlSynthesizerProperties(final SynthesizerProperties props) {
        pitch = props.getPitch();
        pitchRange = props.getPitchRange();
        speakingRate = props.getSpeakingRate();
        voice = props.getVoice();
        volume = props.getVolume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getPitch() {
        return pitch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getPitchRange() {
        return pitchRange;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getSpeakingRate() {
        return speakingRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Voice getVoice() {
        return voice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getVolume() {
        return volume;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPitch(final float value) throws PropertyVetoException {
        pitch = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPitchRange(final float value) throws PropertyVetoException {
        pitchRange = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSpeakingRate(final float value)
        throws PropertyVetoException {
        speakingRate = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVoice(final Voice value) throws PropertyVetoException {
        voice = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVolume(final float value) throws PropertyVetoException {
        volume = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPropertyChangeListener(
            final PropertyChangeListener listener) {
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getControlComponent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePropertyChangeListener(
            final PropertyChangeListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
    }
}
