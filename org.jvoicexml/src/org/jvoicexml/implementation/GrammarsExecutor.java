package org.jvoicexml.implementation;

import java.util.HashSet;
import java.util.Set;
import org.jvoicexml.RecognitionResult;

/**
 *
 * @author raphael
 */
public final class GrammarsExecutor {
    
    private final Set<GrammarImplementation> grammars;
    
    private GrammarImplementation lastGrammar;
    
    public GrammarsExecutor() {
        grammars = new HashSet<>();
        lastGrammar = null;
    }
    
    public Set getSet() {
        return grammars;
    }
    
    public boolean isAcceptable(final RecognitionResult result) {
        for (GrammarImplementation<?> grammar : grammars) {
            if (grammar.accepts(result)) {
                lastGrammar = grammar;
                return true;
            }
        }
        lastGrammar = null;
        return false;
    }
    
    public GrammarImplementation getLastGrammar() {
        return lastGrammar;
    }
}
