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
import org.jvoicexml.xml.srgs.GrammarTypeFactory;

/**
 * Factory to retrieve the new grammar type.
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class RegexGrammarTypeFactory implements GrammarTypeFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getGrammarType(final String attribute) {
        if (attribute == null) {
            return null;
        }
        final MimeType other;
        try {
            other = new MimeType(attribute);
        } catch (MimeTypeParseException e) {
            return null;
        }
        if (RegexGrammarType.GRAMMAR_TYPE.match(other)) {
            return RegexGrammarType.REGEX;
        }
        return null;
    }

}
