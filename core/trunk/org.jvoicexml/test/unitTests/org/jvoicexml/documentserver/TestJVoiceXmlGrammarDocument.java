/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link JVoiceXmlGrammarDocument}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestJVoiceXmlGrammarDocument {

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlGrammarDocument#getDocument()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetDocument() throws Exception {
        final SrgsXmlDocument srgsDocument = new SrgsXmlDocument();
        final Grammar grammar = srgsDocument.getGrammar();
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final String encoding = System.getProperty("jvoicexml.xml.encoding",
                "UTF-8");
        final JVoiceXmlGrammarDocument document =
                new JVoiceXmlGrammarDocument(null,
                        srgsDocument.toString().getBytes(),
                        encoding, true);
        document.setModeType(ModeType.VOICE);
        document.setMediaType(GrammarType.SRGS_XML);
        Assert.assertEquals(srgsDocument.toString(), document.getDocument());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlGrammarDocument#equals(java.lang.Object)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testEqualsInternalObject() throws Exception {
        final VoiceXmlDocument doc1 = new VoiceXmlDocument();
        final Vxml vxml1 = doc1.getVxml();
        final Form form1 = vxml1.appendChild(Form.class);
        final Grammar grammar1 = form1.appendChild(Grammar.class);
        grammar1.setVersion("1.0");
        grammar1.setType(GrammarType.SRGS_XML);
        final Rule rule1 = grammar1.appendChild(Rule.class);
        final OneOf oneof1 = rule1.appendChild(OneOf.class);
        final Item item11 = oneof1.appendChild(Item.class);
        item11.addText("visa");
        final Item item12 = oneof1.appendChild(Item.class);
        item12.addText("mastercard");
        final Item item13 = oneof1.appendChild(Item.class);
        item13.addText("american express");
        final JVoiceXmlGrammarDocument document1 =
                new JVoiceXmlGrammarDocument(null,grammar1);

        final VoiceXmlDocument doc2 = new VoiceXmlDocument();
        final Vxml vxml2 = doc2.getVxml();
        final Form form2 = vxml2.appendChild(Form.class);
        final Grammar grammar2 = form2.appendChild(Grammar.class);
        grammar2.setVersion("1.0");
        grammar2.setType(GrammarType.SRGS_XML);
        final Rule rule2 = grammar2.appendChild(Rule.class);
        final OneOf oneof2 = rule2.appendChild(OneOf.class);
        final Item item21 = oneof2.appendChild(Item.class);
        item21.addText("visa");
        final Item item22 = oneof2.appendChild(Item.class);
        item22.addText("mastercard");
        final Item item23 = oneof2.appendChild(Item.class);
        item23.addText("american express");
        final JVoiceXmlGrammarDocument document2 =
                new JVoiceXmlGrammarDocument(null, grammar2);
        Assert.assertEquals(document1, document2);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlGrammarDocument#equals(java.lang.Object)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testEqualsExternalObject() throws Exception {
        final SrgsXmlDocument srgsDocument = new SrgsXmlDocument();
        final Grammar grammar = srgsDocument.getGrammar();
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final String encoding = System.getProperty("jvoicexml.xml.encoding",
                "UTF-8");
        final JVoiceXmlGrammarDocument document1 =
                new JVoiceXmlGrammarDocument(null,
                        srgsDocument.toString().getBytes(),
                        encoding, true);
        document1.setModeType(ModeType.VOICE);
        document1.setMediaType(GrammarType.SRGS_XML);

        final JVoiceXmlGrammarDocument document2 =
                new JVoiceXmlGrammarDocument(null,
                        srgsDocument.toString().getBytes(),
                        encoding, true);
        document2.setModeType(ModeType.VOICE);
        document2.setMediaType(GrammarType.SRGS_XML);
        Assert.assertEquals(document1, document2);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlGrammarDocument#hashCode()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testHashCodeInternal() throws Exception {
        final VoiceXmlDocument doc1 = new VoiceXmlDocument();
        final Vxml vxml1 = doc1.getVxml();
        final Form form1 = vxml1.appendChild(Form.class);
        final Grammar grammar1 = form1.appendChild(Grammar.class);
        grammar1.setVersion("1.0");
        grammar1.setType(GrammarType.SRGS_XML);
        final Rule rule1 = grammar1.appendChild(Rule.class);
        final OneOf oneof1 = rule1.appendChild(OneOf.class);
        final Item item11 = oneof1.appendChild(Item.class);
        item11.addText("visa");
        final Item item12 = oneof1.appendChild(Item.class);
        item12.addText("mastercard");
        final Item item13 = oneof1.appendChild(Item.class);
        item13.addText("american express");
        final JVoiceXmlGrammarDocument document1 =
                new JVoiceXmlGrammarDocument(null,grammar1);

        final VoiceXmlDocument doc2 = new VoiceXmlDocument();
        final Vxml vxml2 = doc2.getVxml();
        final Form form2 = vxml2.appendChild(Form.class);
        final Grammar grammar2 = form2.appendChild(Grammar.class);
        grammar2.setVersion("1.0");
        grammar2.setType(GrammarType.SRGS_XML);
        final Rule rule2 = grammar2.appendChild(Rule.class);
        final OneOf oneof2 = rule2.appendChild(OneOf.class);
        final Item item21 = oneof2.appendChild(Item.class);
        item21.addText("visa");
        final Item item22 = oneof2.appendChild(Item.class);
        item22.addText("mastercard");
        final Item item23 = oneof2.appendChild(Item.class);
        item23.addText("american express");
        final JVoiceXmlGrammarDocument document2 =
                new JVoiceXmlGrammarDocument(null, grammar2);
        System.out.println(document1);
        System.out.println(document2);
        Assert.assertEquals(document1.hashCode(), document2.hashCode());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlGrammarDocument#hashCode()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testHashCodeExternal() throws Exception {
        final SrgsXmlDocument srgsDocument = new SrgsXmlDocument();
        final Grammar grammar = srgsDocument.getGrammar();
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");
        final String encoding = System.getProperty("jvoicexml.xml.encoding",
                "UTF-8");
        final JVoiceXmlGrammarDocument document1 =
                new JVoiceXmlGrammarDocument(null,
                        srgsDocument.toString().getBytes(),
                        encoding, true);
        document1.setModeType(ModeType.VOICE);
        document1.setMediaType(GrammarType.SRGS_XML);

        final JVoiceXmlGrammarDocument document2 =
                new JVoiceXmlGrammarDocument(null,
                        srgsDocument.toString().getBytes(),
                        encoding, true);
        document2.setModeType(ModeType.VOICE);
        document2.setMediaType(GrammarType.SRGS_XML);
        Assert.assertEquals(document1.hashCode(), document2.hashCode());
    }
}
