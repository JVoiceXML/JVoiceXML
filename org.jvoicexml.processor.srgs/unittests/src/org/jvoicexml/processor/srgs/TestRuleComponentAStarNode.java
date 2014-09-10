package org.jvoicexml.processor.srgs;

import java.net.URI;
import java.net.URL;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleToken;

public class TestRuleComponentAStarNode {

    @Test
    public void testGetNextRuleComponent() throws Exception {
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final URL url = TestJVoiceXmlGrammarManager.class
                .getResource("pizza-de.xml");
        final URI uri = url.toURI();
        final RuleGrammar grammar = (RuleGrammar) manager.loadGrammar(uri);
        final String root = grammar.getRoot();
        final Rule rule = grammar.getRule(root);
        final RuleSequence sequence = (RuleSequence) rule.getRuleComponent();
        final RuleComponent[] components = sequence.getRuleComponents();
        final Queue<String> queue = new java.util.LinkedList<String>();
        final RuleComponentAStarNode node = new RuleComponentAStarNode(manager,
                sequence, queue);
        Assert.assertEquals(components[1],
                node.getNextRuleComponent(components[0]));
        Assert.assertEquals(components[2],
                node.getNextRuleComponent(components[1]));
        Assert.assertEquals(components[3],
                node.getNextRuleComponent(components[2]));
    }

    @Test
    public void testCostsTo() {
        final Queue<String> queue = new java.util.LinkedList<String>();
        queue.add("this");
        queue.add("is");
        queue.add("a");
        queue.add("test");
        final RuleToken token1 = new RuleToken("start");
        final RuleComponentAStarNode node1 = new RuleComponentAStarNode(null,
                token1, queue);
        final RuleToken token2 = new RuleToken("this is");
        final RuleComponentAStarNode node2 = new RuleComponentAStarNode(null,
                token2, queue);
        final RuleToken token3 = new RuleToken("a");
        final RuleComponentAStarNode node3 = new RuleComponentAStarNode(null,
                token3, queue);
        Assert.assertTrue(node1.costsTo(node2) < node1.costsTo(node3));
        Assert.assertEquals(node1.getHeuristicCosts(), node1.costsTo(node3),
                .001);
    }
}
