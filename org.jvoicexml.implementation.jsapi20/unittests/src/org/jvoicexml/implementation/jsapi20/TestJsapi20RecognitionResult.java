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
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.jsapi2.recognition.BaseResult;
import org.jvoicexml.jsapi2.recognition.sphinx4.SphinxEngineListFactory;
import org.mockito.Mockito;

public class TestJsapi20RecognitionResult {

    @Test
    public void testGetSemanticInterpretation() throws Exception, SemanticError {
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
        final DataModel model = Mockito.mock(DataModel.class);
        Assert.assertEquals("T", res.getSemanticInterpretation(model));
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
                new RuleTag("out = new Object(); out.order = new Object();"),
                new RuleTag("out.order.topping=\"salami\";"),
                new RuleTag("out.order.size=\"medium\";"),
                new RuleTag("out.date=\"now\";")};
        final RuleSequence sequence = new RuleSequence(components);
        final Rule root = new Rule("test", sequence, Rule.PUBLIC);
        grammar.addRule(root);
        recognizer.processGrammars();
        final BaseResult result = new BaseResult(grammar, "test");
        final Jsapi20RecognitionResult res = new Jsapi20RecognitionResult(
                result);
        final DataModel model = Mockito.mock(DataModel.class);
        res.getSemanticInterpretation(model);
        Mockito.verify(model).updateVariable("test", 5);
//        Assert.assertEquals("{\"order\":{\"topping\":\"salami\",\"size\":\"medium\"},\"date\":\"now\"}",
//                ScriptingEngine.toJSON((ScriptableObject) interpretation));
    }

}
