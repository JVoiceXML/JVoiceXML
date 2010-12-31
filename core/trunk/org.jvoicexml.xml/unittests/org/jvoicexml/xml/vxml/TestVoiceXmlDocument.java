/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.DocumentType;

/**
 * Test cases for {@link VoiceXmlDocument}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestVoiceXmlDocument {

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.VoiceXmlDocument#VoiceXmlDocument()}.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testVoiceXmlDocument() throws Exception {
        final VoiceXmlDocument doc1 = new VoiceXmlDocument();
        Assert.assertNotNull(doc1.getVxml());
        Assert.assertNull(doc1.getDoctype());

        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.0");
        final VoiceXmlDocument doc2 = new VoiceXmlDocument();
        Assert.assertNotNull(doc2.getVxml());
        final DocumentType type2 = new VoiceXml20DocumentType();
        Assert.assertEquals(type2.toString(), doc2.getDoctype().toString());

        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.1");
        final VoiceXmlDocument doc3 = new VoiceXmlDocument();
        Assert.assertNotNull(doc3.getVxml());
        final DocumentType type3 = new VoiceXml21DocumentType();
        Assert.assertEquals(type3.toString(), doc3.getDoctype().toString());

        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.2");
        IllegalArgumentException error = null;
        try {
            new VoiceXmlDocument();
        } catch (IllegalArgumentException e) {
            error = e;
        }

        Assert.assertNull("2.2 is an unsupported version type", error);
    }

    /**
     * Test method for {@link VoiceXmlDocument#toXml().
     * @throws Exception
     *         Test failed.
     */
    @Test
    public void testToXml() throws Exception {
        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.1");
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final String xml1 = doc.toXml();
        Assert.assertTrue("missing xml prefix", xml1.startsWith("<?xml"));
        System.setProperty("jvoicexml.xml.encoding", "ISO-8859-1");
        final String xml2 = doc.toXml();
        Assert.assertTrue("missing xml prefix", xml2.startsWith("<?xml"));
    }

    /**
     * Test case for the serialization of an XML document.
     * @throws Exception
     *         test failed.
     * @since 0.7.3
     */
    @Test
    public void testSerialize() throws Exception {
        System.setProperty("jvoicexml.xml.encoding", "ISO-8859-1");
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        block.setName("test");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(doc);
        final ByteArrayInputStream in =
            new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream oin = new ObjectInputStream(in);
        final Object o = oin.readObject();
        Assert.assertEquals(doc.toString(), o.toString());
    }

    /**
     * Test case for the serialization of an XML document.
     * @throws Exception
     *         test failed.
     * @since 0.7.3
     */
    @Test
    public void testSerializeLargeDocument() throws Exception {
        System.setProperty("jvoicexml.xml.encoding", "ISO-8859-1");
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        for (int i=0; i<10000; i++) {
            final Form form = vxml.appendChild(Form.class);
            form.setId("form " + i);
            final Block block = form.appendChild(Block.class);
            block.setName("block " + i);
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new ObjectOutputStream(out);
        oout.writeObject(doc);
        final ByteArrayInputStream in =
            new ByteArrayInputStream(out.toByteArray());
        final ObjectInputStream oin = new ObjectInputStream(in);
        final Object o = oin.readObject();
        Assert.assertEquals(doc.toString(), o.toString());
    }
}

