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

package org.jvoicexml.interpreter.grammar;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.interpreter.GrammarRegistry;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedMap;

/**
 * Implementation of a {@link GrammarRegistry}.
 *
 * <p>
 * The grammars are held in a {@link ScopedMap}.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.3
 */
public final class JVoiceXmlGrammarRegistry
        implements GrammarRegistry {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlGrammarRegistry.class);

    /** The grammar documents contained in the collection. */
    private ScopedMap<GrammarDocument, GrammarImplementation<?>> documents;

    /**
     * Constructs a new object.
     *
     * @since 0.5.5
     */
    JVoiceXmlGrammarRegistry() {
        documents =
            new ScopedMap<GrammarDocument, GrammarImplementation<?>>(null);
    }

    /**
     * {@inheritDoc}
     */
    public void setScopeObserver(final ScopeObserver observer) {
        documents =
            new ScopedMap<GrammarDocument, GrammarImplementation<?>>(observer);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final GrammarDocument document) {
        return documents.containsKey(document);
    }

    /**
     * {@inheritDoc}
     */
    public void addGrammar(
            final GrammarDocument document,
            final GrammarImplementation<? extends Object> grammar) {
        if (grammar == null) {
            LOGGER.warn("cannot add a null grammar");

            return;
        }

        documents.put(document, grammar);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarImplementation<?>> getGrammars() {
        return documents.values();
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<?> getGrammar(final GrammarDocument document) {
        return documents.get(document);
    }
}
