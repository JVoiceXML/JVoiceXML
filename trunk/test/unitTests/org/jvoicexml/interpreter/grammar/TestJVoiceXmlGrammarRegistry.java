/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.grammar;

import junit.framework.TestCase;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.interpreter.GrammarRegistry;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test case for {@link JVoiceXmlGrammarRegistry}.
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class TestJVoiceXmlGrammarRegistry extends TestCase {

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarRegistry#addGrammar(org.jvoicexml.GrammarDocument, org.jvoicexml.GrammarImplementation)}.
     * @exception Exception
     *            Test failed.
     */
    public void testAddGrammar() throws Exception {
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
        final GrammarImplementation implementation =
            new SrgsXmlGrammarImplementation(doc);

        final GrammarRegistry registry = new JVoiceXmlGrammarRegistry();
        registry.addGrammar(document, implementation);
        assertTrue(registry.contains(document));
    }

}
