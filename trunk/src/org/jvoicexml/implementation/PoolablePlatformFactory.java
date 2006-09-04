/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
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

import java.util.Map;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;


/**
 * Factory, which manages a pool of platforms that can be used by an
 * application.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @see org.jvoicexml.implementation.ImplementationPlatform
 *
 * @since 0.5.1
 */
final class PoolablePlatformFactory
        implements KeyedPoolableObjectFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PoolablePlatformFactory.class);

    /** Known platform factories. */
    private final Map<String, PlatformFactory> factories;

    /**
     * Constructs a new object.
     */
    public PoolablePlatformFactory() {
        factories = new java.util.HashMap<String, PlatformFactory>();
    }

    /**
     * {@inheritDoc}
     */
    public Object makeObject(final Object key)
            throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new platform of type '" + key + "'...");
        }

        final PlatformFactory factory = factories.get(key);
        final Platform platform = factory.createPlatform();

        try {
            platform.open();
        } catch (org.jvoicexml.event.error.NoresourceError nre) {
            nre.printStackTrace();
        }

        return platform;
    }

    /**
     * {@inheritDoc}
     */
    public void destroyObject(final Object object, final Object object1)
            throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public boolean validateObject(final Object object, final Object object1) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void activateObject(final Object key, final Object object)
            throws Exception {
        final Platform platform = (Platform) object;
        platform.activate();
    }

    /**
     * {@inheritDoc}
     */
    public void passivateObject(final Object key, final Object object)
            throws Exception {
        final Platform platform = (Platform) object;
        platform.passivate();
    }

    /**
     * Adds the given platform and opens it.
     * @param factory The {@link PlatformFactory} to add.
     */
    public void addPlatformFactory(final PlatformFactory factory) {
        final String type = factory.getType();

        factories.put(type, factory);
    }
}
