/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jsapi10.speakstrategy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * Listener for changes of the synthesizer's voice.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
class MultiPropertyChangeListener implements PropertyChangeListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MultiPropertyChangeListener.class);

    /** Property name voice. */
    public static String VOICE = "Voice";

    /** Property name speaking rate. */
    public static String SPEAKING_RATE = "SpeakingRate";

    /** Property name speaking rate. */
    public static String PITCH = "Pitch";

    /** Expected property changes. */
    private final Collection<String> expectedChanges;

    /**
     * Constructs a new object.
     */
    public MultiPropertyChangeListener() {
        expectedChanges = new java.util.ArrayList<String>();
    }

    /**
     * Adds the name of the property to the list of properties to wait for.
     * @param name name of the property.
     */
    public void addProperty(final String name) {
        synchronized (expectedChanges) {
            expectedChanges.add(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String name = evt.getPropertyName();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("property changed: " + evt.getPropertyName()
                    + ", " + evt.getOldValue() + ", "
                    + evt.getNewValue());
        }
        synchronized (expectedChanges) {
            if (!expectedChanges.remove(name)) {
                return;
            }
            if (!expectedChanges.isEmpty()) {
                return;
            }
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void waitChanged() throws InterruptedException{
        synchronized (expectedChanges) {
            if (expectedChanges.isEmpty()) {
                return;
            }
        }
        synchronized (this) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("waiting for voice change...");
            }
            while (!expectedChanges.isEmpty()) {
                this.wait(300);
            }
        }
    }
}

