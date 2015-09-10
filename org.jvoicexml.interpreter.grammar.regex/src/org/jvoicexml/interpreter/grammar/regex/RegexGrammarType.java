package org.jvoicexml.interpreter.grammar.regex;

import org.jvoicexml.xml.srgs.GrammarType;

class RegexGrammarType extends GrammarType {
    public static final String GRAMMAR_TYPE = "application/grammar+regex";

    /** Regex formatted grammar. */
    public static final GrammarType REGEX =
        new RegexGrammarType();

    /**
     * Constructs a new object.
     */
    private RegexGrammarType() {
        super(GRAMMAR_TYPE, false);
    }
}
