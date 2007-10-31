/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.implementation.jsapi10.jvxml;

import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.jsapi10.SynthesizerModeDescFactory;

import junit.framework.TestCase;

/**
 *  Test cases for {@link SynthesizedOutputFactory}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestSynthesizedOutputFactory
        extends TestCase {
    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.jvxml.SynthesizedOutputFactory#createResource()}.
     * @exception Exception
     *            Test failed.
     * @exception NoresourceError
     *            Test failed.
     */
    public void testCreateResource() throws Exception, NoresourceError {
        final SynthesizedOutputFactory factory1 =
            new SynthesizedOutputFactory();
        final String type = "jsapi1.0";
        factory1.setType(type);

        final SynthesizedOuput output1 = factory1.createResource();
        assertNotNull(output1);
        assertEquals(type, output1.getType());

        final SynthesizedOutputFactory factory2 =
            new SynthesizedOutputFactory();
        factory2.setType(type);

        final SynthesizerModeDescFactory descriptorFactory =
            new SynthesizerModeDescFactory();
        factory2.setSynthesizerModeDescriptor(descriptorFactory);

        final SynthesizedOuput output2 = factory2.createResource();
        assertNotNull(output2);
        assertEquals(type, output2.getType());
}

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.jvxml.SynthesizedOutputFactory#setInstances(int)}.
     */
    public void testSetInstances() {
        final SynthesizedOutputFactory factory = new SynthesizedOutputFactory();
        final int instances = 42;
        factory.setInstances(instances);

        assertEquals(instances, factory.getInstances());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.jvxml.SynthesizedOutputFactory#setType(java.lang.String)}.
     */
    public void testSetType() {
        final SynthesizedOutputFactory factory = new SynthesizedOutputFactory();
        final String type = "jsapi1.0";
        factory.setType(type);

        assertEquals(type, factory.getType());
    }
}
