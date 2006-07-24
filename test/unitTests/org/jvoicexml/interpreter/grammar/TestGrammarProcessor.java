package org.jvoicexml.interpreter.grammar;

/*
 * File:    $RCSfile: TestGrammarProcessor.java,v $
 * Version: $Revision: 1.5 $
 * Date:    $Date: 2006/03/29 11:40:42 $
 * Author:  $Author: buente $
 * State:   $State: Exp $
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

import java.io.StringReader;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.JVoiceXmlSession;
import org.jvoicexml.application.JVoiceXmlApplication;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedElementError;
import org.jvoicexml.interpreter.GrammarRegistry;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarProcessor;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarRegistry;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * The <code>GrammarProcessorTest</code> does ...
 *
 * @author Christoph Buente
 *
 * @version $Revision: 1.5 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestGrammarProcessor
        extends TestCase {

    private static Logger LOGGER = Logger.getLogger(TestGrammarProcessor.class);

    /**
     * the processors. who got to do the job.
     */
    private final JVoiceXmlGrammarProcessor processor = new JVoiceXmlGrammarProcessor();

    /** The VoiceXML interpreter context to use. */
    private VoiceXmlInterpreterContext context;

    /**
     * The GrammarRegistry to use
     */
    private GrammarRegistry gregistry;

    /**
     * An ABNF grammar to process.
     */
    private Grammar srgsabnfgrammar;

    /**
     * A Srgs XML grammar to process.
     */
    private Grammar srgsxmlgrammar;

    /**
     * Try to process a srgs xml grammar.
     */
    public void testSrgsXmlGrammarTest() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Testing SRGS XML processing");
            }
            processor.process(context, srgsxmlgrammar, gregistry);
        } catch (BadFetchError e) {
            fail();
        } catch (UnsupportedElementError e) {
            fail();
        } catch (NoresourceError e) {
            fail();
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testSrgsAbnfGrammarTest() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Testing SRGS ABNF processing");
            }
            processor.process(context, srgsabnfgrammar, gregistry);
        } catch (BadFetchError e) {
            fail();
        } catch (UnsupportedElementError e) {
            fail();
        } catch (NoresourceError e) {
            fail();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {

        // Make sure, the context is new
        final JVoiceXmlMain jvxml = JVoiceXmlMain.getInstance();
        LOGGER.info("jvxml is: "+jvxml);
        final Application dummy = new JVoiceXmlApplication("dummy", null);
        LOGGER.info("dummy is: "+dummy);
        final ApplicationRegistry registry = jvxml.getApplicationRegistry();
        LOGGER.info("registrry is "+ registry);
        registry.register(dummy);

        JVoiceXmlSession session;

        try {
            session = (JVoiceXmlSession) jvxml
                                             .createSession(null, "dummy");
        } catch (org.jvoicexml.event.error.ErrorEvent ee) {
            session = null;
            fail(ee.getMessage());
        }

        context = new VoiceXmlInterpreterContext(session);

        // make sure, the grammar registry is new
        gregistry = new JVoiceXmlGrammarRegistry(context);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting up grammars");
        }

        /* ABNF Grammar */
        VoiceXmlDocument abnfDocument = new VoiceXmlDocument(new InputSource(
                new StringReader("<grammar/>")));
        srgsabnfgrammar = (Grammar) abnfDocument.getFirstChild();
        srgsabnfgrammar.setAttribute("type", "application/srgs");
        srgsabnfgrammar.addText("#ABNF 1.0 ISO-8859-1;");
        srgsabnfgrammar.addText("language en-AU;");
        srgsabnfgrammar.addText("root $city;");
        srgsabnfgrammar.addText("mode voice;");
        srgsabnfgrammar
                .addText("public $city = Boston | Philadelphia | Fargo;");

        /* XML Grammar with the same semantic meaning */

        VoiceXmlDocument srgsDocument = new VoiceXmlDocument(new InputSource(
                new StringReader("<grammar/>")));
        srgsxmlgrammar = (Grammar) srgsDocument.getFirstChild();
        srgsxmlgrammar.setAttribute(
                Grammar.ATTRIBUTE_TYPE,
                "application/srgs+xml");
        srgsxmlgrammar.setAttribute(Grammar.ATTRIBUTE_VERSION, "1.0");
        srgsxmlgrammar.setAttribute(Grammar.ATTRIBUTE_ROOT, "city");

        final Rule rule = srgsxmlgrammar.addChild(Rule.class);
        rule.setAttribute(Rule.ATTRIBUTE_ID, "city");
        rule.setAttribute(Rule.ATTRIBUTE_SCOPE, "public");

        final OneOf oneof = rule.addChild(OneOf.class);
        final Item item1 = oneof.addChild(Item.class);
        item1.addText("Boston");
        final Item item2 = oneof.addChild(Item.class);
        item2.addText("Philadelphia");
        final Item item3 = oneof.addChild(Item.class);
        item3.addText("Fargo");
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
