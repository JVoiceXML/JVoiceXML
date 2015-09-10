package org.jvoicexml.interpreter.grammar.regex;

import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.GrammarTypeFactory;

public class RegexGrammarTypeFactory implements GrammarTypeFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getGrammarType(String attribute) {
        if (attribute == null) {
            return null;
        }
        if (RegexGrammarType.GRAMMAR_TYPE.equals(attribute)) {
            return RegexGrammarType.REGEX;
        }
        return null;
    }

}
