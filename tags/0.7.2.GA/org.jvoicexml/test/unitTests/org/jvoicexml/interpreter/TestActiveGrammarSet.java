/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
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

    /** A processed grammar to test with. */
    private ProcessedGrammar processed;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         if the set up fails.
     */
    @Before
    public void setUp() throws Exception {
        observer = new ScopeObserver();
        final SrgsXmlDocument doc = new SrgsXmlDocument();
        final Grammar grammar = doc.getGrammar();
        grammar.setType(GrammarType.SRGS_XML);
        grammar.setAttribute(Grammar.ATTRIBUTE_VERSION, "1.0");
        grammar.setAttribute(Grammar.ATTRIBUTE_ROOT, "city");

        final Rule rule = grammar.appendChild(Rule.class);
        rule.setAttribute(Rule.ATTRIBUTE_ID, "city");
        rule.setAttribute(Rule.ATTRIBUTE_SCOPE, "public");

        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("Boston");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("Philadelphia");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("Fargo");

        final GrammarDocument document =
            new JVoiceXmlGrammarDocument(doc.toString());
        final GrammarImplementation<?> implementation =
            new SrgsXmlGrammarImplementation(doc);
        processed = new ProcessedGrammar(document, implementation);
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
    public void testGetImplementations() throws Exception {
        final ActiveGrammarSet set = new ActiveGrammarSet(observer);
        set.add(processed);
        final Collection<GrammarImplementation<?>> impls =
            set.getImplementations();
        Assert.assertEquals(1, impls.size());
        Assert.assertEquals(processed.getImplementation(),
                impls.iterator().next());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.ActiveGrammarSet#contains(GrammarImplementation)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testContains() throws Exception {
        final ActiveGrammarSet set = new ActiveGrammarSet(observer);
        Assert.assertFalse(set.contains(processed.getImplementation()));
        set.add(processed);
        Assert.assertTrue(set.contains(processed.getImplementation()));
    }
}
