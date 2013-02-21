/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.jsapi20;

import java.net.URI;
import java.net.URISyntaxException;

import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineManager;
import javax.speech.EngineMode;
import javax.speech.synthesis.SynthesizerMode;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Demo implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link org.jvoicexml.implementation.SynthesizedOutput} based on
 * JSAPI 2.0.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public final class Jsapi20SynthesizedOutputFactory
        implements ResourceFactory<SynthesizedOutput> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Jsapi20SynthesizedOutputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of the created resources. */
    private String type;

    /** The media locator factory. */
    private OutputMediaLocatorFactory locatorFactory;

    /** The engine mode factory. */
    private SynthesizerModeFactory modeFactory;

    /**
     * Constructs a new object.
     * @param engineFactory class name of the engine list factory.
     */
    public Jsapi20SynthesizedOutputFactory(final String engineFactory) {
        type = "jsapi20";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering engine list factory '"
                    + engineFactory + "' for synthesized output...");
        }
        try {
            EngineManager.registerEngineListFactory(engineFactory);
            LOGGER.info("registered '" + engineFactory
                    + "' engine list factory for synthesized output");
        } catch (EngineException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Sets the media locator factory.
     * @param factory the media locator factory
     * @since 0.7
     */
    public void setMediaLocatorFactory(
            final OutputMediaLocatorFactory factory) {
        locatorFactory = factory;
    }

    /**
     * Sets the engine mode factory.
     * @param factory the engine mode factory
     * @since 0.7.5
     */
    public void setEngineModeFactory(
            final SynthesizerModeFactory factory) {
        modeFactory = factory;
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
        final Jsapi20SynthesizedOutput output =
            new Jsapi20SynthesizedOutput(desc, locatorFactory);
        output.setType(type);
        if (locatorFactory != null) {
            URI locator;
            try {
                locator = locatorFactory.getSourceMediaLocator(output);
            } catch (URISyntaxException e) {
                throw new NoresourceError(e.getMessage(), e);
            }
            output.setMediaLocator(locator);
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
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     * @param value new value for the type.
     * @since 0.7.5
     */
    public void setType(final String value) {
        type = value;
    }
    
    /**
     * Retrieves the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     *         engine selection
     * @exception NoresourceError
     *            error creating the synthesizer mode.
     */
    public SynthesizerMode getEngineProperties() throws NoresourceError {
        try {
            final EngineMode mode = getEngineMode();
            final EngineList engines = EngineManager.availableEngines(mode);
            if (engines.size() > 0) {
                return (SynthesizerMode) (engines.elementAt(0));
            } else {
                LOGGER.error("no synthesizer for mode '" + mode + "'");
                return null;
            }
        } catch (SecurityException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new NoresourceError(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves the engine mode.
     * @return engine mode
     * @since 0.7.5
     */
    private SynthesizerMode getEngineMode() {
        if (modeFactory == null) {
            return SynthesizerMode.DEFAULT;
        }
        return modeFactory.createDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    public Class<SynthesizedOutput> getResourceType() {
        return SynthesizedOutput.class;
    }
}
