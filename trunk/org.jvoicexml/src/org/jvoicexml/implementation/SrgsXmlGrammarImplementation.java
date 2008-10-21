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

package org.jvoicexml.implementation;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Implementation of a SRGS XML grammar.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public final class SrgsXmlGrammarImplementation
    implements GrammarImplementation<SrgsXmlDocument> {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SrgsXmlGrammarImplementation.class);

    /** The encapsulated grammar. */
    private final SrgsXmlDocument document;

    /**
     * Constructs a new object.
     * @param doc the grammar.
     */
    public SrgsXmlGrammarImplementation(final SrgsXmlDocument doc) {
        document = doc;
    }

    /**
     * {@inheritDoc}
     */
    public SrgsXmlDocument getGrammar() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    public boolean accepts(final RecognitionResult result) {
        if (document == null) {
            return false;
        }
        final Grammar grammar = document.getGrammar();
        final Rule root = grammar.getRootRule();
        if (root == null) {
            return false;
        }
        String[] words = result.getWords();
        if (words == null) {
            return false;
        }
        final int index = accepts(grammar, words, 0, root);
        return index == words.length;
    }

    /**
     * Checks if the given utterance is matched by the given node.
     * @param grammar the grammar
     * @param words the utterance to check.
     * @param index current word
     * @param node the current node.
     * @return the new index if the utterances match, <code>-1</code> otherwise.
     */
    private int accepts(final Grammar grammar,
            final String[] words, final int index, final XmlNode node) {
        final Collection<XmlNode> nodes = node.getChildren();
        int localIndex = index;
        for (XmlNode current : nodes) {
            int newIndex = localIndex;
            if (current instanceof Text) {
                final Text text = (Text) current;
                newIndex = accepts(grammar, words, localIndex, text);
            } else if (current instanceof OneOf) {
                final OneOf oneOf = (OneOf) current;
                newIndex = accepts(grammar, words, localIndex, oneOf);
            } else if (current instanceof Ruleref) {
                final Ruleref ref = (Ruleref) current;
                newIndex = accepts(grammar, words, localIndex, ref);
            }
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
    private int accepts(final Grammar grammar, final String[] words,
            final int index, final Text text) {
        final String value = text.getTextContent().trim();
        if (value.length() == 0) {
            // Ignore whitespace.
            return index;
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
    private int accepts(final Grammar grammar, final String[] words,
            final int index,
            final OneOf oneOf) {
        Collection<Item> items = oneOf.getChildNodes(Item.class);
        for (Item item : items) {
            final int newIndex = accepts(grammar, words, index, item);
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
    private int accepts(final Grammar grammar, final String[] words,
            final int index, final Ruleref ref) {
        final String reference = ref.getUri();
        if (!reference.startsWith("#")) {
            LOGGER.warn("external references are currently not supported: "
                    + reference);
            return -1;
        }
        final String localReference = reference.substring(1);
        final Rule rule = grammar.getRule(localReference);
        return accepts(grammar, words, index, rule);
    }
}

