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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.ExternalGrammarDocument;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * A parser for halef gramamrs.
 * @author Patrick L. Lange
 * @since 0.7.8
 */
public class HalefGrammarParser implements GrammarParser<GrammarDocument> {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getType() {
        return HalefGrammarType.HALEF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<GrammarDocument> load(final URI uri)
            throws IOException {
	/** TODO Come up with an actual format and not just put the URI as content */
        final URL url = uri.toURL();
	String urlString = url.toString();
        byte[] documentBuffer = urlString.getBytes();
        final String charset = Charset.defaultCharset().toString();
        final ExternalGrammarDocument document =
                new ExternalGrammarDocument(uri, documentBuffer, charset, true);
        return new HalefGrammarImplementation(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarEvaluator parse(final GrammarDocument document)
            throws IOException, UnsupportedFormatError {
        final String resourceName = document.getTextContent();
        final URI uri = document.getURI();
        return new HalefGrammarEvaluator(resourceName, uri);
    }
}
