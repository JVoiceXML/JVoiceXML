/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/DummyCallControlFactory.java $
 * Version: $LastChangedRevision: 214 $
 * Date:    $LastChangedDate: 2007-02-13 09:19:18 +0100 (Di, 13 Feb 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.jtapi;

import org.jvoicexml.CallControl;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Demo implementation of a
 * {@link org.jvoicexml.implementation.ResourceFactory} for the
 * {@link CallControl} based on JTAPI 1.3.1.
 *
 * @author Dirk Schnelle
 * @version $Revision: 214 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public final class JtapiCallControlFactory
    implements ResourceFactory<CallControl> {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(JtapiCallControlFactory.class);

    /** Number of instances that this factory will create. */
    private int instances;

    /**
     * Constructs a new object.
     */
    public JtapiCallControlFactory() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering dummy call control...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public CallControl createResource()
        throws NoresourceError {
        return new JtapiCallControl();
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
        return "jtapi";
    }
}
