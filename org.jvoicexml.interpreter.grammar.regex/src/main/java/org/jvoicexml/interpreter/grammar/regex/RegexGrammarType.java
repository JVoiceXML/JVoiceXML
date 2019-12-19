/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Definition of the regex grammar type as {@code application/grammar+regex}.
 * @author Dirk Schnelle-Walka
 *
 */
final class RegexGrammarType extends GrammarType {
    public final static MimeType GRAMMAR_TYPE;

    static {
        try {
            GRAMMAR_TYPE = new MimeType("application", "grammar+regex");
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** Regex formatted grammar. */
    public static final GrammarType REGEX =
        new RegexGrammarType();

    /**
     * Constructs a new object.
     */
    private RegexGrammarType() {
        super(GRAMMAR_TYPE, false);
    }
}
