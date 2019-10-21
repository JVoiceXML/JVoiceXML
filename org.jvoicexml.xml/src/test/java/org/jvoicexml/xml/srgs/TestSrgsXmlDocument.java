/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * The <code>TestSrgsXmlDocument</code> tests the integrity of the specified
 * class.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 */
public final class TestSrgsXmlDocument {
    /**
     * Test constructor for 'SrgsXmlDocument'.
     *
     * @see SrgsXmlDocument()
     * @exception Exception
     *                test failed
     */
    @Test
    public void testEmptyConstructor() throws Exception {
        SrgsXmlDocument document = new SrgsXmlDocument();
        Assert.assertNotNull(document);
    }

    /**
     * Test constructor for 'SrgsXmlDocument'.
     *
     * @see SrgsXmlDocument(InputSource)
     * @exception Exception
     *                test failed
     */
    @Test
    public void testInputConstructor() throws Exception {
        final InputStream in = TestSrgsXmlDocument.class
                .getResourceAsStream("/places.grxml");
        final InputSource source = new InputSource(in);
        final SrgsXmlDocument document = new SrgsXmlDocument(source);
        Assert.assertNotNull(document);
    }
}
