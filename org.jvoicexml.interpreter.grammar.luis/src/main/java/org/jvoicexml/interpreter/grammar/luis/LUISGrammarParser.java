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
package org.jvoicexml.interpreter.grammar.luis;

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.UriGrammarDocument;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * A parser for regex gramamrs.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class LUISGrammarParser implements GrammarParser<GrammarDocument> {
    /** The LUIS subscription key. */
    private String subscriptionKey;

    /**
     * Constructs a new object.
     */
    public LUISGrammarParser() {
    }

    /**
     * Sets the subscription key
     * 
     * @param subscription
     *            the subscription key
     */
    public void setSubscription(final String subscription) {
        subscriptionKey = subscription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getType() {
        return LUISGrammarType.LUIS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<GrammarDocument> load(final URI uri)
            throws IOException {
        final UriGrammarDocument document = new UriGrammarDocument(
                uri, LUISGrammarType.LUIS, ModeType.VOICE);
        return new LUISGrammarImplementation(document, subscriptionKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarEvaluator parse(final GrammarDocument document)
            throws IOException, UnsupportedFormatError {
        final URI uri = document.getURI();
        return new LUISGrammarEvaluator(subscriptionKey, uri);
    }
}
