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

import java.util.Collection;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.Grammar;

/**
 * An input item that can have nested grammar tags.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public interface GrammarContainer extends InputItem {
    /**
     * Gets all nested definitions of a <code>&lt;grammar&gt;</code>.
     *
     * @return Collection about all nested <code>&lt;grammar&gt;</code> tags.
     */
    Collection<Grammar> getGrammars();

    /**
     * Adds the given converted grammar to the list of converted grammars
     * for this field.
     * @param document the processed grammar document
     * @since 0.7
     */
    void addGrammar(final GrammarDocument document);

}
