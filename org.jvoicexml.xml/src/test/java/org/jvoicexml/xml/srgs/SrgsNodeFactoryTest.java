/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.xml.SrgsNode;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link SrgsNodeFactory}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class SrgsNodeFactoryTest {

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.SrgsNodeFactory#getXmlNode(org.w3c.dom.Node)}
     * .
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testGetXmlNode() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        rule.addText("this is a test");
        final SrgsNodeFactory factory = new SrgsNodeFactory();
        final SrgsNode node = factory.getXmlNode(rule);
        Assert.assertNotNull(node);
        Assert.assertEquals(Rule.TAG_NAME, node.getTagName());
    }

    @Test
    public void testGetXmlNodeNamespaces() throws Exception {
        final InputStream input = SrgsNodeFactory.class
                .getResourceAsStream("/srgs-namespace-test.srgs");
        final InputSource source = new InputSource(input);
        final SrgsXmlDocument document = new SrgsXmlDocument(source);
        final Grammar grammar = document.getGrammar();
        Assert.assertNotNull("node must not be null", grammar);
    }
}
