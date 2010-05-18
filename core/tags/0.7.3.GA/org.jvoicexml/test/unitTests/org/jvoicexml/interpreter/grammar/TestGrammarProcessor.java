/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.ProcessedGrammar;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.grammar.identifier.SrgsAbnfGrammarIdentifier;
import org.jvoicexml.interpreter.grammar.identifier.SrgsXmlGrammarIdentifier;
import org.jvoicexml.interpreter.grammar.transformer.SrgsXml2SrgsAbnfGrammarTransformer;
import org.jvoicexml.interpreter.grammar.transformer.SrgsXml2SrgsXmlGrammarTransformer;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
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
 * The <code>TestGrammarProcessor</code> provides tests for the
 * {@link org.jvoicexml.interpreter.GrammarProcessor}.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision$
 */
public final class TestGrammarProcessor {
    /**
     * the processors doing the job.
     */
    private JVoiceXmlGrammarProcessor processor;

    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /**
     * Checks if the given grammar type is supported by the implementation
     * platform.
     * @param type the type to check.
     * @return <code>true</code> if the grammar type is supported by the
     *         implementation platform.
     * @throws JVoiceXMLEvent
     *         Error accessing the user input.
     */
    private boolean isSupportedGrammarType(final GrammarType type)
        throws JVoiceXMLEvent {
        final ImplementationPlatform platform =
            context.getImplementationPlatform();
        final UserInput input = platform.getUserInput();
        final Collection<GrammarType> supportedTypes =
            input.getSupportedGrammarTypes(ModeType.VOICE);
        for (GrammarType currentType : supportedTypes) {
            if (currentType == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Try to process a SRGS XML grammar.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testSrgsXmlGrammarTest() throws JVoiceXMLEvent, Exception {
        final SrgsXmlDocument srgsDocument = new SrgsXmlDocument();
        final Grammar srgsxmlgrammar = srgsDocument.getGrammar();
        srgsxmlgrammar.setType(GrammarType.SRGS_XML);
        srgsxmlgrammar.setRoot("city");

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

        final ProcessedGrammar processed =
            processor.process(context, null, srgsxmlgrammar);

        final GrammarImplementation<?> grammar = processed.getImplementation();
        final GrammarType type = grammar.getMediaType();
        Assert.assertTrue(type + " is not a supported grammar type",
                isSupportedGrammarType(type));
    }

    /**
     * Try to process a SRGS ABNF grammar.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testSrgsAbnfGrammarTest() throws Exception, JVoiceXMLEvent {
        /* ABNF Grammar */
        final VoiceXmlDocument abnfDocument = new VoiceXmlDocument();
        final Vxml vxml = abnfDocument.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar srgsabnfgrammar = form.appendChild(Grammar.class);
        srgsabnfgrammar.setType(GrammarType.SRGS_ABNF);
        srgsabnfgrammar.addText("#ABNF 1.0 ISO-8859-1;");
        srgsabnfgrammar.addText("language en-AU;");
        srgsabnfgrammar.addText("root $city;");
        srgsabnfgrammar.addText("mode voice;");
        srgsabnfgrammar
                .addText("public $city = Boston | Philadelphia | Fargo;");

        UnsupportedFormatError error = null;
        try {
            processor.process(context, null, srgsabnfgrammar);
        } catch (UnsupportedFormatError e) {
            error = e;
        }

        Assert.assertNotNull("SRGS ABNF conversion should be unsupported",
                error);
    }

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        processor = new JVoiceXmlGrammarProcessor();

        final GrammarIdentifierCentral identifier =
            new GrammarIdentifierCentral();
        identifier.addIdentifier(new SrgsXmlGrammarIdentifier());
        identifier.addIdentifier(new SrgsAbnfGrammarIdentifier());

        processor.setGrammaridentifier(identifier);

        final GrammarTransformerCentral transformer =
            new GrammarTransformerCentral();
        transformer.addTransformer(new SrgsXml2SrgsAbnfGrammarTransformer());
        transformer.addTransformer(new SrgsXml2SrgsXmlGrammarTransformer());

        processor.setGrammartransformer(transformer);

        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        context = new VoiceXmlInterpreterContext(session);
    }
}
