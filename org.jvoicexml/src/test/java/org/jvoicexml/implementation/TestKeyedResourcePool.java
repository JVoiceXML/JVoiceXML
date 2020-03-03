/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.mockito.Mockito;

/**
 *Test cases for {@link KeyedResourcePool}.
 * @author Dirk Schnelle
 *
 */
public final class TestKeyedResourcePool {
    /** The object to test. */
    private KeyedResourcePool<SynthesizedOutput> pool;

    /** The resource factory inside the pool. */
    private ResourceFactory<SynthesizedOutput> factory;
    
    /**
     * Prepare the test setup.
     * @throws NoresourceError test failed
     * @throws Exception test failed
     * @since 0.7.9
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws NoresourceError, Exception {
        factory = Mockito.mock(ResourceFactory.class);
        Mockito.when(factory.getType()).thenReturn("dummy");
        final SynthesizedOutput output = Mockito.mock(SynthesizedOutput.class);
        Mockito.when(factory.createResource()).thenReturn(output);
        pool = new KeyedResourcePool<SynthesizedOutput>();
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
        final int instances = 500;
        Mockito.when(factory.getInstances()).thenReturn(instances);
        pool.addResourceFactory(factory);
        Assert.assertEquals(instances, pool.getNumIdle());
        final String key = factory.getType();
        final SynthesizedOutput[] outputs = new SynthesizedOutput[instances];
        for (int i = 0; i < instances; i++) {
            outputs[i] = pool.borrowObject(key);
        }
        Assert.assertEquals(instances, pool.getNumActive(key));

        for (int i = 0; i < instances; i++) {
            pool.returnObject(key, outputs[i]);
        }
        Assert.assertEquals(0, pool.getNumActive(key));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.pool.KeyedResourcePool#borrowObject(java.lang.Object)}.
     * @throws Exception
     *         Test failed.
     * @throws NoresourceError
     *         Test succeeded
     */
    @Test(expected = NoresourceError.class)
    public void testBorrowObjectObjectExceed()
        throws Exception, NoresourceError {
        final int instances = 10;
        Mockito.when(factory.getInstances()).thenReturn(instances);
        pool.addResourceFactory(factory);
        pool = new KeyedResourcePool<SynthesizedOutput>();
        pool.addResourceFactory(factory);
        Assert.assertEquals(instances, pool.getNumIdle());
        final String key = factory.getType();
        final SynthesizedOutput[] outputs = new SynthesizedOutput[instances];
        for (int i = 0; i < instances; i++) {
            Assert.assertEquals(i, pool.getNumActive(key));
            Assert.assertEquals(instances - i, pool.getNumIdle(key));
            outputs[i] = pool.borrowObject(key);
        }
        Assert.assertEquals(instances, pool.getNumActive(key));
        pool.borrowObject(key);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.pool.KeyedResourcePool#borrowObject(java.lang.Object)}.
     * @throws Exception
     *         Test failed.
     * @throws NoresourceError
     *         Test failed
     */
    @Test
    public void testBorrowObjectObjectMultipleKey()
        throws Exception, NoresourceError {
        final int instancesKey1 = 3;
        @SuppressWarnings("unchecked")
        final ResourceFactory<SynthesizedOutput> factory1 =
            Mockito.mock(ResourceFactory.class);
        Mockito.when(factory1.getInstances()).thenReturn(instancesKey1);
        Mockito.when(factory1.getType()).thenReturn("dummy1");
        final SynthesizedOutput output1 = Mockito.mock(SynthesizedOutput.class);
        Mockito.when(factory1.createResource()).thenReturn(output1);
        final int instancesKey2 = 5;
        @SuppressWarnings("unchecked")
        final ResourceFactory<SynthesizedOutput> factory2 =
                Mockito.mock(ResourceFactory.class);
        Mockito.when(factory2.getInstances()).thenReturn(instancesKey2);
        Mockito.when(factory2.getType()).thenReturn("dummy2");
        final SynthesizedOutput output2 = Mockito.mock(SynthesizedOutput.class);
        Mockito.when(factory2.createResource()).thenReturn(output2);
        pool = new KeyedResourcePool<SynthesizedOutput>();
        pool.addResourceFactory(factory1);
        pool.addResourceFactory(factory2);
        final String key1 = factory1.getType();
        final String key2 = factory2.getType();
        Assert.assertEquals(instancesKey1, pool.getNumIdle(key1));
        Assert.assertEquals(instancesKey2, pool.getNumIdle(key2));
        Assert.assertEquals(instancesKey1 + instancesKey2, pool.getNumIdle());
        final String[] keys = new String[]
                            {key2, key1, key2, key1, key2, key1, key2, key2};
        final SynthesizedOutput[] outputs =
            new SynthesizedOutput[instancesKey1 + instancesKey2];
        for (int i = 0; i < outputs.length; i++) {
            final String key = keys[i];
            outputs[i] = pool.borrowObject(key);
        }
        Assert.assertEquals(instancesKey1, pool.getNumActive(key1));
        Assert.assertEquals(instancesKey2, pool.getNumActive(key2));
        for (int i = 0; i < outputs.length; i++) {
            final String key = keys[i];
            pool.returnObject(key, outputs[i]);
        }
        Assert.assertEquals(0, pool.getNumActive(key1));
        Assert.assertEquals(0, pool.getNumActive(key2));
    }
}
