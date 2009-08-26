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

import org.apache.log4j.Logger;

/**
 * Listener for changes of the synthesizer's voice.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
class VoiceChangeListener implements PropertyChangeListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(VoiceChangeListener.class);

    /** Flag if the voice has already changed. */
    private boolean changed = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals("Voice")) {
            return;
        }
        changed = true;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("voice changed: " + evt.getPropertyName()
                    + ", " + evt.getOldValue() + ", "
                    + evt.getNewValue());
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void waitChanged() throws InterruptedException{
        if (changed) {
            return;
        }
        synchronized (this) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("waiting for voice change...");
            }
            this.wait();
        }
    }
}

