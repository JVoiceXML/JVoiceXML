/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/srgs/GrammarTypeFactory.java $
 * Version: $LastChangedRevision: 2632 $
 * Date:    $Date: 2011-05-04 16:52:27 -0500 (mié, 04 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

/**
 * A factory for grammar types.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2632 $
 * @since 0.7
 */
public interface GrammarTypeFactory {
    /**
     * Creates a grammar for the given attribute.
     * @param attribute name of the attribute.
     * @return corresponding grammar type or <code>null</code> if the
     *         attribute can not be converted to a grammar type.
     */
    GrammarType getGrammarType(final String attribute);
}
