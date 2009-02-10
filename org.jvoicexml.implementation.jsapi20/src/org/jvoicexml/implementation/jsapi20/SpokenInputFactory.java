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
public final class SpokenInputFactory implements ResourceFactory<SpokenInput> {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(SpokenInputFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    private int currentIntance;

    private String mediaLocator;

    private int basePort;

    private int participantBasePort;

    private final String type;

    /**
     * Constructs a new object.
     * @param engineFactory class name of the engine list factory.
     */
    public SpokenInputFactory(final String engineFactory) {
        currentIntance = 0;
        type = "jsapi20";
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
     * {@inheritDoc}
     */
    public SpokenInput createResource() throws NoresourceError {

        final RecognizerMode desc = getEngineProperties();
        if (desc == null) {
            throw new NoresourceError(
                    "Cannot find any suitable RecognizerMode");
        }

        String currentMediaLocator = null;
        if (mediaLocator != null) {
            currentMediaLocator = mediaLocator.replaceAll("#basePort#",
                    new Integer(getBasePort() + currentIntance * 2).toString());
            currentMediaLocator = currentMediaLocator.replaceAll(
                    "#participantBasePort#",
                    new Integer(getParticipantBasePort()
                            + currentIntance * 2).toString());
            currentIntance++;
        }

        final Jsapi20SpokenInput input = new Jsapi20SpokenInput(desc,
                currentMediaLocator);

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
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
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
