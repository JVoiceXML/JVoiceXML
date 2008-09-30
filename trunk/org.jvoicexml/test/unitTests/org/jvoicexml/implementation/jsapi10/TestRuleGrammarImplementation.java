/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.jsapi10;

import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleName;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleToken;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.test.DummyRecognitionResult;
import org.jvoicexml.test.implementationplatform.DummyRuleGrammar;

/**
 * Test cases for {@link RuleGrammarImplementation}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestRuleGrammarImplementation {

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.RuleGrammarImplementation#accepts(org.jvoicexml.RecognitionResult)}.
     */
    @Test
    public void testAcceptsRecognitionResult() {
        final RuleGrammar grammar = new DummyRuleGrammar();
        String[] words = new String[] {"this", "is", "a", "test"};
        RuleSequence sequence = new RuleSequence(words);
        grammar.setRule(grammar.getName(), sequence, true);
        final RuleGrammarImplementation impl =
            new RuleGrammarImplementation(grammar);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("this is a test");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("this is");
        Assert.assertFalse(result2.getUtterance() + " should not be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("this is a test dummy");
        Assert.assertFalse(result3.getUtterance() + " should not be accepted",
                impl.accepts(result3));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testAcceptsOneOf() throws Exception {
        final RuleGrammar grammar = new DummyRuleGrammar();
        final RuleToken token1 = new RuleToken("1");
        final RuleToken token2 = new RuleToken("2");
        final RuleToken token3 = new RuleToken("3");
        final Rule[] tokens = new Rule[] {token1, token2, token3};
        final RuleAlternatives alternatives = new RuleAlternatives(tokens);
        final RuleToken press = new RuleToken("press");
        final Rule[] rules = new Rule[] {press, alternatives};
        final RuleSequence sequence = new RuleSequence(rules);
        grammar.setRule(grammar.getName(), sequence, true);
        final RuleGrammarImplementation impl =
            new RuleGrammarImplementation(grammar);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("press 1");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("press 2");
        Assert.assertTrue(result2.getUtterance() + " should be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("press 3");
        Assert.assertTrue(result3.getUtterance() + " should be accepted",
                impl.accepts(result3));
        final DummyRecognitionResult result4 = new DummyRecognitionResult();
        result4.setUtterance("press 4");
        Assert.assertFalse(result4.getUtterance() + " should not be accepted",
                impl.accepts(result4));
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.SrgsXmlGrammarImplementation#accepts(RecognitionResult)}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testAcceptsReference() throws Exception {
        final RuleGrammar grammar = new DummyRuleGrammar();
        final RuleToken token1 = new RuleToken("1");
        final RuleToken token2 = new RuleToken("2");
        final RuleToken token3 = new RuleToken("3");
        final Rule[] tokens = new Rule[] {token1, token2, token3};
        final RuleAlternatives digits = new RuleAlternatives(tokens);
        grammar.setRule("digit", digits, true);
        final RuleName name1 = new RuleName("digit");
        final RuleToken token = new RuleToken("or");
        final RuleName name2 = new RuleName("digit");
        final Rule[] rules = new Rule[] {name1, token, name2};
        final RuleSequence sequence = new RuleSequence(rules);
        grammar.setRule(grammar.getName(), sequence, true);
        final RuleGrammarImplementation impl =
            new RuleGrammarImplementation(grammar);
        final DummyRecognitionResult result1 = new DummyRecognitionResult();
        result1.setUtterance("2 or 3");
        Assert.assertTrue(result1.getUtterance() + " should be accepted",
                impl.accepts(result1));
        final DummyRecognitionResult result2 = new DummyRecognitionResult();
        result2.setUtterance("1 or 3");
        Assert.assertTrue(result2.getUtterance() + " should be accepted",
                impl.accepts(result2));
        final DummyRecognitionResult result3 = new DummyRecognitionResult();
        result3.setUtterance("3 or 1");
        Assert.assertTrue(result3.getUtterance() + " should be accepted",
                impl.accepts(result3));
        final DummyRecognitionResult result4 = new DummyRecognitionResult();
        result4.setUtterance("2 or 4");
        Assert.assertFalse(result4.getUtterance() + " should not be accepted",
                impl.accepts(result4));
    }
}
