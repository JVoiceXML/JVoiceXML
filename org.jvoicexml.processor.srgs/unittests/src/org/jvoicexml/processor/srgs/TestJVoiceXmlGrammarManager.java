package org.jvoicexml.processor.srgs;

import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleReference;

public class TestJVoiceXmlGrammarManager {

    @Test
    public void testParseStringGrammarManagerStringString() throws Exception {
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final URL url = TestJVoiceXmlGrammarManager.class.getResource("pizza-de.xml");
        final URI uri = url.toURI();
        final RuleGrammar grammar = (RuleGrammar) manager.loadGrammar(uri);
        final String[] rules = grammar.listRuleNames();
        Assert.assertEquals(5, rules.length);
    }
    
    @Test
    public void testResolve() throws Exception {
        final GrammarManager manager = new JVoiceXmlGrammarManager();
        final URL url = TestJVoiceXmlGrammarManager.class.getResource("pizza-de.xml");
        final URI uri = url.toURI();
        final RuleGrammar grammar = (RuleGrammar) manager.loadGrammar(uri);
        final String root = grammar.getRoot();
        final RuleReference reference = new RuleReference(uri, root);
        final Rule rule = manager.resolve(reference);
        Assert.assertNotNull(rule);
    }

}
