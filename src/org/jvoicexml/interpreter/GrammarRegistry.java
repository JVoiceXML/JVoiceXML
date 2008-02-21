/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;

/**
 * Provides scope aware access to active grammars.
 *
 * <p>
 * The registry maintains a set of active grammars. Grammars are added to the
 * registry by the {@link GrammarProcessor} via the
 * {@link #addGrammar(GrammarDocument, GrammarImplementation)} method.
 * The {@link GrammarProcessor} must take care that only documents are
 * added to the registry that are not contained. The latter can
 * be checked via the {@link #contains(GrammarDocument)} method.
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
public interface GrammarRegistry {
    /**
     * Checks if the registry already contains the given grammar document.
     * @param document the document.
     * @return <code>true</code> if the given document is known by the registry.
     * @since 0.6
     */
    boolean contains(final GrammarDocument document);

    /**
     * Adds the given grammar to the list of known grammars.
     * @param document grammar document.
     * @param grammar converted grammar to add.
     */
    void addGrammar(final GrammarDocument document,
            final GrammarImplementation<? extends Object> grammar);

    /**
     * Gets all registered grammars.
     *
     * @return Collection
     */
    Collection<GrammarImplementation<? extends Object>> getGrammars();
}
