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

package org.jvoicexml.implementation;

import org.jvoicexml.CallControl;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link CallControl} based on JSAPI 1.0.
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
public final class DummyCallControlFactory implements ResourceFactory<
        CallControl> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DummyCallControlFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /**
     * Constructs a new object.
     */
    public DummyCallControlFactory() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering dummy call control...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CallControl createResource() throws NoresourceError {
        DummyCallControl callControl = new DummyCallControl();
        //callControl.setTerminalAddr("sip:1002@172.16.4.20");

        return callControl;
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
        return "dummy";
    }
}
