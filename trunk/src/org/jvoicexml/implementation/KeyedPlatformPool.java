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

import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * Platform pool
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
 * @since 0.6
 */
class KeyedPlatformPool
        extends GenericKeyedObjectPool {
    final PoolablePlatformFactory factory;

    /**
     * Constructs a new object.
     */
    public KeyedPlatformPool() {
        super();

        factory = new PoolablePlatformFactory();

        setFactory(factory);
        setWhenExhaustedAction(WHEN_EXHAUSTED_FAIL);
        setMaxTotal(1);
        setMinIdle(1);
    }

    /**
     * Adds the given platform and opens it.
     * @param platformFactory The {@link PlatformFactory} to add.
     */
    public void addPlatformFactory(final PlatformFactory platformFactory) {
        factory.addPlatformFactory(platformFactory);

        final String type = platformFactory.getType();

        preparePool(type, true);
    }
}
