/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/JVoiceXmlGrammarDocument.java $
 * Version: $LastChangedRevision: 2905 $
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
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Basic implementation of a {@link GrammarDocument}.
 *
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision: 2905 $
 * @since 0.5.5
 */
public final class JVoiceXmlGrammarDocument
        implements GrammarDocument {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlGrammarDocument.class);

    /** Base hash code. */
    private static final int HASH_CODE_BASE = 7;

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

    /** A grammar node. */
    private final Grammar grammar;
    
    /** URI of the grammar source. */
    private final URI uri;

    /**
     * Constructs a new object from a grammar node.
     * @param source URI of the grammar document
     * @param node the grammar node
     */
    public JVoiceXmlGrammarDocument(final URI source, final Grammar node) {
        uri = source;
        charset = System.getProperty("file.encoding");
        isAscii = true;
        document = null;
        //original line of code
//        buffer = node.toString().getBytes();
        //special android port line
        buffer = node.getTextContent().getBytes();
        grammar = node;
    }

    /**
     * Creates a new grammar document.
     * @param source URI of the grammar document
     * @param content the grammar itself
     * @param encoding guessed encoding of the grammar
     * @param ascii <code>true</code> if content is in ASCII format
     */
    public JVoiceXmlGrammarDocument(final URI source, final byte[] content,
            final String encoding, final boolean ascii) {
        uri = source;
        charset = encoding;
        isAscii = ascii;
        document = null;
        buffer = content;
        grammar = null;
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
        if (grammar != null) {
            return grammar.getTextContent();
        }
        return getDocument();
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
        if (!(obj instanceof JVoiceXmlGrammarDocument)) {
            return false;
        }
        final JVoiceXmlGrammarDocument other = (JVoiceXmlGrammarDocument) obj;
        return equals(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarDocument obj) {
        if (!(obj instanceof JVoiceXmlGrammarDocument)) {
            return false;
        }
        final JVoiceXmlGrammarDocument other = (JVoiceXmlGrammarDocument) obj;
        if (!Arrays.equals(buffer, other.buffer)) {
            return false;
        }
        if (mode != other.mode) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
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
    public int hashCode() {
        int hash = HASH_CODE_BASE;
        hash *= HASH_CODE_MULTIPLIER;
        if (type != null) {
            hash += type.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (uri != null) {
            hash += uri.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (document != null) {
            hash += document.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (buffer != null) {
            hash += buffer.hashCode();
        }
        return hash;
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
}
