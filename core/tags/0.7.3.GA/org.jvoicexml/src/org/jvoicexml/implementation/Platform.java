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

package org.jvoicexml.implementation;

/**
 * Basic implementation of a {@link PlatformFactory}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Platform implements PlatformFactory {
    /** The used spoken input. */
    private ResourceFactory<SpokenInput> spokenInputFactory;

    /** The used synthesized output. */
    private ResourceFactory<SynthesizedOutput> synthesizedOutputFactory;

    /** The used audio file output. */
    private ResourceFactory<AudioFileOutput> audioFileOutputFactory;

    /** The used telephonyFactory implementation. */
    private ResourceFactory<Telephony> telephonyFactory;

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<SpokenInput> getSpokeninput() {
        return spokenInputFactory;
    }

    /**
     * Sets the spoken input implementation.
     * @param input the spokenInputFactory to set
     */
    public void setSpokeninput(final ResourceFactory<SpokenInput> input) {
        spokenInputFactory = input;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<SynthesizedOutput> getSynthesizedoutput() {
        return synthesizedOutputFactory;
    }

    /**
     * Sets the synthesized output implementation.
     * @param output the synthesizedOutputFactory to set
     */
    public void setSynthesizedoutput(
            final ResourceFactory<SynthesizedOutput> output) {
        synthesizedOutputFactory = output;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<AudioFileOutput> getAudiofileoutput() {
        return audioFileOutputFactory;
    }

    /**
     * Sets the audio file output implementation.
     * @param output the audioFileOutputFactory to set
     */
    public void setAudiofileoutput(
            final ResourceFactory<AudioFileOutput> output) {
        audioFileOutputFactory = output;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceFactory<Telephony> getTelephony() {
        return telephonyFactory;
    }

    /**
     * Sets the telephonyFactory implementation.
     * @param tel the telephonyFactory to set
     */
    public void setTelephony(final ResourceFactory<Telephony> tel) {
        telephonyFactory = tel;
    }
}
