/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.jvoicexml.ExternalResource;
import org.jvoicexml.RemoteConnectable;

/**
 * Pool to hold all instantiated resources of type <code>T</code>.
 * @param <T> Type of <code>ExternalResource</code> to produce in this factory.
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
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
 *
 * @since 0.5.1
 */
class KeyedResourcePool<T extends ExternalResource>
        extends GenericKeyedObjectPool {
    /** The factory. */
    private final PoolableResourceFactory factory;

    /**
     * Constructs a new object.
     */
    public KeyedResourcePool() {
        super();

        factory = new PoolableResourceFactory();

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

        /** @todo replace this by a per-key setting. */
        final int instances = resourceFactory.getInstances();
        setMaxTotal(instances);
        setMinIdle(instances);

        preparePool(type, true);
    }
}
