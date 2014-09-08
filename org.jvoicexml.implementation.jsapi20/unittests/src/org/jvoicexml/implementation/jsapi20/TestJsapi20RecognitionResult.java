package org.jvoicexml.implementation.jsapi20;

import javax.speech.EngineManager;
import javax.speech.SpeechLocale;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.jsapi2.recognition.BaseResult;
import org.jvoicexml.jsapi2.recognition.sphinx4.SphinxEngineListFactory;
import org.mozilla.javascript.ScriptableObject;

public class TestJsapi20RecognitionResult {

    @Test
    public void testGetSemanticInterpretation() throws Exception {
        EngineManager.registerEngineListFactory(SphinxEngineListFactory.class
                .getName());
        Recognizer recognizer = (Recognizer) EngineManager
                .createEngine(new RecognizerMode(SpeechLocale.ENGLISH));
        recognizer.allocate();
        final GrammarManager manager = recognizer.getGrammarManager();
        final RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                "test");
        final RuleComponent[] components = new RuleComponent[] {
                new RuleToken("test"), new RuleTag("T") };
        final RuleSequence sequence = new RuleSequence(components);
        final Rule root = new Rule("test", sequence, Rule.PUBLIC);
        grammar.addRule(root);
        recognizer.processGrammars();
        final BaseResult result = new BaseResult(grammar, "test");
        final Jsapi20RecognitionResult res = new Jsapi20RecognitionResult(
                result);
        Assert.assertEquals("T", res.getSemanticInterpretation());
    }

    @Test
    public void testGetSemanticInterpretationCompundObject() throws Exception,
            SemanticError {
        EngineManager.registerEngineListFactory(SphinxEngineListFactory.class
                .getName());
        Recognizer recognizer = (Recognizer) EngineManager
                .createEngine(new RecognizerMode(SpeechLocale.ENGLISH));
        recognizer.allocate();
        final GrammarManager manager = recognizer.getGrammarManager();
        final RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                "test");
        final RuleComponent[] components = new RuleComponent[] {
                new RuleToken("test"),
                new RuleTag("out = new Object(); out.test='hello';") };
        final RuleSequence sequence = new RuleSequence(components);
        final Rule root = new Rule("test", sequence, Rule.PUBLIC);
        grammar.addRule(root);
        recognizer.processGrammars();
        final BaseResult result = new BaseResult(grammar, "test");
        final Jsapi20RecognitionResult res = new Jsapi20RecognitionResult(
                result);
        final Object interpretation = res.getSemanticInterpretation();
        Assert.assertEquals("{\"test\":\"hello\"}",
                ScriptingEngine.toJSON((ScriptableObject) interpretation));
    }

}
