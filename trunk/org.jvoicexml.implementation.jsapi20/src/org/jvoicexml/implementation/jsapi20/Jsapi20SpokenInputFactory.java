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

package org.jvoicexml.implementation.jsapi20;

import java.net.URI;
import java.net.URISyntaxException;

import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineManager;
import javax.speech.recognition.RecognizerMode;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;

/**
 * Demo implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link SpokenInput} based on JSAPI 2.0.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public final class Jsapi20SpokenInputFactory
    implements ResourceFactory<SpokenInput> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Jsapi20SpokenInputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of the created resources. */
    private String type;

    /** The media locator factory. */
    private InputMediaLocatorFactory locatorFactory;

    /**
     * Constructs a new object.
     * @param engineFactory class name of the engine list factory.
     */
    public Jsapi20SpokenInputFactory(final String engineFactory) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering engine list factory '"
                    + engineFactory + "' for spoken input...");
        }
        try {
            EngineManager.registerEngineListFactory(engineFactory);
            LOGGER.info("registered '" + engineFactory
                    + "' engine list factory for spoken input");
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
            final InputMediaLocatorFactory factory) {
        locatorFactory = factory;
    }

    /**
     * Sets the type of this resource.
     *
     * @param resourceType
     *                type of the resource
     */
    public void setType(final String resourceType) {
        type = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput createResource() throws NoresourceError {
        final RecognizerMode desc = getEngineProperties();
        if (desc == null) {
            throw new NoresourceError(
                    "Cannot find any suitable RecognizerMode");
        }

        final Jsapi20SpokenInput input = new Jsapi20SpokenInput(desc,
                locatorFactory);
        if (locatorFactory != null) {
            URI locator;
            try {
                locator = locatorFactory.getSourceMediaLocator(input);
            } catch (URISyntaxException e) {
                throw new NoresourceError(e.getMessage(), e);
            }
            input.setMediaLocator(locator);
        }

        input.setType(type);

        return input;
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
    public int getInstances() {
        return instances;
    }

    /**
     * Get the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     *         engine selection
     */
    public RecognizerMode getEngineProperties() {
        try {
            final RecognizerMode mode = RecognizerMode.DEFAULT;
            EngineList list = EngineManager.availableEngines(mode);
            if (list.size() > 0) {
                return (RecognizerMode) (list.elementAt(0));
            } else {
                return null;
            }
        } catch (SecurityException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        } catch (IllegalArgumentException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public Class<SpokenInput> getResourceType() {
        return SpokenInput.class;
    }
}
