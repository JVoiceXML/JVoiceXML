/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2016 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver;

import java.net.URI;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * A grammar document that is only expressed by a URI.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class UriGrammarDocument implements GrammarDocument {
    /** The URI of the grammar document. */
    private URI uri;

    /** The grammar type. */
    private GrammarType type;

    /** The mode type. */
    private ModeType mode;

   /**
    * Constructs a new object.
    * 
    * @param source the source UI
    * @param gramamrType the grammar type
    * @param modeType the mode type
    */
    public UriGrammarDocument(final URI source, final GrammarType gramamrType,
            final ModeType modeType) {
        uri = source;
        type = gramamrType;
        mode = modeType;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCacheable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setURI(final URI source) {
        uri = source;
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
    public void setMediaType(final GrammarType gramamrType) {
        type = gramamrType;
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
    public void setModeType(final ModeType modeType) {
        mode = modeType;
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
    public boolean isAscii() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocument() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextContent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBuffer() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        return equals((ExternalGrammarDocument) obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarDocument obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UriGrammarDocument other = (UriGrammarDocument) obj;
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
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getCanonicalName());
        str.append('[');
        str.append(uri);
        str.append(',');
        str.append(mode);
        str.append(',');
        str.append(type);
        str.append(']');
        return str.toString();
    }
}
