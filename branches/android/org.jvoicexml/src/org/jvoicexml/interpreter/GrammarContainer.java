/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/GrammarContainer.java $
 * Version: $LastChangedRevision: 3218 $
 * Date:    $Date: 2012-08-23 09:15:40 +0200 (Thu, 23 Aug 2012) $
 * Author:  $LastChangedBy: schnelle $
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
 * @version $Revision: 3218 $
 * @since 0.7.2
 */
public interface GrammarContainer extends InputItem {
    /**
     * Gets all nested definitions of a <code>&lt;grammar&gt;</code>.
     *
     * @return collection of all nested <code>&lt;grammar&gt;</code> tags.
     */
    Collection<Grammar> getGrammars();

    /**
     * Retrieves the list of converted grammars.
     * @return list of converted grammars
     * @since 0.7.6
     */
    Collection<GrammarDocument> getGrammarDocuments();

    /**
     * Adds the given converted grammar to the list of converted grammars
     * for this field.
     * @param document the processed grammar document
     * @since 0.7
     */
    void addGrammar(final GrammarDocument document);

}
