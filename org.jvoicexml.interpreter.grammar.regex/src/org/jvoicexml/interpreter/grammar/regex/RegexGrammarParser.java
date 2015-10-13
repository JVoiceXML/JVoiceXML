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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * A parser for regex gramamrs.
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class RegexGrammarParser implements GrammarParser<GrammarDocument> {
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getType() {
        return RegexGrammarType.REGEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<GrammarDocument> load(final URI uri)
            throws IOException {
        final URL url = uri.toURL();
        final InputStream input = url.openStream();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        do {
            read = input.read(buffer);
            out.write(buffer, 0, read);
        } while (read != 0);
        byte[] documentBuffer = out.toByteArray();
        final String charset = Charset.defaultCharset().toString();
        final ExternalGrammarDocument document =
                new ExternalGrammarDocument(uri, documentBuffer, charset, true);
        return new RegexGrammarImplementation(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarEvaluator parse(final GrammarDocument document)
            throws IOException, UnsupportedFormatError {
        final String regex = document.getTextContent();
        final URI uri = document.getURI();
        return new RegexGrammarEvaluator(regex, uri);
    }
}
