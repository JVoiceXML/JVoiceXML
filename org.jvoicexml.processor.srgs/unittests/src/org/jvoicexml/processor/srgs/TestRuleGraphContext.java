package org.jvoicexml.processor.srgs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.RuleAlternatives;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleToken;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

public class TestRuleGraphContext {
    private URI writeToFile(SrgsXmlDocument document, final File file)
            throws IOException {
        final FileWriter writer = new FileWriter(file);
        final String xml = document.toString();
        writer.write(xml);
        writer.close();
        return file.toURI();
    }

    @Test
    public void testTokens() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        rule.addText("this is a test");
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final RuleGraphContext context = new RuleGraphContext(manager,
                ruleGrammar);
        final String root = ruleGrammar.getRoot();
        final org.jvoicexml.processor.srgs.grammar.Rule rootRule = ruleGrammar
                .getRule(root);
        final RuleComponent component = rootRule.getRuleComponent();
        Assert.assertNull(context.getNext(component));
    }

    @Test
    public void testOneOf() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final OneOf politeOneOf = rule.appendChild(OneOf.class);
        final Item politeItem = politeOneOf.appendChild(Item.class);
        politeItem.setOptional();
        politeItem.addText("please");
        rule.addText("press ");
        final OneOf oneOf = rule.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final RuleGraphContext context = new RuleGraphContext(manager,
                ruleGrammar);
        RuleComponent[] next0 = context.getNext(null);
        Assert.assertEquals(1, next0.length);
        Assert.assertTrue(next0[0] instanceof RuleSequence);
        final RuleGraphContext context1 = context.nextContext(next0[0]);
        RuleComponent[] next1 = context1.getNext(next0[0]);
        Assert.assertEquals(1, next1.length);
        Assert.assertTrue(next1[0] instanceof RuleAlternatives);
        final RuleGraphContext context2 = context1.nextContext(next1[0]);
        RuleComponent[] next2 = context2.getNext(next1[0]);
        Assert.assertEquals(1, next2.length);
        Assert.assertTrue(next2[0] instanceof RuleCount);
        final RuleGraphContext context3 = context2.nextContext(next2[0]);
        RuleComponent[] next3 = context3.getNext(next2[0]);
        Assert.assertEquals(1, context3.getCurrentIterationCount());
        Assert.assertEquals(1, context3.getCurrentLocalMaxIterationCount());
        Assert.assertEquals(2, next3.length);
        Assert.assertTrue(next3[0] instanceof RuleToken);
        Assert.assertEquals("please", ((RuleToken) next3[0]).getText());
        Assert.assertTrue(next3[1] instanceof RuleToken);
        Assert.assertEquals("press", ((RuleToken) next3[1]).getText());
        final RuleGraphContext context4 = context3.nextContext(next3[0]);
        final RuleComponent[] next4 = context4.getNext(next3[0]);
        Assert.assertEquals(1, next4.length);
        Assert.assertTrue(next4[0] instanceof RuleToken);
        Assert.assertEquals("press", ((RuleToken) next4[0]).getText());
        final RuleGraphContext context5 = context4.nextContext(next4[0]);
        final RuleComponent[] next5 = context5.getNext(next4[0]);
        Assert.assertTrue(next5[0] instanceof RuleAlternatives);
        final RuleGraphContext context6 = context5.nextContext(next5[0]);
        final RuleComponent[] next6 = context6.getNext(next5[0]);
        Assert.assertEquals(3, next6.length);
        Assert.assertTrue(next6[1] instanceof RuleToken);
        Assert.assertEquals("2", ((RuleToken) next6[1]).getText());
        final RuleGraphContext context7 = context6.nextContext(next6[1]);
        final RuleComponent[] next7 = context7.getNext(next6[1]);
        Assert.assertNull(next7);
        final RuleGraphContext context32 = context3.getSubContext(next3[1]);
        final RuleGraphContext context42 = context32.nextContext(next3[1]);
        Assert.assertEquals(0, context32.getCurrentIterationCount());
        Assert.assertEquals(0, context32.getCurrentLocalMaxIterationCount());
        final RuleComponent[] next42 = context42.getNext(next3[1]);
        Assert.assertEquals(1, next42.length);
        Assert.assertTrue(next42[0] instanceof RuleAlternatives);
        final RuleGraphContext context52 = context42.nextContext(next42[0]);
        final RuleComponent[] next52 = context52.getNext(next42[0]);
        Assert.assertEquals(3, next52.length);
        Assert.assertTrue(next52[1] instanceof RuleToken);
        Assert.assertEquals("2", ((RuleToken) next52[1]).getText());
        final RuleGraphContext context62 = context52.nextContext(next52[1]);
        final RuleComponent[] next62 = context62.getNext(next52[1]);
        Assert.assertNull(next62);
    }

    @Test
    public void testReference() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule ruleDigit = grammar.appendChild(Rule.class);
        ruleDigit.setId("digit");
        final OneOf oneOf = ruleDigit.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final Ruleref ref1 = rule.appendChild(Ruleref.class);
        ref1.setUri("#digit");
        rule.addText("or");
        final Ruleref ref2 = rule.appendChild(Ruleref.class);
        ref2.setUri("#digit");
        final File file = File.createTempFile("jvxmltest", "vxml");
        final URI uri = writeToFile(document, file);
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final RuleGrammar ruleGrammar = (RuleGrammar) manager.loadGrammar(uri);
        final String root = ruleGrammar.getRoot();
        final org.jvoicexml.processor.srgs.grammar.Rule rootRule = ruleGrammar
                .getRule(root);
        final RuleGraphContext context = new RuleGraphContext(manager,
                ruleGrammar);
        final RuleComponent component = rootRule.getRuleComponent();
        final RuleGraphContext nextContext1 = context.nextContext(component);
        final RuleComponent[] next1 = nextContext1.getNext(component);
        Assert.assertEquals(1, next1.length);
        Assert.assertTrue(next1[0] instanceof RuleReference);
        Assert.assertEquals("digit", ((RuleReference) next1[0]).getRuleName());
        final RuleGraphContext nextContext2 = nextContext1
                .nextContext(next1[0]);
        final RuleComponent[] next2 = nextContext2.getNext(next1[0]);
        Assert.assertEquals(1, next2.length);
        Assert.assertTrue(next2[0] instanceof RuleAlternatives);
        final RuleGraphContext nextContext3 = nextContext2
                .nextContext(next2[0]);
        final RuleComponent[] next3 = nextContext3.getNext(next2[0]);
        Assert.assertEquals(3, next3.length);
        Assert.assertTrue(next3[0] instanceof RuleToken);
        Assert.assertEquals("1", ((RuleToken) next3[0]).getText());
        Assert.assertTrue(next3[1] instanceof RuleToken);
        Assert.assertEquals("2", ((RuleToken) next3[1]).getText());
        Assert.assertTrue(next3[2] instanceof RuleToken);
        Assert.assertEquals("3", ((RuleToken) next3[2]).getText());
        final RuleGraphContext nextContext4 = nextContext3
                .nextContext(next3[1]);
        RuleComponent[] next4 = nextContext4.getNext(next3[1]);
        Assert.assertEquals(1, next4.length);
        Assert.assertTrue(next4[0] instanceof RuleToken);
        Assert.assertEquals("or", ((RuleToken) next4[0]).getText());
        final RuleGraphContext nextContext5 = nextContext4
                .nextContext(next4[0]);
        RuleComponent[] next5 = nextContext5.getNext(next4[0]);
        Assert.assertEquals(1, next5.length);
        Assert.assertTrue(next5[0] instanceof RuleReference);
        Assert.assertEquals("digit", ((RuleReference) next5[0]).getRuleName());
        final RuleGraphContext nextContext6 = nextContext5
                .nextContext(next5[0]);
        final RuleComponent[] next6 = nextContext6.getNext(next5[0]);
        Assert.assertEquals(1, next6.length);
        Assert.assertTrue(next6[0] instanceof RuleAlternatives);
        final RuleGraphContext nextContext7 = nextContext6
                .nextContext(next6[0]);
        final RuleComponent[] next7 = nextContext7.getNext(next6[0]);
        Assert.assertEquals(3, next7.length);
        Assert.assertTrue(next7[0] instanceof RuleToken);
        Assert.assertEquals("1", ((RuleToken) next7[0]).getText());
        Assert.assertTrue(next7[1] instanceof RuleToken);
        Assert.assertEquals("2", ((RuleToken) next7[1]).getText());
        Assert.assertTrue(next7[2] instanceof RuleToken);
        Assert.assertEquals("3", ((RuleToken) next7[2]).getText());
        final RuleGraphContext nextContext8 = nextContext7
                .nextContext(next7[0]);
        Assert.assertNull(nextContext8.getNext(next7[0]));
    }

}
