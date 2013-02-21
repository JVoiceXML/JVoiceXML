/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jsapi10/src/org/jvoicexml/implementation/jsapi10/RuleGrammarImplementation.java $
 * Version: $LastChangedRevision: 2682 $
 * Date:    $Date: 2011-05-30 14:39:56 +0700 (Mon, 30 May 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mobicents;

import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleName;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleToken;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Implementation of a JSGF grammar.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2682 $
 * @since 0.5.5
 */
public final class RuleGrammarImplementation
    implements GrammarImplementation<RuleGrammar> {
    /** The encapsulated grammar. */
    private final RuleGrammar grammar;

    /** The grammars source. */
    private final String jsgf;

    /**
     * Constructs a new object.
     * @param ruleGrammar the grammar.
     * @param source the JSGF source code
     */
    public RuleGrammarImplementation(final RuleGrammar ruleGrammar,
            final String source) {
        grammar = ruleGrammar;
        jsgf = source;
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar getGrammar() {
        return grammar;
    }

    /**
     * Retrieves the JSGF source code.
     * @return the JSGF source code
     * @since 0.7.3
     */
    public String getJsgf() {
        return jsgf;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return GrammarType.JSGF;
    }

    /**
     * {@inheritDoc}
     */
    public ModeType getModeType() {
        return ModeType.VOICE;
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

    /**
     * Checks if the given utterance is matched by the given rule.
     * @param rule the current rule
     * @param words the utterance to check.
     * @param index current word
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final Rule rule,
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
     * Checks if the given utterance is matched by the given sequence.
     * @param sequence the current sequence
     * @param words the utterance to check.
     * @param index current word
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
     * Checks if the given utterance is matched by the given token.
     * @param token the current token
     * @param words the utterance to check.
     * @param index current word
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final RuleToken token, final String[] words,
            final int index) {
        final String value = token.getText().trim();
        if (value.length() == 0) {
            return -1;
        }
        final String[] content = value.split(" ");
        if ((index >= words.length) || (words.length < content.length)) {
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
     * Checks if the given utterance is matched by the given alternatives.
     * @param alternatives the current alternatives
     * @param words the utterance to check.
     * @param index current word
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
     * Checks if the given utterance is matched by the given rule reference.
     * @param ref the current reference
     * @param words the utterance to check.
     * @param index current word
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final RuleName ref, final String[] words,
            final int index) {
        final String reference = ref.getRuleName();
        final Rule rule = grammar.getRule(reference);
        return accepts(rule, words, index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<RuleGrammar> obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RuleGrammarImplementation other = (RuleGrammarImplementation) obj;
        if (jsgf == null) {
            if (other.jsgf != null) {
                return false;
            }
        } else if (!jsgf.equals(other.jsgf)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (grammar == null) {
            result = prime * result;
        } else {
            result = prime * result + grammar.hashCode();
        }
        if (jsgf == null) {
            result = prime * result;
        } else {
            result = prime * result + jsgf.hashCode();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @since 0.7.3
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(RuleGrammarImplementation.class.getCanonicalName());
        str.append('[');
        str.append(getMediaType());
        str.append(',');
        str.append(getModeType());
        str.append(',');
        str.append(jsgf);
        str.append(']');
        return str.toString();
    }
}
