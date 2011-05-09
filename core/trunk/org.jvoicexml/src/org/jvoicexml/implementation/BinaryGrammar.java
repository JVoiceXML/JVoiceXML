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

/**
 * A binary grammar document.
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
}
