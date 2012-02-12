/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.grammar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.implementation.grammar.GrammarCache;
import org.jvoicexml.implementation.grammar.ProcessedGrammar;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;


/**
 * Test cases for {@link GrammarCache}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class TestGrammarCache {
    /** A processed grammar to test with. */
    private ProcessedGrammar processed;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         if the set up fails.
     */
    @Before
    public void setUp() throws Exception {
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
            new JVoiceXmlGrammarDocument(null, grammar);
        final GrammarImplementation<?> implementation =
            new SrgsXmlGrammarImplementation(doc);
        processed = new ProcessedGrammar(document, implementation);
    }

    /**
     * Test method for {@link GrammarCache#contains(org.jvoicexml.GrammarDocument)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testContains() throws Exception {
        final GrammarCache cache = new GrammarCache();
        Assert.assertFalse(cache.contains(processed.getImplementation()));
        cache.add(processed);
        Assert.assertTrue(cache.contains(processed.getImplementation()));
    }


}
