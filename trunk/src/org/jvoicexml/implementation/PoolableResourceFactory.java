/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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
import org.apache.log4j.Logger;
import org.jvoicexml.ExternalResource;
import org.jvoicexml.event.error.NoresourceError;


/**
 * Factory, which manages a pool of {@link ResourceFactory}s of type
 * <code>T</code>.
 * @param <T> Type of <code>ExternalResource</code> to produce in this factory.
 *
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
 * @since 0.5.5
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
    public Object makeObject(final Object key)
            throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new resource of type '" + key + "'...");
        }

        final ResourceFactory<T> factory = factories.get(key);
        final ExternalResource resource;
        try {
            resource = factory.createResource();
        } catch (NoresourceError e) {
            throw new Exception(e);
        }

        try {
            resource.open();
        } catch (org.jvoicexml.event.error.NoresourceError nre) {
            LOGGER.error("error opening external resource", nre);
        }

        return resource;
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
        final ExternalResource resource = (ExternalResource) object;
        resource.activate();
    }

    /**
     * {@inheritDoc}
     */
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
