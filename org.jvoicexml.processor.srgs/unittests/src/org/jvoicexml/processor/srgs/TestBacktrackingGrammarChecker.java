package org.jvoicexml.processor.srgs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleParse;
import org.jvoicexml.processor.srgs.grammar.RuleToken;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

public class TestBacktrackingGrammarChecker {

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
        final BacktrackingGrammarChecker checker = new BacktrackingGrammarChecker();
        final String[] input = new String[] { "this", "is", "a", "test" };
        final RuleComponent result = checker.backtrack(context, input);
        Assert.assertTrue("expected RuleParse, but was " + result,
                result instanceof RuleParse);
        final RuleParse parse = (RuleParse) result;
        final RuleComponent component = parse.getParse();
        Assert.assertTrue(component instanceof RuleToken);
        final RuleToken token = (RuleToken) component;
        Assert.assertEquals("this is a test", token.getText());
    }

    @Test
    public void testTokensLonger() throws Exception {
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
        final BacktrackingGrammarChecker checker = new BacktrackingGrammarChecker();
        final String[] input = new String[] { "this", "is", "a", "test",
                "longer" };
        final RuleComponent result = checker.backtrack(context, input);
        Assert.assertNull(result);
    }

    @Test
    public void testTokensShorter() throws Exception {
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
        final BacktrackingGrammarChecker checker = new BacktrackingGrammarChecker();
        final String[] input = new String[] { "this", "is", "a" };
        final RuleComponent result = checker.backtrack(context, input);
        Assert.assertNull(result);
    }

    @Test
    public void testTokensWrongInput() throws Exception {
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
        final BacktrackingGrammarChecker checker = new BacktrackingGrammarChecker();
        final String[] input = new String[] { "this", "is", "another", "test" };
        final RuleComponent result = checker.backtrack(context, input);
        Assert.assertNull(result);
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
        final BacktrackingGrammarChecker checker = new BacktrackingGrammarChecker();
        final String[] input = new String[] { "press", "2"};
        final RuleComponent result = checker.backtrack(context, input);
        Assert.assertEquals("<ruleref uri=\"#test\"><one-of><item>"
                + "<item repeat=\"0\">please</item>press<one-of><item>2</item>"
                + "</one-of></item></one-of></ruleref>",
                result.toString());
    }

    @Test
    public void testOneOfAlternative() throws Exception {
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
        final BacktrackingGrammarChecker checker = new BacktrackingGrammarChecker();
        final String[] input = new String[] { "please", "press", "2"};
        final RuleComponent result = checker.backtrack(context, input);
        System.out.println(result);
        Assert.assertEquals("<ruleref uri=\"#test\"><one-of><item>"
                + "<item repeat=\"1\">please</item>press<one-of><item>2</item>"
                + "</one-of></item></one-of></ruleref>",
                result.toString());
    }
}
