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
import org.jvoicexml.processor.srgs.grammar.RuleToken;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

public class TestAStar {
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
        final String[] input = new String[] { "this", "is", "a", "test" };
        final AStar astar = new AStar();
        final org.jvoicexml.processor.srgs.grammar.Rule rootRule = ruleGrammar
                .getRule("test");
        final RuleComponent rootComponent = rootRule.getRuleComponent();

        final GrammarChecker checker = new GrammarChecker(manager, null);
        final RuleComponent validRule = checker.isValid(ruleGrammar, input);
        Assert.assertTrue(validRule instanceof RuleToken);
        final RuleToken token = (RuleToken) validRule;
        Assert.assertEquals("this is a test", token.getText());
    }

}
