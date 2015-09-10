package org.jvoicexml.interpreter.grammar.regex;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;

public class RegexGrammarIdentifier implements GrammarIdentifier {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType identify(final GrammarDocument grammar) {
        // Check if we are able to compile it into a pattern.
        final String content = grammar.getTextContent();
        try {
            Pattern.compile(content);
        } catch (PatternSyntaxException e) {
            // Compilation was not successful, so non of this kind
            return null;
        }
        
        // It could compile, so it is a valid regular expression.
        return RegexGrammarType.REGEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getSupportedType() {
        return RegexGrammarType.REGEX;
    }

}
