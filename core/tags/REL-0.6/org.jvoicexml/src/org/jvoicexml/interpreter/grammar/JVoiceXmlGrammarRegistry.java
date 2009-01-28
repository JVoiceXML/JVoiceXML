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
import org.jvoicexml.interpreter.scope.ScopedCollection;

/**
 * Implementation of a {@link GrammarRegistry}.
 *
 * <p>
 * The grammars are held in a {@link ScopedCollection}.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class JVoiceXmlGrammarRegistry
        implements GrammarRegistry {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlGrammarRegistry.class);

    /** The grammar documents contained in the collection. */
    private ScopedCollection<GrammarDocument> documents;

    /** The scope aware map of all grammars. */
    private ScopedCollection<GrammarImplementation<? extends Object>> grammars;

    /**
     * Constructs a new object.
     *
     * @since 0.5.5
     */
    JVoiceXmlGrammarRegistry() {
        grammars =
            new ScopedCollection<GrammarImplementation<? extends Object>>(null);

        documents =
            new ScopedCollection<GrammarDocument>(null);
    }

    /**
     * {@inheritDoc}
     */
    public void setScopeObserver(final ScopeObserver observer) {
        grammars = new
            ScopedCollection<GrammarImplementation<? extends Object>>(observer);
        documents = new ScopedCollection<GrammarDocument>(observer);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final GrammarDocument document) {
        return documents.contains(document);
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

        documents.add(document);
        grammars.add(grammar);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GrammarImplementation<? extends Object>> getGrammars() {
        return grammars;
    }
}
