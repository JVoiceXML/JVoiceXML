/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.xml.vxml;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link JVoiceXmlDataTypeFactory}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JVoiceXmlDataTypeFactoryTest {
    /**
     * Test method for
     * {@link org.jvoicexml.xml.vxml.JVoiceXmlDataTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarType() {
        final DataTypeFactory factory = new JVoiceXmlDataTypeFactory();
        final DataType typeXml = factory
                .getDataType(DataType.XML.toString());
        Assert.assertTrue(DataType.XML.getType().match(typeXml.getType()));
        final DataType typeJson = factory
                .getDataType(DataType.JSON.toString());
        Assert.assertTrue(DataType.JSON.getType().match(typeJson.getType()));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.vxml.JVoiceXmlDataTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarTypeNull() {
        final DataTypeFactory factory = new JVoiceXmlDataTypeFactory();
        final DataType type = factory.getDataType(null);
        Assert.assertNull(type);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.vxml.JVoiceXmlDataTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarTypeOtherType() {
        final DataTypeFactory factory = new JVoiceXmlDataTypeFactory();
        final DataType type = factory.getDataType("application/dummy");
        Assert.assertNull(type);
    }
}
