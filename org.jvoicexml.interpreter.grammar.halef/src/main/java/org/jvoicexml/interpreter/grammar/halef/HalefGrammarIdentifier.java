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

package org.jvoicexml.interpreter.grammar.halef;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * A {@link GrammarIdentifier} for the new grammar. Halef grammars
 * are always considered valid at the moment.
 * @author Patrick L. Lange
 * @since 0.7.8
 */
public class HalefGrammarIdentifier implements GrammarIdentifier {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType identify(final GrammarDocument grammar) {
        final GrammarType requestedType = grammar.getMediaType();
        if (requestedType.equals(HalefGrammarType.HALEF)) {
            return HalefGrammarType.HALEF;
        }
        final String content = grammar.getTextContent();
        if (content == null) {
            return null;
        }
        if (grammar.getTextContent().startsWith("wfst")) {
            return HalefGrammarType.HALEF;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getSupportedType() {
        return HalefGrammarType.HALEF;
    }
}
