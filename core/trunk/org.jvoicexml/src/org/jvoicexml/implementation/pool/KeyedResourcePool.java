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

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;
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
public final class KeyedResourcePool<T extends ExternalResource> {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(KeyedResourcePool.class);

    /** Known pools. */
    private final Map<String, ObjectPool> pools;

    /**
     * Constructs a new object.
     */
    public KeyedResourcePool() {
        super();
        pools = new java.util.HashMap<String, ObjectPool>();
    }

    /**
     * Adds the given resource factory.
     * @param resourceFactory The {@link ResourceFactory} to add.
     * @exception Exception error populating the pool
     */
    public void addResourceFactory(
            final ResourceFactory<T> resourceFactory) throws Exception {
        final PoolableObjectFactory factory =
            new PoolableResourceFactory<T>(resourceFactory);
        final GenericObjectPool pool = new GenericObjectPool(factory);
        final int instances = resourceFactory.getInstances();
        pool.setMinIdle(instances);
        pool.setMaxActive(instances);
        pool.setMaxIdle(instances);
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        final String type = resourceFactory.getType();
        pools.put(type, pool);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("loading resources of type '" + type + "'...");
        }
        for (int i = 0; i < instances; i++) {
            pool.addObject();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...resources loaded.");
        }
    }

    /**
     * Type safe return of the object to borrow from the pool.
     * @param key the type of the object to borrow from the pool
     * @return borrowed object
     * @exception NoresourceError
     *            the object could not be borrowed
     */
    @SuppressWarnings("unchecked")
    public synchronized T borrowObject(final Object key)
        throws NoresourceError {
        final ObjectPool pool = pools.get(key);
        if (pool == null) {
            throw new NoresourceError("Pool of type '" + key + "' is unknown!");
        }
        T resource;
        try {
            resource = (T) pool.borrowObject();
        } catch (NoSuchElementException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (Exception e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        LOGGER.info("borrowed object of type '" + key + "' ("
                + resource.getClass().getCanonicalName() + ")");
        if (LOGGER.isDebugEnabled()) {
            final int active = pool.getNumActive();
            final int idle = pool.getNumIdle();
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
     * @throws NoresourceError
     *         Error returning the object to the pool.
     * @since 0.6
     */
    public synchronized void returnObject(final String key,
            final T resource) throws NoresourceError {
        final ObjectPool pool = pools.get(key);
        if (pool == null) {
            throw new NoresourceError("Pool of type '" + key + "' is unknown!");
        }
        try {
            pool.returnObject(resource);
        } catch (Exception e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        LOGGER.info("returned object of type '" + key + "' ("
                + resource.getClass().getCanonicalName() + ")");

        if (LOGGER.isDebugEnabled()) {
            final int active = pool.getNumActive();
            final int idle = pool.getNumIdle();
            LOGGER.debug("pool has now " + active
                         + " active/" + idle + " idle for key '" + key
                         + "' (" + resource.getClass().getCanonicalName()
                         + ") after return");
        }
    }

    /**
     * Retrieves the number of active resources in all pools.
     * @return number of active resources
     * @since 0.7.3
     */
    public synchronized int getNumActive() {
        int active = 0;
        final Collection<ObjectPool> col = pools.values();
        for (ObjectPool pool : col) {
            active += pool.getNumActive();
        }
        return active;
    }

    /**
     * Retrieves the number of active resources in the pool for the given key.
     * @param key the key
     * @return number of active resources
     * @since 0.7.3
     */
    public synchronized int getNumActive(final String key) {
        final ObjectPool pool = pools.get(key);
        return pool.getNumActive();
    }

    /**
     * Retrieves the number of idle resources in all pools.
     * @return number of idle resources
     * @since 0.7.3
     */
    public synchronized int getNumIdle() {
        int idle = 0;
        final Collection<ObjectPool> col = pools.values();
        for (ObjectPool pool : col) {
            idle += pool.getNumIdle();
        }
        return idle;
    }

    /**
     * Retrieves the number of idle resources in the pool for the given key.
     * @param key the key
     * @return number of idle resources
     * @since 0.7.3
     */
    public synchronized int getNumIdle(final String key) {
        final ObjectPool pool = pools.get(key);
        return pool.getNumIdle();
    }

    /**
     * Closes all pools for all keys.
     * @throws Exception
     *         error closing a pool
     * @since 0.7.3
     */
    public synchronized void close() throws Exception {
        final Collection<ObjectPool> col = pools.values();
        for (ObjectPool pool : col) {
            pool.close();
        }
    }
}
