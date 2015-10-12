/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/implementation/SrgsXmlGrammarImplementation.java $
 * Version: $LastChangedRevision: 4509 $
 * Date:    $Date: 2015-01-12 09:08:00 +0100 (Mo, 12 Jan 2015) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Implementation of a SRGS XML grammar.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4509 $
 * @since 0.5.5
 */
public final class SrgsXmlGrammarImplementation
        implements GrammarImplementation<SrgsXmlDocument> {
    /** The encapsulated grammar. */
    private final SrgsXmlDocument document;

    /** {@code true} if the result is accepted. */
    private boolean accepted;


    /** The URI of the grammar document. */
    private final URI uri;

    /**
     * Constructs a new object.
     * 
     * @param doc
     *            the grammar
     * @param documentUri
     *            the URI of the grammar document
     */
    public SrgsXmlGrammarImplementation(final SrgsXmlDocument doc,
            final URI documentUri) {
        document = doc;
        uri = documentUri;
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
    public SrgsXmlDocument getGrammarDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        if (document == null) {
            return null;
        }
        final Grammar grammar = document.getGrammar();
        return grammar.getMode();
    }

    /**
     * Marks the result as accepted.
     * 
     * @param isAccepted
     *            <code>true</code> if the result is accepted.
     * @since 0.7
     */
    public void setAccepted(final boolean isAccepted) {
        accepted = isAccepted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(final RecognitionResult result) {
        return accepted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<SrgsXmlDocument> other) {
        return document.equals(other.getGrammarDocument());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (accepted ? 1231 : 1237);
        result = prime * result
                + ((document == null) ? 0 : document.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }
}
