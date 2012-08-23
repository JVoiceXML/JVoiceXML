/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link ActiveGrammarSet}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class TestActiveGrammarSet {
    /** The scope observer to use. */
    private ScopeObserver observer;

    /** A grammar document to test with. */
    private GrammarDocument document1;

    /** Another grammar document to test with. */
    private GrammarDocument document2;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         if the set up fails.
     */
    @Before
    public void setUp() throws Exception {
        observer = new ScopeObserver();
        final SrgsXmlDocument doc1 = new SrgsXmlDocument();
        final Grammar grammar1 = doc1.getGrammar();
        grammar1.setType(GrammarType.SRGS_XML);
        grammar1.setAttribute(Grammar.ATTRIBUTE_VERSION, "1.0");
        grammar1.setAttribute(Grammar.ATTRIBUTE_ROOT, "city");

        final Rule rule1 = grammar1.appendChild(Rule.class);
        rule1.setAttribute(Rule.ATTRIBUTE_ID, "city");
        rule1.setAttribute(Rule.ATTRIBUTE_SCOPE, "public");

        final OneOf oneof = rule1.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("Boston");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("Philadelphia");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("Fargo");

        document1 = new JVoiceXmlGrammarDocument(null, grammar1);

        final SrgsXmlDocument doc2 = new SrgsXmlDocument();
        final Grammar grammar2 = doc2.getGrammar();
        grammar2.setAttribute(Grammar.ATTRIBUTE_VERSION, "1.0");
        grammar2.setAttribute(Grammar.ATTRIBUTE_ROOT, "test");

        final Rule rule2 = grammar2.appendChild(Rule.class);
        rule2.addText("test input");

        document2 = new JVoiceXmlGrammarDocument(null, grammar2);
    }

    /**
     * Tear down the test environment.
     * @throws java.lang.Exception
     *         if the tear down fails.
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ActiveGrammarSet#getImplementations()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetGrammars() throws Exception {
        final ActiveGrammarSet set = new ActiveGrammarSet(observer);
        set.add(document1);
        set.add(document2);
        final Collection<GrammarDocument> docs = set.getGrammars();
        Assert.assertEquals(2, docs.size());
    }

    /**
     * Test method for {@link ActiveGrammarSet#add(GrammarDocument)}.
     * @throws Exception test failed
     * @since 0.7.5
     */
    @Test
    public void testAdd() throws Exception {
        final ActiveGrammarSet set = new ActiveGrammarSet(observer);
        set.add(document1);
        set.add(document2);
        set.add(document1);
        final SrgsXmlDocument doc3 = new SrgsXmlDocument();
        final Grammar grammar3 = doc3.getGrammar();
        grammar3.setAttribute(Grammar.ATTRIBUTE_VERSION, "1.0");
        grammar3.setAttribute(Grammar.ATTRIBUTE_ROOT, "test");
        final Rule rule3 = grammar3.appendChild(Rule.class);
        rule3.addText("test input");
        final JVoiceXmlGrammarDocument document3 =
                new JVoiceXmlGrammarDocument(null, grammar3);
        set.add(document3);
        final byte[] buffer = grammar3.toString().getBytes();
        final String encoding = System.getProperty("jvoicexml.xml.encoding",
                "UTF-8");
        final JVoiceXmlGrammarDocument document4 =
                new JVoiceXmlGrammarDocument(null, buffer, encoding, true);
        set.add(document4);
        final Collection<GrammarDocument> docs = set.getGrammars();
        Assert.assertEquals(2, docs.size());
    }

    /**
     * Test method for {@link ActiveGrammarSet#add(GrammarDocument)}.
     * @throws Exception test failed
     * @throws JVoiceXMLEvent test failed
     * @since 0.7.5
     */
    @Test
    public void testAddScopeChange() throws Exception, JVoiceXMLEvent {
        final ActiveGrammarSet set = new ActiveGrammarSet(observer);
        final ActiveGrammarSetObserver grammarObserver
            = new ActiveGrammarSetObserver() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void removedGrammars(final ActiveGrammarSet set,
                    final Collection<GrammarDocument> removed) {
                Assert.assertEquals(2, removed.size());
            }
        };
        set.addActiveGrammarSetObserver(grammarObserver);
        observer.enterScope(Scope.APPLICATION);
        Assert.assertEquals(0, set.size());
        observer.enterScope(Scope.DIALOG);
        set.add(document1);
        Assert.assertEquals(1, set.size());
        set.add(document2);
        Assert.assertEquals(2, set.size());
        observer.exitScope(Scope.DIALOG);
        Assert.assertEquals(0, set.size());
    }
}
