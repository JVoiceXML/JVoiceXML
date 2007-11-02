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

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.recognition.RecognizerModeDesc;

import org.jvoicexml.SpokenInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jsapi10.Jsapi10SpokenInput;
import org.jvoicexml.implementation.jsapi10.SpokenInputConnectionHandler;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link SpokenInput} based on JSAPI 1.0.
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
public final class SpokenInputFactory
    implements ResourceFactory<SpokenInput> {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(SpokenInputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** A custom handler to handle remote connections. */
    private SpokenInputConnectionHandler handler;

    /**
     * Constructs a new object.
     */
    public SpokenInputFactory() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("registering sphinx4 engine central...");
            }
            Central.registerEngineCentral(Sphinx4EngineCentral.class.getName());
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource()
        throws NoresourceError {
        final RecognizerModeDesc desc = getEngineProperties();

        final Jsapi10SpokenInput input = new Jsapi10SpokenInput(desc);

        if (handler != null) {
            input.setSpokenInputConnectionHandler(handler);
        }

        return input;
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
        return "jsapi10";
    }

    /**
     * Get the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     *   engine selection
     *
     * @todo This is more or less a bogus implementation and has to be replaced,
     * once sphinx4 is more JSAPI compliant.
     */
    public RecognizerModeDesc getEngineProperties() {
        return new Sphinx4RecognizerModeDesc();
    }

    /**
     * Sets a custom connection handler.
     * @param connectionHandler the connection handler.
     */
    public void setSynthesizedOutputConnectionHandler(
            final SpokenInputConnectionHandler connectionHandler) {
        handler = connectionHandler;
    }
}
