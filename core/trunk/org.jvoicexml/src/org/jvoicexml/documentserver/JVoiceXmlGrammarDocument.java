/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.util.Arrays;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Basic implementation of a {@link GrammarDocument}.
 *
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision$
 * @since 0.5.5
 */
public final class JVoiceXmlGrammarDocument
        implements GrammarDocument {
    /** Base hash code. */
    private static final int HASH_CODE_BASE = 7;

    /** Multiplier for hash code generation. */
    private static final int HASH_CODE_MULTIPLIER = 31;

    /** The grammar type. */
    private GrammarType type;

    /** The mode type. */
    private ModeType mode;

    /** The grammar document. */
    private final String document;

    /** The grammar document buffer if the document is binary. */
    private final byte[] buffer;

    /** URI of the grammar source. */
    private final URI uri;

    /**
     * Creates a new ASCCI grammar document
     * @param source URI of the grammar document
     * @param content
     *        The grammar itself.
     */
    public JVoiceXmlGrammarDocument(final URI source, final String content) {
        uri = source;
        document = content;
        buffer = null;
    }

    /**
     * Creates a new binary grammar document
     * @param source URI of the grammar document
     * @param content
     *        The grammar itself.
     */
    public JVoiceXmlGrammarDocument(final URI source, final byte[] content) {
        uri = source;
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
        return document != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBuffer() {
        if (document != null) {
            return document.getBytes();
        }
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
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
        if (document == null) {
            if (other.document != null) {
                return false;
            }
        } else if (!document.equals(other.document)) {
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
    public void setModeType(ModeType modeType) {
        mode = modeType;
    }
}
