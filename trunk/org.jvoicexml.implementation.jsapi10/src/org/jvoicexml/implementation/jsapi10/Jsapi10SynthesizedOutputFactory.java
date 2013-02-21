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

package org.jvoicexml.implementation.jsapi10;

import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SynthesizedOutput} based on JSAPI 1.0.
 *
 * <p>
 * Custom implementations are expected to override
 * {@link #registerEngineCentral()} to register the JSAPI compliant
 * {@link javax.speech.EngineCentral} for the
 * {@link javax.speech.synthesis.Synthesizer}. Afterwards the default
 * mechanisms of JSAPI 1.0  are used to instantiate the
 * {@link javax.speech.synthesis.Synthesizer}.
 * </p>
 *
 * <p>
 * The {@link SynthesizerModeDesc} can be specified in the following ways:
 * <ol>
 * <li>setting the default descriptor directly via
 * {@link #setSynthesizerModeDescriptor(SynthesizerModeDesc)} or by</li>
 * <li>using a {@link JVoiceXmlSynthesizerModeDescFactory}.</li>
 * </ol>
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public class Jsapi10SynthesizedOutputFactory
    implements ResourceFactory<SynthesizedOutput> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Jsapi10SynthesizedOutputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** A custom handler to handle remote connections. */
    private SynthesizedOutputConnectionHandler handler;

    /** Factory for the default {@link SynthesizerModeDesc}. */
    private SynthesizerModeDescFactory descriptorFactory;

    /** The default descriptor. */
    private SynthesizerModeDesc defaultDescriptor;

    /** Type of the created resources. */
    private String type;

    /**
     * Creates a new object and registers the engines.
     */
    public Jsapi10SynthesizedOutputFactory() {
        type = "jsapi10";

        try {
            registerEngineCentral();
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }
    }

    /**
     * Registers the {@link javax.speech.EngineCentral} so that a
     * {@link javax.speech.synthesis.Synthesizer} can be created via
     * {@link javax.speech.Central#createSynthesizer(javax.speech.EngineModeDesc)}.
     * @exception EngineException
     *            Error registering the engine central.
     */
    public void registerEngineCentral() throws EngineException {
    }

    /**
     * {@inheritDoc}
     */
    public final SynthesizedOutput createResource() throws NoresourceError {
        final SynthesizerModeDesc desc = getDescriptor();
        final Jsapi10SynthesizedOutput output = new Jsapi10SynthesizedOutput(
                desc);

        output.setSynthesizedOutputConnectionHandler(handler);
        output.setType(type);

        return output;
    }

    /**
     * Determines the {@link SynthesizerModeDesc} for the instance to create.
     * @return mode descriptor to use.
     */
    private SynthesizerModeDesc getDescriptor() {
        if (descriptorFactory == null) {
            if (defaultDescriptor == null) {
                return null;
            } else {
                return defaultDescriptor;
            }
        }

        return descriptorFactory.createDescriptor();
    }

    /**
     * Sets the number of instances that this factory will create.
     *
     * @param number
     *            Number of instances to create.
     */
    public final void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public final int getInstances() {
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    public final String getType() {
        return type;
    }

    /**
     * Sets the type of the resource.
     *
     * @param resourceType
     *            type of the resource.
     */
    public final void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * Sets the factory for the default {@link SynthesizerModeDesc}.
     *
     * @param desc
     *            the factory.
     */
    public final void setSynthesizerModeDescriptorFactory(
            final SynthesizerModeDescFactory desc) {
        descriptorFactory = desc;
    }

    /**
     * Sets the factory for the default {@link SynthesizerModeDesc}.
     *
     * @param desc
     *            the factory.
     */
    public final void setSynthesizerModeDescriptor(
            final SynthesizerModeDesc desc) {
        defaultDescriptor = desc;
    }

    /**
     * Sets a custom connection handler.
     *
     * @param connectionHandler
     *            the connection handler.
     */
    public final void setConnectionhandler(
            final SynthesizedOutputConnectionHandler connectionHandler) {
        handler = connectionHandler;
    }

    /**
     * {@inheritDoc}
     */
    public final Class<SynthesizedOutput> getResourceType() {
        return SynthesizedOutput.class;
    }
}
