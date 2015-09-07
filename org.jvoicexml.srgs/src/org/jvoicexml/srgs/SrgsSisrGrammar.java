package org.jvoicexml.srgs;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;
import org.jvoicexml.xml.srgs.Grammar;

public class SrgsSisrGrammar {
    private static final Logger LOGGER = Logger
            .getLogger(SrgsSisrGrammar.class);
    private Grammar grammarNode;
    private String rootRule;
    private URI uri;
    private boolean isLiteral = false;

    private SemanticInterpretationBlock globalTags = new SemanticInterpretationBlock();
    private HashMap<String, SrgsRule> rules = new HashMap<String, SrgsRule>();

    // A pool of grammars shared by all that were parsed together
    private HashMap<URI, SrgsSisrGrammar> grammarPool = null;

    public SrgsSisrGrammar(Grammar grammarNode, URI uri,
            HashMap<URI, SrgsSisrGrammar> grammarPool) {
        this.grammarNode = grammarNode;
        this.uri = uri;
        rootRule = grammarNode.getRoot();
        String tagFormat = grammarNode.getTagFormat();
        isLiteral = tagFormat != null
                && tagFormat.equals("semantics/1.0-literals");
        this.grammarPool = grammarPool;
    }

    public Grammar getGrammar() {
        return grammarNode;
    }

    public URI getUri() {
        return uri;
    }

    public SrgsSisrGrammar getGrammarFromPool(URI uri) {
        return grammarPool.get(uri);
    }

    public void putGrammarInPool(SrgsSisrGrammar grammar) {
        grammarPool.put(grammar.getUri(), grammar);
    }

    public HashMap<URI, SrgsSisrGrammar> getGrammarPool() {
        return grammarPool;
    }

    public SisrRecognitionResult isValid(String[] words) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addGlobalTagContent(String tagContents) {
        globalTags.append(tagContents);
    }

    public SemanticInterpretationBlock getGlobalTags() {
        return globalTags;
    }

    public void addRule(SrgsRule rule) {
        rules.put(rule.getId(), rule);
    }

    public SrgsRule getRule(String id, boolean needsToBePublic) {
        String desiredRuleId = id == null ? rootRule : id;

        SrgsRule rule = rules.get(desiredRuleId);
        if (rule == null)
            return null;

        if (needsToBePublic && !rule.isPublic())
            return null;

        return rule;
    }

    public Collection<SrgsRule> getRules() {
        return rules.values();
    }

    public void dump() {
        LOGGER.debug("grammar(uri=" + uri + ", root=" + rootRule + ")");
        globalTags.dump(" ");
        for (SrgsRule rule : rules.values()) {
            rule.dump(" ");
        }
    }

    /**
     * Recognize a parsed grammar
     * 
     * @param text
     *            Caller's spoken text
     * @return The recognition
     */
    public Object recognize(String text) {
        LOGGER.debug("recognize(" + text + ")");

        if (text == null || text.length() == 0)
            return null;

        MatchConsumption mc = match(text);
        if (mc == null) {
            LOGGER.debug("recognize: did not recognize");
            return null;
        }
        LOGGER.debug("Recognized: " + text);

        if (LOGGER.isTraceEnabled())
            mc.dump(true);

        return mc.executeSisr();
    }

    public Object recognize(String[] words) {
        ArrayList<String> list = new ArrayList<String>();
        for (String word : words) {
            list.add(word);
        }

        MatchConsumption mc = match(list);
        if (mc == null) {
            return null;
        }

        return mc.executeSisr();
    }

    MatchConsumption match(ArrayList<String> tokens) {
        SrgsRule rule = rules.get(rootRule);
        if (rule == null)
            return null;
        MatchConsumption mc = rule.match(tokens, 0);
        if (mc != null) {
            mc.setGlobalExecutableSI(globalTags);
        }
        return mc;
    }

    MatchConsumption match(String text) {
        ArrayList<String> tokens = new ArrayList<String>();
        String[] parts = text.split(" ");
        for (String part : parts) {
            if (part.trim().length() > 0) {
                tokens.add(part.trim());
            }
        }

        return match(tokens);
    }

    public boolean isLiteral() {
        return isLiteral;
    }

    public void setLiteral(boolean isLiteral) {
        this.isLiteral = isLiteral;
    }

}
