/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/GrammarProcessor.java $
 * Version: $LastChangedRevision: 2592 $
 * Date:    $Date: 2011-02-17 22:01:47 -0600 (jue, 17 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.Configurable;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.xml.srgs.Grammar;

/**
 * The <code>GrammarProcessor</code> is the main entry point for
 * grammar processing.
 *
 * <p>
 * This class provides a lean method interface to process a grammar
 * in a VoiceXML file.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Christoph Buente
 *
 * @version $Revision: 2592 $
 *
 * @since 0.3
 */
public interface GrammarProcessor extends Configurable {
    /**
     * Give in grammar into this processor and let it do it's job.
     *
     * <p>
     * Ok, to be a more specific, here is a short briefing, what is
     * done to the grammar:
     * </p>
     *
     * <p>
     * First of all, the grammar node is going to be walked along to
     * check it's attributes and children. If it has a "src" attribut
     * as well as an inline grammar an "error.badfetch" is thrown.
     * </p>
     *
     * <p>
     * External grammars referenced by the src attribute as well as
     * external rule expansions are going to be fetched. This is done
     * in a loop until all external grammars are loaded.<br>
     * While fetching all grammars one by one, the type and language
     * is going to be checked.<br>
     * If a unsupported grammar type is referenced an
     * "UnsupportedFormatError" is thrown.<br>
     * If a grammar for an unsupported language is fetched
     * "UnsupportedLanguageError" is thrown.
     * </p>
     *
     * @param context
     *        The current context.
     * @param attributes
     *        attributes governing the fetch.
     * @param grammar
     *        The grammar to process
     * @return the transformed grammar
     * @exception NoresourceError
     *         Error accessing the input device.
     * @exception UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @exception BadFetchError
     *         If the document could not be fetched successfully.
     * @exception SemanticError
     *         if there was an error evaluating a scripting expression
     */
    GrammarDocument process(
            final VoiceXmlInterpreterContext context,
                final FetchAttributes attributes,
                final Grammar grammar)
            throws NoresourceError, BadFetchError, UnsupportedFormatError,
                SemanticError;

}
