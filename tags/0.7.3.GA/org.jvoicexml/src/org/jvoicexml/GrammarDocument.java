/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Grammar document that is retrieved from the {@link DocumentServer}.
 *
 * <p>
 * VoiceXML is designed to support at least
 * <ul>
 * <li>JSGF</li>
 * <li>SRGS grammar with ABNF format</li>
 * <li>SRGS grammar with XML format</li>
 * </ul>
 * Documents are kept as a string.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5.5
 */
public interface GrammarDocument {
    /**
     * Sets the media type of this document.
     * @param type the new media type.
     */
    void setMediaType(final GrammarType type);

    /**
     * Returns the declared media type of the grammar.
     *
     * @return The media type of the grammar file.
     */
    GrammarType getMediaType();

    /**
     * Retrieves the document as a string.
     * @return the document object.
     */
    String getDocument();

    /**
     * Checks if this grammar document is equal to the given grammar document.
     * @param other the grammar document to compare with.
     * @return <code>true</code> if the grammar documents are equal.
     * @since 0.7.2
     */
    boolean equals(final GrammarDocument other);
}
