package org.jvoicexml.srgs;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.AddToMatchedText;
import org.jvoicexml.srgs.sisr.ExecutableSI;

public class TokenRuleExpansion implements RuleExpansion {
    private static final Logger LOGGER = Logger
            .getLogger(TokenRuleExpansion.class);

    private ArrayList<String> tokens = new ArrayList<String>();
    private ExecutableSI executableSI = null;

    public TokenRuleExpansion() {
    }

    public TokenRuleExpansion(String token) {
        addToken(token);
    }

    public TokenRuleExpansion(String[] split) {
        addTokens(split);
    }

    public void addToken(String token) {
        tokens.add(token);
    }

    public void addTokens(String[] tokens) {
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.length() == 0)
                continue;
            this.tokens.add(trimmed);
        }
    }

    /**
     * Add executable SI (tag) to item to return if matched. Note, per spec only
     * the last tag associated with the element is kept.
     * 
     * @param si
     */
    public void setExecutionSI(ExecutableSI si) {
        executableSI = si;
    }

    @Override
    public MatchConsumption match(ArrayList<String> tokensToMatch, int offset) {
        int tokenCount = tokens.size();

        if (tokenCount == 0)
            return new MatchConsumption(); // empty match

        // If there aren't enough tokens left to match, short circuit the check
        if (tokenCount > tokensToMatch.size() - offset)
            return null;

        for (int i = 0; i < tokenCount; i++)
            if (!tokens.get(i).equals(tokensToMatch.get(offset + i)))
                return null;

        // Return match. No SI attached to a token (items, yes, tokens no)
        MatchConsumption result = new MatchConsumption(tokenCount);
        result.addTokens(tokens);

        String matchedText = SrgsSisrXmlGrammarParser.joinTokens(tokensToMatch,
                offset, result.getTokensConsumed());
        if (matchedText.length() > 0)
            result.addExecutableSI(new AddToMatchedText(matchedText));

        // Maintain null if present
        result.addExecutableSI(executableSI);
        return result;
    }

    @Override
    public void dump(String pad) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : tokens) {
            if (first)
                first = false;
            else
                sb.append(' ');
            sb.append(s);
        }

        LOGGER.debug(pad + "token(" + sb + ") ");
        if (executableSI != null)
            executableSI.dump(pad);

    }

}
