/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Implementation of a SRGS grammar.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.5.5
 */
public final class RuleGrammarImplementation
    implements GrammarImplementation<RuleGrammar> {
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
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    public ModeType getModeType() {
        return ModeType.VOICE;
    }

    /**
     * Retrieves the name of this grammar.
     * @return name of the grammar
     */
    public String getName() {
        return grammar.getReference();
    }

    /**
     * {@inheritDoc}
     */
    public boolean accepts(final RecognitionResult result) {
        try {
            final RuleParse rp = grammar.parse(result.getWords(),
                                               grammar.getRoot());
            return rp != null;
        } catch (GrammarException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * @since 0.7.2
     */
    @Override
    public boolean equals(final Object obj) {
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
        return equals(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<RuleGrammar> other) {
        if (other == null) {
            return false;
        }
        final RuleGrammar otherGrammar = other.getGrammar();
        if (grammar == null) {
            if (otherGrammar != null) {
                return false;
            }
        } else if (!grammar.equals(otherGrammar)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * @since 0.7.2
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
        return result;
    }
}
