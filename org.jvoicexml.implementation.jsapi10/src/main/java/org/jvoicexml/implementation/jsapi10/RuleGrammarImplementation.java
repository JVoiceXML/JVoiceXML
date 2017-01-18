/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Implementation of a JSGF grammar.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5.5
 */
public final class RuleGrammarImplementation
        implements GrammarImplementation<RuleGrammar> {
    /** The encapsulated grammar. */
    private final RuleGrammar grammar;

    /** The grammars source. */
    private final URI uri;

    /**
     * Constructs a new object.
     * 
     * @param ruleGrammar
     *            the grammar.
     * @param source
     *            the JSGF source code
     */
    public RuleGrammarImplementation(final RuleGrammar ruleGrammar,
            final URI source) {
        grammar = ruleGrammar;
        uri = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleGrammar getGrammarDocument() {
        return grammar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return GrammarType.JSGF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return ModeType.VOICE;
    }

    /**
     * Retrieves the name of the grammar.
     * 
     * @return name of the grammar.
     */
    public String getName() {
        return grammar.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RuleGrammarImplementation)) {
            return false;
        }
        RuleGrammarImplementation other = (RuleGrammarImplementation) obj;
        if (grammar == null) {
            if (other.grammar != null) {
                return false;
            }
        } else if (!grammar.equals(other.grammar)) {
            return false;
        }
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<RuleGrammar> obj) {
        RuleGrammarImplementation other = (RuleGrammarImplementation) obj;
        if (grammar == null) {
            if (other.grammar != null) {
                return false;
            }
        } else if (!grammar.equals(other.grammar)) {
            return false;
        }
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
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
        str.append(uri);
        str.append(']');
        return str.toString();
    }
}
