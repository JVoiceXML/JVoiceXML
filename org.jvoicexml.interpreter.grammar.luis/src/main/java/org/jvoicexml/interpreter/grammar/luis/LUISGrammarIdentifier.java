/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * A {@link GrammarIdentifier} for the new grammar. LUIS grammars
 * are considered to be valid if they point to a LUIS deployment.
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class LUISGrammarIdentifier implements GrammarIdentifier {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType identify(final GrammarDocument grammar) {
        // Check if we are able to find a link to LUIS
        final URI uri = grammar.getURI();
        final String host = uri.getHost();
        if (host == null) {
            return null;
        }
        if (host.toLowerCase().contains("api.cognitive.microsoft.com")) {
        	return LUISGrammarType.LUIS;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getSupportedType() {
        return LUISGrammarType.LUIS;
    }
}
