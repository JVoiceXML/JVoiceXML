/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.apache.log4j.Logger;
import org.jvoicexml.event.error.NoresourceError;


/**
 * Factory, which manages a pool of {@link ResourceFactory}s of type
 * <code>T</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 *
 * @param <T> Type of the {@link ExternalResource} to produce in this
 * factory.
 */
final class PoolableResourceFactory<T extends ExternalResource>
        implements KeyedPoolableObjectFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(PoolableResourceFactory.class);

    /** Known platform factories. */
    private final Map<String, ResourceFactory<T>> factories;

    /**
     * Constructs a new object.
     */
    public PoolableResourceFactory() {
        factories = new java.util.HashMap<String, ResourceFactory<T>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object makeObject(final Object key)
            throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating a new resource of type '" + key + "'...");
        }

        final ResourceFactory<T> factory = factories.get(key);
        final ExternalResource resource;
        try {
            resource = factory.createResource();
            LOGGER.info("created a new resource of type '" + key + "' ("
                    + resource.getClass().getCanonicalName() + ")");
        } catch (NoresourceError e) {
            throw new Exception(e.getMessage(), e);
        }

        try {
            resource.open();
        } catch (org.jvoicexml.event.error.NoresourceError e) {
            LOGGER.error("error opening external resource", e);
            throw new Exception(e.getMessage(), e);
        }

        return resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyObject(final Object key, final Object object)
            throws Exception {
        final ExternalResource resource = (ExternalResource) object;
        resource.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateObject(final Object key, final Object object) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateObject(final Object key, final Object object)
            throws Exception {
        final ExternalResource resource = (ExternalResource) object;
        resource.activate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivateObject(final Object key, final Object object)
            throws Exception {
        final ExternalResource resource = (ExternalResource) object;
        resource.passivate();
    }

    /**
     * Adds the given factory to the pool.
     * @param factory The {@link ResourceFactory} to add.
     */
    public void addResourceFactory(final ResourceFactory<T> factory) {
        final String type = factory.getType();

        factories.put(type, factory);
    }
}
