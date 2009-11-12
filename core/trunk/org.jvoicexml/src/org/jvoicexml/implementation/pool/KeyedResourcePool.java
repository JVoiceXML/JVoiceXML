/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.pool;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.ResourceFactory;

/**
 * Pool to hold all instantiated resources of type <code>T</code>.
 *
 * <p>
 * The <code>KeyedResourcePool</code> uses a {@link ResourceFactory} to create
 * new objects for the pool until the number of instances is exceeded that is
 * set by the factory.
 * </p>
 *
 * @param <T> Type of {@link ExternalResource} to produce in this factory.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.5.1
 */
public class KeyedResourcePool<T extends ExternalResource>
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
    public final void addResourceFactory(
            final ResourceFactory<T> resourceFactory) {
        factory.addResourceFactory(resourceFactory);

        final String type = resourceFactory.getType();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading resources of type '" + type + "'...");
        }
        final int instances = resourceFactory.getInstances();

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
    @Override
    public final synchronized T borrowObject(final Object key)
        throws Exception {
        @SuppressWarnings("unchecked")
        final T resource = (T) super.borrowObject(key);

        if (LOGGER.isDebugEnabled()) {
            final int active = getNumActive(key);
            final int idle = getNumIdle(key);
            LOGGER.debug("pool has now " + active
                         + " active/" + idle + " idle for key '" + key
                         + "' (" + resource.getClass().getCanonicalName()
                         + ") after borrow");
        }

        return resource;
    }

    /**
     * Returns a previously borrowed resource to the pool.
     * @param key resource type.
     * @param resource resource to return.
     * @throws Exception
     *         Error returning the object to the pool.
     * @since 0.6
     */
    public final synchronized void returnObject(final String key,
            final T resource) throws Exception {
        super.returnObject(key, resource);

        if (LOGGER.isDebugEnabled()) {
            final int active = getNumActive(key);
            final int idle = getNumIdle(key);
            LOGGER.debug("pool has now " + active
                         + " active/" + idle + " idle for key '" + key
                         + "' (" + resource.getClass().getCanonicalName()
                         + ") after return");
        }
    }
}
