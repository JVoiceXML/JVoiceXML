package org.jvoicexml.processor.srgs.grammar;

import java.io.IOException;
import java.net.URI;

//Comp. 2.0.6

public interface GrammarManager {
    Grammar[] listGrammars();

    Grammar loadGrammar(URI grammarReference)
            throws GrammarException, IOException;

    Grammar getGrammar(URI grammarReference);
    
    void deleteGrammar(Grammar grammar);

    public Rule resolve(RuleReference reference);
}
