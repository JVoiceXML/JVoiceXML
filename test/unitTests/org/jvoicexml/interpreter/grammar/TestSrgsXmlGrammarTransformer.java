/*
 * File:    $RCSfile: TestSrgsXmlGrammarTransformer.java,v $
 * Version: $Revision: 1.9 $
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
package org.jvoicexml.interpreter.grammar;

import javax.speech.Central;
import javax.speech.EngineModeDesc;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RuleGrammar;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.implementation.jsapi10.RecognitionEngine;
import org.jvoicexml.implementation.jsapi10.jvxml.JVoiceXmlPlatform;
import org.jvoicexml.interpreter.grammar.transformer.SrgsXmlGrammarTransformer;

/**
 * The <code>TestSrgsXmlGrammarTransformer</code> tests the
 * functionality of the corresponding class.
 *
 * @author Christoph Buente
 *
 * @version $Revision: 1.9 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestSrgsXmlGrammarTransformer
        extends TestCase {
    /**
     * Logger for this class.
     */
    private static Logger LOGGER = Logger
            .getLogger(TestSrgsXmlGrammarTransformer.class);

    /**
     * The class, which wil be tested.
     */
    private GrammarTransformer transformer;

    /**
     * The class, which will be compared to the transformed grammar
     * for equality.
     */
    private RuleGrammar grammar;

    /**
     * The Recognizer from which to get the empty rule Grammar
     */
    private Recognizer recognizer;

    /**
     * Defines the base directory to the test grammars.
     */
    private static final String BASE = "test/config/irp_vxml21/";

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        /* create a very new transformer */
        this.transformer = new SrgsXmlGrammarTransformer();
        /* get a platform */
        JVoiceXmlPlatform platform = new JVoiceXmlPlatform();
        /* get recognition engine from patform */
        RecognitionEngine engine = platform.getRecognitionEngine();
        /* register engines at the JSAPI Central */
        engine.registerEngines();
        /* Get mode descripor */
        final EngineModeDesc desc = engine.getEngineProperties();
        try {
            /* try to get recognizer from JSAPI central */
            this.recognizer = Central.createRecognizer(desc);
            if (this.recognizer != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("allocating recognizer...");
                }
                this.recognizer.allocate();
                this.grammar = this.recognizer.newRuleGrammar("testgrammar");
            }
        } catch (Exception ee) {
            LOGGER.info(ee.getMessage());
            fail();
        }

    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() {
        try {
            this.recognizer.deallocate();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        this.recognizer = null;
        this.transformer = null;
        this.grammar = null;
    }

    public final void testEmptyRuleGrammar() {
        assertNotNull(this.grammar);
    }

}
