/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/text/TextSynthesizedOutputFactory.java $
 * Version: $LastChangedRevision: 511 $
 * Date:    $LastChangedDate: 2007-10-16 23:33:57 +0200 (Di, 16 Okt 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.pool.KeyedResourcePool;
import org.jvoicexml.test.implementation.DummySynthesizedOutputFactory;

/**
 *Test cases for {@link KeyedResourcePool}.
 * @author Dirk Schnelle
 *
 */
public final class TestKeyedResourcePool {
    /** The number of instances that this pool can create. */
    private static final int INSTANCES = 500;

    /** The object to test. */
    private KeyedResourcePool<SynthesizedOutput> pool;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        final ResourceFactory<SynthesizedOutput> factory =
            new DummySynthesizedOutputFactory();
        ((DummySynthesizedOutputFactory) factory).setInstances(INSTANCES);
        pool = new KeyedResourcePool<SynthesizedOutput>();
        pool.addResourceFactory(factory);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.pool.KeyedResourcePool#borrowObject(java.lang.Object)}.
     * @throws Exception
     *         Test failed.
     * @throws NoresourceError
     *         Test failed.
     */
    @Test
    public void testBorrowObjectObject()  throws Exception, NoresourceError {
        final Object[] outputs = new Object[INSTANCES];
        for (int i = 0; i < INSTANCES; i++) {
            outputs[i] = pool.borrowObject("dummy");
        }
        Assert.assertEquals(INSTANCES, pool.getNumActive());

        for (int i = 0; i < INSTANCES; i++) {
            pool.returnObject("text", outputs[i]);
        }
        Assert.assertEquals(0, pool.getNumActive());
    }
}
