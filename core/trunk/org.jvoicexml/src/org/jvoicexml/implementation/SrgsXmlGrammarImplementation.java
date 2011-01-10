/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.processor.srgs.GrammarGraph;
import org.jvoicexml.processor.srgs.SrgsXmlGrammarParser;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Implementation of a SRGS XML grammar.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public final class SrgsXmlGrammarImplementation
    implements GrammarImplementation<SrgsXmlDocument> {
    /** The encapsulated grammar. */
    private final SrgsXmlDocument document;

    /** A parsed graph. */
    private GrammarGraph graph;

    /** The grammar checker for the {@link #graph}. */
    private GrammarChecker checker;

    /**
     * Constructs a new object.
     * @param doc the grammar.
     */
    public SrgsXmlGrammarImplementation(final SrgsXmlDocument doc) {
        document = doc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SrgsXmlDocument getGrammar() {
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
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(final RecognitionResult result) {
        if (document == null) {
            return false;
        }
        if (graph == null) {
            final SrgsXmlGrammarParser parser = new SrgsXmlGrammarParser();
            graph = parser.parse(document);
        }
        if (checker == null) {
            checker = new GrammarChecker(graph);
        }
        final String[] words = result.getWords();
        if (words == null) {
            return false;
        }
        return checker.isValid(words);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<SrgsXmlDocument> other) {
        return document.equals(other.getGrammar());
    }

    /**
     * {@inheritDoc}
     * @since 0.7.2
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (checker == null) {
            result = prime * result;
        } else {
            result = prime * result + checker.hashCode();
        }
        if (document == null) {
            result = prime * result;
        } else {
            result = prime * result + document.hashCode();
        }
        if (graph == null) {
            result = prime * result;
        } else {
            result = prime * result + graph.hashCode();
        }
        return result;
    }
}

