/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.grammar;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedElementError;
import org.jvoicexml.interpreter.GrammarRegistry;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The <code>GrammarProcessorTest</code> does ...
 *
 * @author Christoph Buente
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestGrammarProcessor
        extends TestCase {
    /**
     * the processors doing the job.
     */
    private JVoiceXmlGrammarProcessor processor =
        new JVoiceXmlGrammarProcessor();

    /**
     * The GrammarRegistry to use.
     */
    private GrammarRegistry registry;

    /**
     * Try to process a SRGS XML grammar.
     */
    public void testSrgsXmlGrammarTest() {
        VoiceXmlDocument srgsDocument = null;
        try {
            srgsDocument = new VoiceXmlDocument(new InputSource(
                    new StringReader("<grammar/>")));
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        final Grammar srgsxmlgrammar = (Grammar) srgsDocument.getFirstChild();
        srgsxmlgrammar.setAttribute(
                Grammar.ATTRIBUTE_TYPE,
                "application/srgs+xml");
        srgsxmlgrammar.setAttribute(Grammar.ATTRIBUTE_VERSION, "1.0");
        srgsxmlgrammar.setAttribute(Grammar.ATTRIBUTE_ROOT, "city");

        final Rule rule = srgsxmlgrammar.appendChild(Rule.class);
        rule.setAttribute(Rule.ATTRIBUTE_ID, "city");
        rule.setAttribute(Rule.ATTRIBUTE_SCOPE, "public");

        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("Boston");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("Philadelphia");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("Fargo");

        try {
            processor.process(null, srgsxmlgrammar, registry);
        } catch (BadFetchError e) {
            fail(e.getMessage());
        } catch (UnsupportedElementError e) {
            fail(e.getMessage());
        } catch (NoresourceError e) {
            fail(e.getMessage());
        }
    }

    /**
     * Try to process a SRGS ABNF grammar.
     */
    public void testSrgsAbnfGrammarTest() {
        /* ABNF Grammar */
        VoiceXmlDocument abnfDocument = null;
        try {
            abnfDocument = new VoiceXmlDocument(new InputSource(
                    new StringReader("<grammar/>")));
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        } catch (SAXException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        final Grammar srgsabnfgrammar = (Grammar) abnfDocument.getFirstChild();
        srgsabnfgrammar.setAttribute("type", "application/srgs");
        srgsabnfgrammar.addText("#ABNF 1.0 ISO-8859-1;");
        srgsabnfgrammar.addText("language en-AU;");
        srgsabnfgrammar.addText("root $city;");
        srgsabnfgrammar.addText("mode voice;");
        srgsabnfgrammar
                .addText("public $city = Boston | Philadelphia | Fargo;");

        try {
            processor.process(null, srgsabnfgrammar, registry);
        } catch (BadFetchError e) {
            fail(e.getMessage());
        } catch (UnsupportedElementError e) {
            fail(e.getMessage());
        } catch (NoresourceError e) {
            fail(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        // make sure, the grammar registry is new
        registry = new JVoiceXmlGrammarRegistry();

    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
