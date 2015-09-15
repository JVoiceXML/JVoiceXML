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

package org.jvoicexml.interpreter.grammar.regex;

import java.net.URI;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * A {@link GrammarImplementation} for regex grammars.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public final class RegexGrammarImplementation
    implements GrammarImplementation<GrammarDocument> {
    /** The document. */
    private final GrammarDocument document;

    /** The regular expression. */
    private String regex;
    
    /**
     * Constructs a new object.
     * @param doc the grammar document.
     */
    public RegexGrammarImplementation(final GrammarDocument doc) {
        document = doc;
        document.getTextContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return document.getURI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (document == null) {
            result = prime * result;
        } else {
            result = prime * result + document.hashCode();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(final RecognitionResult result) {
        final String utterance = result.getUtterance();
        return utterance.matches(regex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<GrammarDocument> other) {
        if (other == null) {
            return false;
        }
        return document.equals(other.getGrammarDocument());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarDocument getGrammarDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return document.getMediaType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return ModeType.VOICE;
    }
}
