package org.jvoicexml.interpreter.grammar.luis;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Definition of the LUIS grammar type as {@code application/grammar+luis}.
 * @author Dirk Schnelle-Walka
 *
 */
public class LUISGrammarType extends GrammarType {
    /** The grammar type {@code application/grammar+regex}. */
    public static final String GRAMMAR_TYPE = "application/grammar+luis";

    /** Regex formatted grammar. */
    public static final GrammarType LUIS =
        new LUISGrammarType();

    /**
     * Constructs a new object.
     */
    private LUISGrammarType() {
        super(GRAMMAR_TYPE, false);
    }
}
