/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml.implementation;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.jvoicexml.ExternalResource;

/**
 * Pool to hold all instantiated resources of type <code>T</code>.
 *
 * <p>
 * The <code>KeyedResourcePool</code> uses a {@link ResourceFactory} to create
 * new objects for the pool until the number of instances is exceeded that is
 * set by the factory.
 * </p>
 *
 * @param <T> Type of <code>ExternalResource</code> to produce in this factory.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
 *
 * @since 0.5.1
 */
class KeyedResourcePool<T extends ExternalResource>
        extends GenericKeyedObjectPool {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(KeyedResourcePool.class);

    /** The factory. */
    private final PoolableResourceFactory<T> factory;

    /**
     * Constructs a new object.
     */
    public KeyedResourcePool() {
        super();

        factory = new PoolableResourceFactory<T>();

        setFactory(factory);
        setWhenExhaustedAction(WHEN_EXHAUSTED_FAIL);
    }

    /**
     * Adds the given resource factory.
     * @param resourceFactory The {@link ResourceFactory} to add.
     */
    public void addResourceFactory(final ResourceFactory<T> resourceFactory) {
        factory.addResourceFactory(resourceFactory);

        final String type = resourceFactory.getType();

        /**
         * @todo replace the number of instances by a per-key setting.
         */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading resources of type '" + type + "'...");
        }
        final int instances = resourceFactory.getInstances();
        setMaxTotal(instances);
        setMinIdle(instances);
        setMaxActive(instances);

        preparePool(type, true);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...resources loaded.");
        }
    }

    /**
     * Type safe return of the object to borrow from the pool.
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized T borrowObject(final Object key) throws Exception {
        return (T) super.borrowObject(key);
    }
}
