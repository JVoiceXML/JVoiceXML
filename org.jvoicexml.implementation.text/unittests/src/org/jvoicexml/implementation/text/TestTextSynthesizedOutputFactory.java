/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.text;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Test cases for {@link TextSynthesizedOutputFactory}.
 * @author Dirk Schnelle
 *
 */
public final class TestTextSynthesizedOutputFactory {
    /** The number of instances that this factory can create. */
    private static final int INSTANCES = 500;

    /** The object to test. */
    private TextSynthesizedOutputFactory factory;

    /**
     * Set up the test environment.
     * @exception Exception set up failed
     */
    @Before
    public void setUp() throws Exception {
        factory = new TextSynthesizedOutputFactory();
        factory.setInstances(INSTANCES);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextSynthesizedOutputFactory#createResource()}.
     * @throws Exception
     *         Test failed.
     * @throws NoresourceError
     *         Test failed.
     */
    @Test
    public void testCreateResource() throws Exception, NoresourceError {
        for (int i = 0; i < INSTANCES; i++) {
            SynthesizedOutput output = factory.createResource();
            Assert.assertEquals("text", output.getType());
        }
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextSynthesizedOutputFactory#getInstances()}.
     */
    public void testGetInstances() {
        Assert.assertEquals(INSTANCES, factory.getInstances());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextSynthesizedOutputFactory#getType()}.
     */
    public void testGetType() {
        Assert.assertEquals("text", factory.getType());
    }

}
