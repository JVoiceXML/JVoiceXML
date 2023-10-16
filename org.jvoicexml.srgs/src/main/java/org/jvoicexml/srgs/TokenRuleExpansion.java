/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.srgs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.AddToMatchedText;
import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;

public class TokenRuleExpansion implements RuleExpansion {
    private static final Logger LOGGER = Logger
            .getLogger(TokenRuleExpansion.class);

    private ArrayList<String> tokens = new ArrayList<String>();
    private ExecutableSemanticInterpretation executableSI = null;

    public TokenRuleExpansion() {
    }

    public TokenRuleExpansion(final String token) {
        addToken(token);
    }

    public TokenRuleExpansion(final String[] split) {
        addTokens(split);
    }

    public void addToken(final String token) {
        tokens.add(token);
    }

    public void addTokens(final String[] tokens) {
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.length() == 0) {
                continue;
            }
            this.tokens.add(trimmed);
        }
    }

    /**
     * Add executable SI (tag) to item to return if matched. Note, per spec only
     * the last tag associated with the element is kept.
     * 
     * @param si
     */
    public void setExecutionSemanticInterpretation(
            final ExecutableSemanticInterpretation si) {
        executableSI = si;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MatchConsumption match(final List<String> tokensToMatch, 
            final int offset) {
        int tokenCount = tokens.size();

        if (tokenCount == 0) {
            return new MatchConsumption(); // empty match
        }

        // If there aren't enough tokens left to match, short circuit the check
        if (tokenCount > tokensToMatch.size() - offset) {
            return null;
        }

        for (int i = 0; i < tokenCount; i++) {
            if (!tokens.get(i).equals(tokensToMatch.get(offset + i))) {
                return null;
            }
        }

        // Return match. No SI attached to a token (items, yes, tokens no)
        MatchConsumption result = new MatchConsumption(tokenCount);
        result.addTokens(tokens);

        String matchedText = SrgsSisrXmlGrammarParser.joinTokens(tokensToMatch,
                offset, result.getTokensConsumed());
        if (matchedText.length() > 0) {
            result.addExecutableSemanticInterpretation(
                    new AddToMatchedText(matchedText));
        }

        // Maintain null if present
        result.addExecutableSemanticInterpretation(executableSI);
        return result;
    }

    @Override
    public void dump(final String pad) {
        final String str = toString();
        LOGGER.debug(pad + "token(" + str + ") ");
        if (executableSI != null) {
            executableSI.dump(pad);
        }
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        for (String s : tokens) {
            if (str.length() != 0) {
                str.append(' ');
            }
            str.append(s);
        }
        return str.toString();
    }
}
