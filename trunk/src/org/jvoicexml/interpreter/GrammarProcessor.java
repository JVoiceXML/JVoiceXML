/*
 * File:    $RCSfile: GrammarProcessor.java,v $
 * Version: $Revision $
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
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
 * @author Dirk Schnelle
 * @author Christoph Buente
 *
 * @version $Revision$
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface GrammarProcessor {
    /** Configuration key. */
    String CONFIG_KEY = "grammarprocessor";

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
     * <p>
     * When there are no more external rule expansions, it is time to
     * convert the grammar into a form, which can be passed to a JSAPI
     * compliant ASR Engine. JSAPI 1.0 specifies that any compliant
     * ASR engine must be able to process grammars in the JSGF Form.
     * </p>
     *
     * <p>
     * To provide support for a wide range of grammars, it is possible
     * to create your own grammar transformer. The appropriate
     * transformer for the actual grammar is selected from a list of
     * registered <code>GrammarTransformer</code>. The resulting
     * <code>RuleGrammar</code> object is wrapped in a
     * <code>Scopable</code> object. The object is passed into a
     * container to make sure, the grammar can be activated at the
     * right time.<br>
     * After this, the method returns.
     * </p>
     *
     * @param context
     *        The current context.
     * @param grammar
     *        The grammar to process
     * @param grammars
     *        The used grammar registry.
     * @exception NoresourceError
     *         Error accessing the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    void process(final VoiceXmlInterpreterContext context,
                 final Grammar grammar, final GrammarRegistry grammars)
            throws NoresourceError, BadFetchError, UnsupportedFormatError;

}
