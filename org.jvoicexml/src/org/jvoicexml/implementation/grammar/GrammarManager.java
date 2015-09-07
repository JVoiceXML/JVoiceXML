/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.grammar;

import java.io.IOException;

import org.jvoicexml.GrammarDocument;

/**
 * A grammar manager retrieves {@link org.jvoicexml.GrammarDocument}s to
 * load or remove and takes care that these are accessible via the
 * {@link org.jvoicexml.ImplementationPlatform}.
 * 
 * Usually, it will be used by the {@link GrammarProcessor} to process
 * a actual utterance into a {@link org.jvoicexml.RecognitionResult}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.8
 */
public interface GrammarManager {
    /**
     * Loads the given grammar.
     * @param document the grammar to load
     * @throws IOException
     *         error loading the grammar
     */
    void loadGrammar(final GrammarDocument document) throws IOException;

    /**
     * Unloads the given grammar.
     * @param document the grammar to unload
     */
    void unloadGrammar(final GrammarDocument document);
}
