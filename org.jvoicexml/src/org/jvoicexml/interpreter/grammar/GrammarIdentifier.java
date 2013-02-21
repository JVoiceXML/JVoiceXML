/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/grammar/GrammarIdentifier.java $
 * Version: $LastChangedRevision: 2582 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The <code>GrammarIdentifier</code> interface declares
 * a couple of methods to identify a certain grammar document.
 *
 * Each implementation of this class is considered to
 * be an identifier for a certain grammar. So common
 * task of all of these implementations is to identify
 * one, and only one specific kind of grammar.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision: 2582 $
 */
public interface GrammarIdentifier {
    /**
     * Identifies the grammar specified by <code>grammar</code>. If
     * <code>grammar</code> could be identified a type is returned, otherwise
     * <code>null</code>.
     *
     * @param grammar
     *        The grammar to be identified.
     *
     * @return String the type of the grammar. If grammar could not be
     *         identified, null is returned.
     */
    GrammarType identify(final GrammarDocument grammar);

    /**
     * Returns the supported media type.
     *
     * @return the supported media type.
     */
    GrammarType getSupportedType();
}
