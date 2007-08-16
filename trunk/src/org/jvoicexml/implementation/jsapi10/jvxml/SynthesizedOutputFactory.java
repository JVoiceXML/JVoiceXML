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

import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
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

    /** Name of the default voice. */
    private String voice;

    /** A custom handler to handle remote connections. */
    private SynthesizedOutputConnectionHandler handler;

    /**
     * Constructs a new object.
     */
    public SynthesizedOutputFactory() {
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
    public SynthesizedOuput createResource()
        throws NoresourceError {
        final SynthesizerModeDesc desc = getEngineProperties();
        final Jsapi10SynthesizedOutput output =
            new Jsapi10SynthesizedOutput(desc);

        output.setSynthesizedOutputConnectionHandler(handler);

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
    public SynthesizerModeDesc getEngineProperties() {
        return new SynthesizerModeDesc(null, null, Locale.US, null, null);
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
