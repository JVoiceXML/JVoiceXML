/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/builtin/GrammarCreator.java $
 * Version: $LastChangedRevision: 2597 $
 * Date:    $Date: 2011-02-19 17:43:35 -0600 (sáb, 19 feb 2011) $
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

package org.jvoicexml.documentserver.schemestrategy.builtin;

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.event.error.BadFetchError;

/**
 * A creator for a built-in grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2597 $
 * @since 0.7.1
 */
interface GrammarCreator {
    /**
     * Creates the built-in grammar.
     * @param uri the URI for the built-in grammar to create.
     * @return created grammar as a byte stream
     * @exception BadFetchError
     *            error creating the grammar
     * @exception IOException
     *            error reading the grammar
     */
    byte[] createGrammar(final URI uri) throws BadFetchError, IOException;

    /**
     * Retrieves the type name of this grammar creator.
     * @return type name
     * @since 0.7.5
     */
    String getTypeName();
}
