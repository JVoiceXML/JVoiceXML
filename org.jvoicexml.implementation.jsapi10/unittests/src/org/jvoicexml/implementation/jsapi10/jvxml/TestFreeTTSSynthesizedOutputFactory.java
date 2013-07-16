/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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
package org.jvoicexml.implementation.jsapi10.jvxml;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.jsapi10.JVoiceXmlSynthesizerModeDescFactory;
import org.jvoicexml.implementation.jsapi10.Jsapi10SynthesizedOutputFactory;
import org.jvoicexml.implementation.jsapi10.SynthesizerModeDescFactory;

/**
 * Test cases for {@link FreeTTSSynthesizedOutputFactory}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestFreeTTSSynthesizedOutputFactory {
    /**
     * Test method for
     * {@link org.jvoicexml.implementation.jsapi10.jvxml.FreeTTSSynthesizedOutputFactory#createResource()}.
     *
     * @exception Exception
     *                Test failed.
     * @exception NoresourceError
     *                Test failed.
     */
    @Test
    public void testCreateResource() throws Exception, NoresourceError {
        final Jsapi10SynthesizedOutputFactory factory1 =
            new FreeTTSSynthesizedOutputFactory();
        final String type = "jsapi1.0";
        factory1.setType(type);

        final SynthesizedOutput output1 = factory1.createResource();
        Assert.assertNotNull(output1);
        Assert.assertEquals(type, output1.getType());

        final Jsapi10SynthesizedOutputFactory factory2 =
            new FreeTTSSynthesizedOutputFactory();
        factory2.setType(type);

        final SynthesizerModeDescFactory descriptorFactory =
            new JVoiceXmlSynthesizerModeDescFactory();
        factory2.setSynthesizerModeDescriptorFactory(descriptorFactory);

        final SynthesizedOutput output2 = factory2.createResource();
        Assert.assertNotNull(output2);
        Assert.assertEquals(type, output2.getType());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.jsapi10.jvxml.FreeTTSSynthesizedOutputFactory#setInstances(int)}.
     */
    @Test
    public void testSetInstances() {
        final Jsapi10SynthesizedOutputFactory factory =
            new FreeTTSSynthesizedOutputFactory();
        final int instances = 42;
        factory.setInstances(instances);

        Assert.assertEquals(instances, factory.getInstances());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.implementation.jsapi10.jvxml.FreeTTSSynthesizedOutputFactory#setType(java.lang.String)}.
     */
    @Test
    public void testSetType() {
        final Jsapi10SynthesizedOutputFactory factory =
            new FreeTTSSynthesizedOutputFactory();
        final String type = "jsapi1.0";
        factory.setType(type);

        Assert.assertEquals(type, factory.getType());
    }
}
