/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/unittests/org/jvoicexml/implementation/text/TestTextSynthesizedOutputFactory.java $
 * Version: $LastChangedRevision: 854 $
 * Date:    $LastChangedDate: 2008-04-29 04:39:39 -0400 (Di, 29 Apr 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Test cases for {@link TextSynthesizedOutputFactory}.
 * @author Dirk Schnelle
 *
 */
public final class TestTextSynthesizedOutputFactory extends TestCase {
    /** The number of instances that this factory can create. */
    private static final int INSTANCES = 500;

    /** The object to test. */
    private TextSynthesizedOutputFactory factory;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
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
    public void testCreateResource() throws Exception, NoresourceError {
        for (int i = 0; i < INSTANCES; i++) {
            SynthesizedOutput output = factory.createResource();
            assertEquals("text", output.getType());
        }
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextSynthesizedOutputFactory#getInstances()}.
     */
    public void testGetInstances() {
        assertEquals(INSTANCES, factory.getInstances());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.text.TextSynthesizedOutputFactory#getType()}.
     */
    public void testGetType() {
        assertEquals("text", factory.getType());
    }

}
