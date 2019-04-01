/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.interpreter.grammar;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.w3c.dom.Document;

/**
 * A {@link GrammarDocument} referencing a grammar inside a VoiceXML document.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class InternalGrammarDocument implements GrammarDocument {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(InternalGrammarDocument.class);

    /** Base hash code. */
    private static final int HASH_CODE_BASE = 13;

    /** Multiplier for hash code generation. */
    private static final int HASH_CODE_MULTIPLIER = 31;

    /** The grammar type. */
    private GrammarType type;

    /** The mode type. */
    private ModeType mode;

    /** The grammar document. */
    private String document;

    /** Guessed character set. */
    private final String charset;

    /** The grammar document buffer if the document is binary. */
    private final byte[] buffer;

    /** A grammar node. */
    private final Grammar grammar;

    /** URI of the grammar source. */
    private URI uri;

    /**
     * Constructs a new object from a grammar node. This constructor is intended
     * to be used to capture inline grammars in a VoiceXML document.
     * 
     * @param node
     *            the grammar node
     * @throws UnsupportedEncodingException 
     *          the node contains a grammar with an invalid character set
     */
    public InternalGrammarDocument(final Grammar node)
            throws UnsupportedEncodingException {
        // Try getting the encoding of the owner document
        final Document owner = node.getOwnerDocument();
        final String ownerEncoding = owner.getInputEncoding();
        if (ownerEncoding == null) {
            charset = System.getProperty("file.encoding");
        } else {
            charset = ownerEncoding;
        }
        document = null;
        buffer = node.toString().getBytes(charset);
        grammar = node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCacheable() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setURI(final URI value) {
        uri = value;
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
    public GrammarType getMediaType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMediaType(final GrammarType grammartype) {
        type = grammartype;
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>true</code> since internal documents are for sure ASCII.
     */
    @Override
    public boolean isAscii() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBuffer() {
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocument() {
        if (document == null) {
            if (charset == null) {
                document = new String(buffer);
            } else {
                try {
                    document = new String(buffer, charset);
                } catch (UnsupportedEncodingException ex) {
                    LOGGER.warn("unable to use charset '" + charset
                            + "' to convert grammar '" + uri
                            + "'' Using default.", ex);
                    document = new String(buffer);
                }
            }
        }
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextContent() {
        return grammar.getTextContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof GrammarDocument)) {
            return false;
        }
        final GrammarDocument other = (GrammarDocument) obj;
        return equals(other);
    }

    /**
     * {@inheritDoc}
     * 
     * @return <code>true</code> if the {@link GrammarDocument}s share the same
     *         buffer
     */
    @Override
    public boolean equals(final GrammarDocument other) {
        final byte[] otherBuffer = other.getBuffer();
        return Arrays.equals(buffer, otherBuffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = HASH_CODE_MULTIPLIER;
        return prime * HASH_CODE_BASE + Arrays.hashCode(buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModeType(final ModeType modeType) {
        mode = modeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getCanonicalName());
        str.append('[');
        str.append(uri);
        str.append(',');
        str.append(mode);
        str.append(',');
        str.append(type);
        str.append(',');
        str.append(charset);
        str.append(',');
        str.append(getDocument());
        str.append(']');
        return str.toString();
    }
}
