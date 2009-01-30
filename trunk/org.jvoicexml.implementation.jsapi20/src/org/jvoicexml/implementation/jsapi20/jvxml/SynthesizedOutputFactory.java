/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20.jvxml;

import java.beans.PropertyVetoException;

import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineManager;
import javax.speech.EngineMode;
import javax.speech.synthesis.SynthesizerMode;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.jsapi20.Jsapi20SynthesizedOutput;
import org.jvoicexml.jsapi2.jse.synthesis.freetts.FreeTTSEngineListFactory;

/**
 * Demo implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link SynthesizedOuput} based on JSAPI 2.0.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public final class SynthesizedOutputFactory
        implements ResourceFactory<SynthesizedOutput> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(SynthesizedOutputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    private int currentInstance;

    /** Name of the default voice. */
    private String voice;

    /** Type of the created resources. */
    private final String type;

    private String mediaLocator;

    private int basePort;

    private int participantBasePort;

    /**
     * Constructs a new object.
     */
    public SynthesizedOutputFactory() {
        type = "jsapi20";
        currentInstance = 0;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering FreeTTS engine list factory...");
        }
        try {
            EngineManager.registerEngineListFactory(
                    FreeTTSEngineListFactory.class.getName());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...registered FreeTTS engine list factory");
            }
        } catch (EngineException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput createResource() throws NoresourceError {
        final SynthesizerMode desc = getEngineProperties();
        if (desc == null) {
            throw new NoresourceError(
                    "Cannot find any suitable SynthesizerMode");
        }
        String currentMediaLocator = null;
        if (mediaLocator != null) {
            currentMediaLocator = mediaLocator.replaceAll("#basePort#",
                    new Integer(getBasePort() + currentInstance * 2).toString());
            currentMediaLocator = currentMediaLocator.replaceAll(
                    "#participantBasePort#", new Integer(getParticipantBasePort()
                            + currentInstance * 2).toString());
        }
        currentInstance++;

        final Jsapi20SynthesizedOutput output = new Jsapi20SynthesizedOutput(
                desc, currentMediaLocator);

        output.setType(type);

        try {
            output.setVoice(voice);
        } catch (PropertyVetoException e) {
            throw new NoresourceError(
                    "error setting voice to '" + voice + "'!", e);
        }

        return output;
    }

    /**
     * Sets the number of instances that this factory will create.
     *
     * @param number
     *                Number of instances to create.
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
     *
     * @param voiceName
     *                Name of the default voice.
     */
    public void setDefaultVoice(final String voiceName) {
        voice = voiceName;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    public String getMediaLocator() {
        return mediaLocator;
    }

    public int getBasePort() {
        return basePort;
    }

    public int getParticipantBasePort() {
        return participantBasePort;
    }

    public void setMediaLocator(String mediaLocator) {
        this.mediaLocator = mediaLocator;
    }

    public void setBasePort(final int basePort) {
        this.basePort = basePort;
    }

    public void setParticipantBasePort(final int participantBasePort) {
        this.participantBasePort = participantBasePort;
    }

    /**
     * Retrieves the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     *         engine selection
     */
    public SynthesizerMode getEngineProperties() {
        try {
            final EngineMode mode = SynthesizerMode.DEFAULT;
            final EngineList engines = EngineManager.availableEngines(mode);
            if (engines.size() > 0) {
                return (SynthesizerMode) (engines.elementAt(0));
            } else {
                return null;
            }
        } catch (SecurityException ex) {
            LOGGER.warn(ex.getMessage(), ex);
            return null;
        } catch (IllegalArgumentException ex) {
            LOGGER.warn(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<SynthesizedOutput> getResourceType() {
        return SynthesizedOutput.class;
    }
}
