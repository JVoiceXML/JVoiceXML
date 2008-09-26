/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleName;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleToken;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Implementation of a JSGF grammar.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public final class RuleGrammarImplementation
    implements GrammarImplementation<RuleGrammar> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RuleGrammarImplementation.class);

    /** The encapsulated grammar. */
    private final RuleGrammar grammar;

    /**
     * Constructs a new object.
     * @param ruleGrammar the grammar.
     */
    public RuleGrammarImplementation(final RuleGrammar ruleGrammar) {
        grammar = ruleGrammar;
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar getGrammar() {
        return grammar;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return GrammarType.JSGF;
    }

    /**
     * Retrieves the name of the grammar.
     * @return name of the grammar.
     */
    public String getName() {
        return grammar.getName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean accepts(final RecognitionResult result) {
        if (grammar == null) {
            return false;
        }
        final String name = grammar.getName();
        final Rule root = grammar.getRule(name);
        if (root == null) {
            return false;
        }
        String[] words = result.getWords();
        if (words == null) {
            return false;
        }
        final int index = accepts(root, words, 0);
        return index == words.length;
    }

    final int accepts(final Rule rule,
            final String[] words, final int index) {
        int newIndex = index;
        if (rule instanceof RuleSequence) {
            final RuleSequence sequence = (RuleSequence) rule;
            newIndex = accepts(sequence, words, index);
        } else if (rule instanceof RuleToken) {
            final RuleToken token = (RuleToken) rule;
            newIndex = accepts(token, words, index);
        } else if (rule instanceof RuleAlternatives) {
            final RuleAlternatives alternatives = (RuleAlternatives) rule;
            newIndex = accepts(alternatives, words, index);
        } else if (rule instanceof RuleName) {
            final RuleName ref = (RuleName) rule;
            newIndex = accepts(ref, words, index);
        }
        return newIndex;
    }
    /**
     * Checks if the given utterance is matched by the given node.
     * @param grammar the grammar
     * @param words the utterance to check.
     * @param index current word
     * @param node the current node.
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final RuleSequence sequence,
            final String[] words, final int index) {
        final Rule[] rules = sequence.getRules();
        int localIndex = index;
        for (Rule current : rules) {
            int newIndex = accepts(current, words, localIndex);
            if (newIndex < 0) {
                return -1;
            } else {
                localIndex = newIndex;
            }
        }
        return localIndex;
    }

    /**
     * Checks if the given utterance is matched by the given text.
     * @param grammar the grammar
     * @param words the utterance to check.
     * @param index current word
     * @param text the current text node
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final RuleToken token, final String[] words,
            final int index) {
        final String value = token.getText().trim();
        if (value.length() == 0) {
            return -1;
        }
        final String[] content = value.split(" ");
        if (words.length < content.length) {
            return -1;
        }
        for (int i = 0; i < content.length; i++) {
            if (!words[index + i].equals(content[i])) {
                return -1;
            }
        }
        return index + content.length;
    }

    /**
     * Checks if the given utterance is matched by the given text.
     * @param grammar the grammar
     * @param words the utterance to check.
     * @param index current word
     * @param oneOf the current OneOf node
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final RuleAlternatives alternatives,
            final String[] words, final int index) {
        final Rule[] items = alternatives.getRules();
        for (Rule item : items) {
            final int newIndex = accepts(item, words, index);
            if (newIndex >= 0) {
                return newIndex;
            }
        }
        return -1;
    }
    /**
     * Checks if the given utterance is matched by the given text.
     * @param grammar the grammar
     * @param words the utterance to check.
     * @param index current word
     * @param ref the current Ruleref node
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final RuleName ref, final String[] words,
            final int index) {
        final String reference = ref.getRuleName();
        final Rule rule = grammar.getRule(reference);
        return accepts(rule, words, index);
    }
}
