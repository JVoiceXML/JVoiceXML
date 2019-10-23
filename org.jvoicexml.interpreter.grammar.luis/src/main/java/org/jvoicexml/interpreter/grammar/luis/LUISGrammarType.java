package org.jvoicexml.interpreter.grammar.luis;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Definition of the LUIS grammar type as {@code application/grammar+luis}.
 * @author Dirk Schnelle-Walka
 *
 */
public class LUISGrammarType extends GrammarType {
    /** The grammar type {@code application/grammar+regex}. */
    public final static MimeType GRAMMAR_TYPE;

    static {
        try {
            GRAMMAR_TYPE = new MimeType("application", "grammar+luis");
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
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
