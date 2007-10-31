/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jsapi10.SynthesizerModeDescFactory;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.SynthesizedOutputConnectionHandler;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SynthesizedOuput} based on JSAPI 1.0.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5.5
 */
public final class SynthesizedOutputFactory
    implements ResourceFactory<SynthesizedOuput> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SynthesizedOutputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** A custom handler to handle remote connections. */
    private SynthesizedOutputConnectionHandler handler;

    /** Factory for the default {@link SynthesizerModeDesc}. */
    private SynthesizerModeDescFactory descriptorFactory;

    /** Type of the created resources. */
    private String type;

    /**
     * Constructs a new object.
     */
    public SynthesizedOutputFactory() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("registering FreeTTS engine central...");
            }
            Central.registerEngineCentral(FreeTTSEngineCentral.class.getName());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...registered FreeTTS engine central");
            }
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }

        type = "jsapi10";
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOuput createResource()
        throws NoresourceError {
        final SynthesizerModeDesc desc;
        if (descriptorFactory == null) {
            desc = null;
        } else {
            desc = descriptorFactory.getDescriptor();
        }
        final Jsapi10SynthesizedOutput output =
            new Jsapi10SynthesizedOutput(desc);

        output.setSynthesizedOutputConnectionHandler(handler);
        output.setType(type);

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
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the resource.
     * @param resourceType type of the resource.
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * Sets the factory for the default {@link SynthesizerModeDesc}.
     * @param desc the factory.
     */
    public void setSynthesizerModeDescriptor(
            final SynthesizerModeDescFactory desc) {
        descriptorFactory = desc;
    }

    /**
     * Sets a custom connection handler.
     * @param connectionHandler the connection handler.
     */
    public void setConnectionhandler(
            final SynthesizedOutputConnectionHandler connectionHandler) {
        handler = connectionHandler;
    }
}
