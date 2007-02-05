/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/UserInput.java $
 * Version: $LastChangedRevision: 142 $
 * Date:    $Date: 2006-11-29 09:25:22 +0100 (Mi, 29 Nov 2006) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Wrapper for a grammar.
 *
 * <p>
 * VoiceXML supports multiple types of grammar. This class represents
 * a container to transfer the different implementations regardless of the
 * actual type.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 206 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 *
 * @param <T> the grammar.
 */
public final class TypedGrammar<T extends GrammarImplementation> {
    /** The current media type. */
    private final GrammarType mediaType;

    /** The encapsulated grammar. */
    private final T grammar;

    /**
     * Constructs a new objects.
     * @param type the media type.
     * @param gram the grammar implementation.
     */
    public TypedGrammar(final GrammarType type, final T gram) {
        mediaType = type;
        grammar = gram;
    }

    /**
     * Returns the declared media type of the external grammar.
     *
     * @return The media type of the grammar file.
     */
    public GrammarType getMediaType() {
        return mediaType;
    }

    /**
     * Retrieves the grammar object.
     * @return the grammar.
     */
    public T getGrammar() {
        return grammar;
    }

    /**
     * Retrieves the name of this grammar.
     *
     * @return name of the gramar.
     */
    public String getName() {
        return grammar.getName();
    }
}
