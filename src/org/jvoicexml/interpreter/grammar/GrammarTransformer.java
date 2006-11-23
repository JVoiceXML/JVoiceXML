/*
 * File:    $RCSfile: GrammarTransformer.java,v $
 * Version: $Revision: 1.8 $
 * Date:    $Date: 2006/01/12 14:37:55 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;

/**
 * The <code>GrammarHandler</code> interface defines a couple of
 * methodes to process any kind of ASR grammar.
 *
 * <p>
 * Every implementation of this interface has a
 * <code>GrammarHandlerModeDesc</code> which describes the way a
 * certain input grammar is processed and converted to a JSGF
 * compatible grammar.
 * </p>
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $Revision: 1.8 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface GrammarTransformer {
    /**
     * Creates a JSGF compatible grammar object, which can be passed
     * to the ASR engine.
     *
     * @param input
     *        The current <code>UserInput</code> to create an empty
     *        grammar.
     * @param grammar
     *        A VoiceXML node containing a grammar in the
     *        <code>&lt;grammar&gt;</code> element.
     * @param type
     *        The type of the grammar.
     *
     * @return RuleGrammar The result of the processing. A grammar
     *         representation which can be passed to an ASR engine.
     *
     * @exception NoresourceError
     *         Error creating a grammar from the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has been given.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    RuleGrammar createGrammar(final UserInput input, final String grammar,
                              final String type)
            throws NoresourceError, UnsupportedFormatError, BadFetchError;

    /**
     * Returns the string representing the supported media type.
     *
     * @return a <code>String</code> representing the supported
     *         media type.
     */
    String getSupportedType();
}
