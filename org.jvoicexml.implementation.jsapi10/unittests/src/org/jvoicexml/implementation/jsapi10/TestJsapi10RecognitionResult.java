/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jsapi10;

import java.io.StringReader;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.RuleGrammar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.sun.speech.engine.recognition.BaseResult;

import edu.cmu.sphinx.jsapi.SphinxEngineCentral;
import edu.cmu.sphinx.jsapi.SphinxRecognizerModeDesc;

/**
 * Test cases for {@link Jsapi10RecognitionResult}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public class TestJsapi10RecognitionResult {
    /** The recognizer. */
    private Recognizer recognizer;

    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     */
    @BeforeClass
    public static void init() throws EngineException {
        final String config = "/sphinx4.jsapi10.config";
        final SphinxRecognizerModeDesc desc =
                new SphinxRecognizerModeDesc(config);
        SphinxEngineCentral.registerEngineModeDesc(desc);
        Central.registerEngineCentral(SphinxEngineCentral.class.getName());
    }

    /**
     * Set up the test environment.
     * @throws Exception test failed
     * @since 0.7.3
     */
    @Before
    public void setUp() throws Exception {
        final String config = "/sphinx4.jsapi10.config";
        final RecognizerModeDesc desc = new SphinxRecognizerModeDesc(config);
        recognizer = Central.createRecognizer(desc);
        recognizer.allocate();
        recognizer.waitEngineState(Recognizer.ALLOCATED);
    }

    /**
     * Tear down the test environment.
     * @throws Exception test failed
     * @since 0.7.3
     */
    public void tearDown() throws Exception {
        if (recognizer != null) {
            recognizer.deallocate();
        }
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.Jsapi10RecognitionResult#getSemanticInterpretation()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetSemanticInterpretation() throws Exception {
        final String lf = System.getProperty("line.separator");
        final String grammar = "#JSGF V1.0;" + lf
            + "grammar test;" + lf
            + "public <test> = a{student.name='horst'}|b|c;";
        final StringReader reader = new StringReader(grammar);
        final RuleGrammar rule = recognizer.loadJSGF(reader);
        rule.setEnabled(true);
        final BaseResult result = new BaseResult(rule, "a");
        result.setResultState(BaseResult.ACCEPTED);

        final Jsapi10RecognitionResult res =
            new Jsapi10RecognitionResult(result);
        final Object out = res.getSemanticInterpretation();
        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        final Scriptable scope = context.initStandardObjects();
        scope.put("out", scope, out);
        Assert.assertEquals("horst", context.evaluateString(scope,
                "out.student.name", "expr", 1, null));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.Jsapi10RecognitionResult#getSemanticInterpretation()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetSemanticInterpretationSimple() throws Exception {
        final String lf = System.getProperty("line.separator");
        final String grammar = "#JSGF V1.0;" + lf
            + "grammar test;" + lf
            + "public <test> = yes{true}|no{false}|one{1234}|two{'horst'};";
        final StringReader reader = new StringReader(grammar);
        final RuleGrammar rule = recognizer.loadJSGF(reader);
        rule.setEnabled(true);

        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        final Scriptable scope = context.initStandardObjects();

        final BaseResult result1 = new BaseResult(rule, "yes");
        result1.setResultState(BaseResult.ACCEPTED);
        final Jsapi10RecognitionResult res1 =
            new Jsapi10RecognitionResult(result1);
        final Object out1 = res1.getSemanticInterpretation();
        scope.put("out", scope, out1);
        Assert.assertEquals(Boolean.TRUE, context.evaluateString(scope,
                "out", "expr", 1, null));

        final BaseResult result2 = new BaseResult(rule, "one");
        result2.setResultState(BaseResult.ACCEPTED);
        final Jsapi10RecognitionResult res2 =
            new Jsapi10RecognitionResult(result2);
        final Object out2 = res2.getSemanticInterpretation();
        scope.put("out", scope, out2);
        Assert.assertEquals(new Integer(1234), context.evaluateString(scope,
                "out", "expr", 1, null));

        final BaseResult result3 = new BaseResult(rule, "two");
        result3.setResultState(BaseResult.ACCEPTED);
        final Jsapi10RecognitionResult res3 =
            new Jsapi10RecognitionResult(result3);
        final Object out3 = res3.getSemanticInterpretation();
        scope.put("out", scope, out3);
        Assert.assertEquals("horst", context.evaluateString(scope,
                "out", "expr", 1, null));
    }
}
