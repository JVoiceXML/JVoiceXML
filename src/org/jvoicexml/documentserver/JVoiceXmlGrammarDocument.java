/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Basic implementation of a {@link GrammarDocument}.
 *
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
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

    /** The grammar document. */
    private final String document;

    /**
     * Creates a new object.
     *
     * @param content
     *        The grammar itself.
     */
    public JVoiceXmlGrammarDocument(final String content) {
        this.document = content;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public void setMediaType(final GrammarType grammartype) {
        type = grammartype;
    }

    /**
     * {@inheritDoc}
     */
    public String getDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     * @since 0.6
     *
     * <p>
     * Objects are considered to be equal if they have the same grammar
     * type and the same document.
     * </p>
     *
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JVoiceXmlGrammarDocument)) {
            return false;
        }

        final JVoiceXmlGrammarDocument other = (JVoiceXmlGrammarDocument) obj;
        final boolean equalType;
        final GrammarType otherType = other.getMediaType();
        if (type == null) {
            equalType = otherType == null;
        } else {
            equalType = type.equals(otherType);
        }

        if (!equalType) {
            return false;
        }

        final boolean equalDocument;
        final String otherDocument = other.getDocument();
        if (document == null) {
            equalDocument = otherDocument == null;
        } else {
            equalDocument = document.equals(otherDocument);
        }
        return equalDocument;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.6
     */
    @Override
    public int hashCode() {
        int hash = HASH_CODE_BASE;
        hash *= HASH_CODE_MULTIPLIER;
        if (type != null) {
            hash += type.hashCode();
        }
        hash *= HASH_CODE_MULTIPLIER;
        if (document != null) {
            hash += document.hashCode();
        }
        return hash;
    }
}
