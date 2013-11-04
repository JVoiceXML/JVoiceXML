/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/ExternalGrammarDocument.java $
 * Version: $LastChangedRevision: 3231 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Basic implementation of a {@link GrammarDocument}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3231 $
 * @since 0.5.5
 */
public final class ExternalGrammarDocument
        implements GrammarDocument {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ExternalGrammarDocument.class);

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

    /** <code>true</code> if the contents of {@link #buffer} is plain text. */
    private final boolean isAscii;

    /** The grammar document buffer if the document is binary. */
    private final byte[] buffer;

    /** URI of the grammar source. */
    private final URI uri;

    /**
     * Creates a new grammar document. This constructor is intended to be
     * used to capture external grammars.
     * @param source URI of the grammar document
     * @param content the grammar itself
     * @param encoding guessed encoding of the grammar
     * @param ascii <code>true</code> if content is in ASCII format
     */
    public ExternalGrammarDocument(final URI source, final byte[] content,
            final String encoding, final boolean ascii) {
        uri = source;
        charset = encoding;
        isAscii = ascii;
        document = null;
        buffer = content;
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
     */
    @Override
    public boolean isAscii() {
        return isAscii;
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
                            + "' to convert grammar '"  + uri
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
        return getDocument();
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
     * @return <code>true</code> if the {@link GrammarDocument}s share
     * the same buffer
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
        int result = HASH_CODE_BASE;
        result = prime * result + Arrays.hashCode(buffer);
        return result;
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
