/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineModeDesc;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.apache.log4j.Logger;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jsapi10.AudioOutput;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for JSAPI 1.0.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class SystemOutputFactory
    implements ResourceFactory<SystemOutput> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SystemOutputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** Name of the default voice. */
    private String voice;

    /**
     * Constructs a new object.
     */
    public SystemOutputFactory() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("registering FreeTTS engine central...");
            }
            Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SystemOutput createResource()
        throws NoresourceError {
        final EngineModeDesc desc = getEngineProperties();
        final AudioOutput output = new AudioOutput(desc);

        try {
            output.setVoice(voice);
        } catch (PropertyVetoException e) {
            throw new NoresourceError("error setting voice to '" + voice + "'!",
                    e);
        }

        return output;
    }

    /**
     * Sets the number of instances that this factory will create.
     * @param number Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }

    /**
     * Sets the default voice for the synthesizers.
     * @param voiceName Name of the default voice.
     */
    public void setDefaultVoice(final String voiceName) {
        voice = voiceName;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi10";
    }

    /**
     * Retrieves the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     * engine selection
     */
    public EngineModeDesc getEngineProperties() {
        return new SynthesizerModeDesc(null, null, Locale.US, null, null);
    }
}
