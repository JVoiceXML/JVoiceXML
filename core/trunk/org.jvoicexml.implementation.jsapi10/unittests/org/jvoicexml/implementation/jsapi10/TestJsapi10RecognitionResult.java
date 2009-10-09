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
package org.jvoicexml.implementation.jsapi10;

import java.io.StringReader;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.RuleGrammar;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.implementation.jsapi10.jvxml.Sphinx4EngineCentral;
import org.jvoicexml.implementation.jsapi10.jvxml.Sphinx4RecognizerModeDesc;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.sun.speech.engine.recognition.BaseResult;

/**
 * Test cases for {@link Jsapi10RecognitionResult}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public class TestJsapi10RecognitionResult {
    /**
     * Global initialization.
     * @throws EngineException
     *         error registering the engine.
     */
    @BeforeClass
    public static void init() throws EngineException {
        Central.registerEngineCentral(Sphinx4EngineCentral.class.getName());
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
            + "public <test> = a{name='horst'}|b|c;";
        final StringReader reader = new StringReader(grammar);
        final RecognizerModeDesc desc = new Sphinx4RecognizerModeDesc();
        final Recognizer recognizer =
            Central.createRecognizer(desc);
        recognizer.allocate();
        recognizer.waitEngineState(Recognizer.ALLOCATED);
        final RuleGrammar rule = recognizer.loadJSGF(reader);
        rule.setEnabled(true);
        final BaseResult result = new BaseResult(rule, "a");
        result.setResultState(BaseResult.ACCEPTED);

        final Jsapi10RecognitionResult res =
            new Jsapi10RecognitionResult(result);
        final ScriptableObject out = res.getSemanticInterpretation();
        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        final Scriptable scope = context.initStandardObjects();
        scope.put("out", scope, out);
        Assert.assertEquals("horst", context.evaluateString(scope, "out.name",
                "expr", 1, null));
    }

}
