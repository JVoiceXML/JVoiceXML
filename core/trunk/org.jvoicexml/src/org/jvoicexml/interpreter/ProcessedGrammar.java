/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;

/**
 * A <code>&lt;grammar&gt;</code> that has been processed by the
 * {@link GrammarProcessor}. In fact this is a pair of a
 * {@link GrammarDocument} and a {@link GrammarImplementation}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public final class ProcessedGrammar {
    /** The grammar document. */
    private GrammarDocument document;

    /** The transformed grammar implementation. */
    private GrammarImplementation<?> implementation;

    /**
     * Constructs a new object.
     */
    public ProcessedGrammar() {
    }

    /**
     * Constructs a new object initializing with the given parameters.
     * @param doc the grammar document
     * @param impl the grammar implementation
     */
    public ProcessedGrammar(final GrammarDocument doc,
            final GrammarImplementation<?> impl) {
        document = doc;
        implementation = impl;
    }

    /**
     * Retrieves the grammar document.
     * @return the grammar document
     */
    public GrammarDocument getDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     * @since 0.7.2
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((document == null) ? 0 : document.hashCode());
        result = prime * result
                + ((implementation == null) ? 0 : implementation.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @since 0.7.2
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProcessedGrammar other = (ProcessedGrammar) obj;
        if (document == null) {
            if (other.document != null) {
                return false;
            }
        } else if (!document.equals(other.document)) {
            return false;
        }
        if (implementation == null) {
            if (other.implementation != null) {
                return false;
            }
        } else if (!implementation.equals(other.implementation)) {
            return false;
        }
        return true;
    }

    /**
     * Sets the grammar document.
     * @param doc the grammar document
     */
    public void setDocument(final GrammarDocument doc) {
        document = doc;
    }

    /**
     * Retrieves the grammar implementation.
     * @return the grammar implementation
     */
    public GrammarImplementation<?> getImplementation() {
        return implementation;
    }

    /**
     * Sets the grammar implementation.
     * @param impl the grammar implementation
     */
    public void setImplementation(final GrammarImplementation<?> impl) {
        implementation = impl;
    }
}
