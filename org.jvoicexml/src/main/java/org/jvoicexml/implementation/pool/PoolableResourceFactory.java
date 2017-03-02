/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.commons.pool.PoolableObjectFactory;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.ResourceFactory;


/**
 * Factory, which manages a pool of {@link ResourceFactory}s of type
 * <code>T</code>.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5.5
 *
 * @param <T> Type of the {@link ExternalResource} to produce in this
 * factory.
 */
final class PoolableResourceFactory<T extends ExternalResource>
        implements PoolableObjectFactory<T> {
    /** Known platform factories. */
    private final ResourceFactory<T> factory;

    /**
     * Constructs a new object.
     * @param resourceFactory the factory to create new resource
     */
     PoolableResourceFactory(final ResourceFactory<T> resourceFactory) {
        factory = resourceFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T makeObject()
            throws Exception {
        final T resource;
        try {
            resource = factory.createResource();
            resource.open();
        } catch (NoresourceError e) {
            throw new Exception("error creating resource of type "
                        + factory.getType() + ": "
                        + e.getMessage(), e);
        }

        return resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyObject(final T resource)
            throws Exception {
        resource.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateObject(final T object) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateObject(final T resource)
            throws Exception {
        try {
            resource.activate();
        } catch (NoresourceError e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivateObject(final T resource)
            throws Exception {
        try {
            resource.passivate();
        } catch (NoresourceError e) {
            throw new Exception(e.getMessage(), e);
        }
    }
}
