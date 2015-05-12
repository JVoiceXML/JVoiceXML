package org.jvoicexml.processor.srgs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.jvoicexml.processor.srgs.grammar.Grammar;
import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;
import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleGrammar;
import org.jvoicexml.processor.srgs.grammar.RuleReference;

public class JVoiceXmlGrammarManager implements GrammarManager {
    private final Map<URI, Grammar> grammars;

    public JVoiceXmlGrammarManager() {
        grammars = new java.util.HashMap<URI, Grammar>();
    }
    
    @Override
    public Grammar[] listGrammars() {
        final Grammar[] listed = new Grammar[grammars.size()];
        return grammars.values().toArray(listed);
    }


    @Override
    public Grammar getGrammar(URI grammarReference) {
        return grammars.get(grammarReference);
    }
    
    @Override
    public Grammar loadGrammar(URI grammarReference)
            throws GrammarException, IOException {
        final SrgsRuleGrammarParser parser = new SrgsRuleGrammarParser();
        final URL url = grammarReference.toURL();
        final InputStream in = url.openStream();
        Rule[] rules;
        try {
            rules = parser.load(in);
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
        if (rules == null) {
            throw new IOException("Unable to load grammar '" + grammarReference
                    + "'");
        }
        // Initialize rule grammar
        final JVoiceXmlGrammar grammar = new JVoiceXmlGrammar(this,
                grammarReference);
        grammar.addRules(rules);
        final Map<String, String> attributes = parser.getAttributes();
        final String root = attributes.get("root");
        if (root != null) {
            grammar.setRoot(root);
            grammar.setActivatable(root, true);
        }

        // Register grammar
        grammars.put(grammarReference, grammar);

        return grammar;
    }

    @Override
    public void deleteGrammar(Grammar grammar) {
        grammars.remove(grammar);
    }

    public Rule resolve(RuleReference reference) {
        final URI ref = reference.getGrammarReference();
        final RuleGrammar grammar = (RuleGrammar) grammars.get(ref);
        if (ref == null) {
            return null;
        }
        String name = reference.getRuleName();
        if (name == null) {
            name = grammar.getRoot();
        }
        return grammar.getRule(name);
    }
}
