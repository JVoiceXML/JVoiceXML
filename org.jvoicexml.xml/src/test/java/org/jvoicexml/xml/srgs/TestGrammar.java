/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.xml.IllegalAttributeException;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link Grammar}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class TestGrammar {
    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Grammar#getRootRule()}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetRootRule() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.makePublic();
        rule.setId("test");
        final Rule rootRule = grammar.getRootRule();
        Assert.assertEquals(rule, rootRule);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Grammar#getRootRule()}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetRootRule2() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule = grammar.appendChild(Rule.class);
        rule.makePublic();
        rule.setId("test");
        grammar.setRoot(rule);
        final Rule rootRule = grammar.getRootRule();
        Assert.assertEquals(rule, rootRule);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Grammar#getRootRule()}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetRootRuleExternal() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/test.grxml");
        final InputSource source = new InputSource(in);
        SrgsXmlDocument document = new SrgsXmlDocument(source);
        Grammar grammar = document.getGrammar();
        Rule rootRule = grammar.getRootRule();
        Assert.assertNotNull(rootRule);
        Assert.assertEquals("main", rootRule.getId());
    }

    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Grammar#getRule(String))}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetRule() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test1");
        final Rule rule1 = grammar.appendChild(Rule.class);
        rule1.makePublic();
        rule1.setId("test1");
        final Rule rule2 = grammar.appendChild(Rule.class);
        rule2.setId("test2");

        final Rule rule1Node = grammar.getRule("test1");
        Assert.assertEquals(rule1, rule1Node);
        final Rule rule2Node = grammar.getRule("test2");
        Assert.assertEquals(rule2, rule2Node);
    }

    /**
     * Test method for {@link Grammar#getRules()}.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testGetRules() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule1 = grammar.appendChild(Rule.class);
        grammar.setRoot(rule1);
        rule1.makePublic();
        rule1.setId("test1");
        final Rule rule2 = grammar.appendChild(Rule.class);
        rule2.setId("test2");
        final Rule rule3 = grammar.appendChild(Rule.class);
        rule3.setId("test3");
        rule3.makePublic();
        final Collection<Rule> rules = grammar.getRules();
        Assert.assertEquals(3, rules.size());
    }

    /**
     * Test method for {@link Grammar#getRules()}.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testGetRulesEmpty() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Collection<Rule> rules = grammar.getRules();
        Assert.assertEquals(0, rules.size());
    }

    /**
     * Test method for {@link Grammar#getPublicRules()}.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testGetPublicRules() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule1 = grammar.appendChild(Rule.class);
        grammar.setRoot(rule1);
        rule1.makePublic();
        rule1.setId("test1");
        final Rule rule2 = grammar.appendChild(Rule.class);
        rule2.setId("test2");
        final Rule rule3 = grammar.appendChild(Rule.class);
        rule3.setId("test3");
        rule3.makePublic();
        final Collection<Rule> rules = grammar.getPublicRules();
        Assert.assertEquals(2, rules.size());
    }

    /**
     * Test method for {@link Grammar#getPublicRules()}.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testGetPublicRulesEmpty() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Collection<Rule> rules = grammar.getPublicRules();
        Assert.assertEquals(0, rules.size());
    }

    /**
     * Test method for {@link Grammar#getPublicRules()}.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testGetPublicRulesOnlyPrivate() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule1 = grammar.appendChild(Rule.class);
        rule1.setId("test1");
        final Rule rule2 = grammar.appendChild(Rule.class);
        rule2.setId("test2");
        final Rule rule3 = grammar.appendChild(Rule.class);
        rule3.setId("test3");
        final Collection<Rule> rules = grammar.getPublicRules();
        Assert.assertEquals(0, rules.size());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.Grammar#setType(GrammarType))}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testSetType() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setType(GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.SRGS_XML, grammar.getType());
        Assert.assertEquals(GrammarType.SRGS_XML.toString(),
                grammar.getTypename());
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.Grammar#Grammar(org.w3c.dom.Node)}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testNamespaceSrgsDocument() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        Assert.assertEquals("http://www.w3.org/2001/06/grammar",
                grammar.getAttribute("xmlns"));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.xml.srgs.Grammar#Grammar(org.w3c.dom.Node)}.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testNamespaceVoiceXmlDocument() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Grammar grammar = field.appendChild(Grammar.class);
        Assert.assertNull(grammar.getAttribute("xmlns"));
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    @Test(expected = IllegalAttributeException.class)
    public void testIsExternalInvalid1() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/invalid1.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;
            /* now test isExternalMethod */
            gr.isExternalGrammar();
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    @Test(expected = IllegalAttributeException.class)
    public void testIsExternalInvalid2() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/invalid2.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;

            /* now test isExternalMethod */
            gr.isExternalGrammar();
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    @Test(expected = IllegalAttributeException.class)
    public void testIsExternalInvalid3() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/invalid3.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;

            /* now test isExternalMethod */
            gr.isExternalGrammar();
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    @Test(expected = IllegalAttributeException.class)
    public void testIsExternalInvalid4() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/invalid4.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;

            /* now test isExternalMethod */
            gr.isExternalGrammar();
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testIsExternalValid1() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/valid1.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;

            /* now test isExternalMethod */
            Assert.assertEquals(true, gr.isExternalGrammar());
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    public void testIsExternalValid2() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/valid2.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;

            /* now test isExternalMethod */
            Assert.assertEquals(true, gr.isExternalGrammar());
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     * 
     * @exception Exception
     *                test failed
     */
    @Test
    public void testIsExternalvalid3() throws Exception {
        final InputStream in = TestGrammar.class
                .getResourceAsStream("/valid3.grxml");
        final InputSource source = new InputSource(in);
        final VoiceXmlDocument document = new VoiceXmlDocument(source);
        /* Lets test, if it is srgs+xml */
        final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
        if (node instanceof Grammar) {
            /* ok, it seems to be a srgs xml grammar */
            Grammar gr = (Grammar) node;

            /* now test isExternalMethod */
            Assert.assertEquals(false, gr.isExternalGrammar());
        }
    }
}
