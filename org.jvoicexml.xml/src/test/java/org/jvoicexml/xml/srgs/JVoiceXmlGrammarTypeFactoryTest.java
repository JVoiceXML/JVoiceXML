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
package org.jvoicexml.xml.srgs;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link JVoiceXmlGrammarTypeFactory}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JVoiceXmlGrammarTypeFactoryTest {

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.JVoiceXmlGrammarTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarType() {
        final GrammarTypeFactory factory = new JVoiceXmlGrammarTypeFactory();
        final GrammarType typeSrgsXml = factory
                .getGrammarType(GrammarType.SRGS_XML.toString());
        Assert.assertTrue(
                GrammarType.SRGS_XML.getType().match(typeSrgsXml.getType()));
        final GrammarType typeSrgsAbnf = factory
                .getGrammarType(GrammarType.SRGS_ABNF.toString());
        Assert.assertTrue(
                GrammarType.SRGS_ABNF.getType().match(typeSrgsAbnf.getType()));
        final GrammarType typeJsgf = factory
                .getGrammarType(GrammarType.JSGF.toString());
        Assert.assertTrue(
                GrammarType.JSGF.getType().match(typeJsgf.getType()));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.JVoiceXmlGrammarTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarTypeNull() {
        final GrammarTypeFactory factory = new JVoiceXmlGrammarTypeFactory();
        final GrammarType type = factory.getGrammarType(null);
        Assert.assertNull(type);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.JVoiceXmlGrammarTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarTypeOtherType() {
        final GrammarTypeFactory factory = new JVoiceXmlGrammarTypeFactory();
        final GrammarType type = factory.getGrammarType("application/dummy");
        Assert.assertNull(type);
    }
}
