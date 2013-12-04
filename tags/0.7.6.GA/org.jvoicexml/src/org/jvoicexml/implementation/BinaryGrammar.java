/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.net.URI;
import java.util.Arrays;

/**
 * A buffer for a binary grammar. A binary grammar can be used in a
 * {@link GrammarImplementation} to capture the loaded
 * {@link org.jvoicexml.GrammarDocument}.
 * 
 * @author Shuo Yang
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public class BinaryGrammar {
    /** The grammar document buffer if the document is binary. */
    private final byte[] buffer;

    /** URI of the grammar source. */
    private final URI uri;

    /**
     * Constructs a new object.
     * @param src the URI of the grammar
     * @param grammar the binary grammar.
     */
    public BinaryGrammar(final URI src, final byte[] grammar) {
        uri = src;
        buffer = grammar;
    }

    /**
     * Retrieves the URI of the grammar.
     * @return the URI of the grammar.
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * Retrieves the contents of the binary grammar.
     * @return the contents of the grammar.
     * @since 0.7.5
     */
    public final byte[] getGrammar() {
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(buffer);
        if (uri == null) {
            result = prime * result;
        } else {
            result = prime * result +  uri.hashCode();
        }
        return result;
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
        if (!(obj instanceof BinaryGrammar)) {
            return false;
        }
        BinaryGrammar other = (BinaryGrammar) obj;
        if (!Arrays.equals(buffer, other.buffer)) {
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
}
