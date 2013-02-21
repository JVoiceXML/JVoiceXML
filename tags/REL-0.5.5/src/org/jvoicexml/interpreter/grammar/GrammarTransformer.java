/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The <code>GrammarHandler</code> interface defines a couple of
 * methods to process a grammar document from a source type into a target type.
 *
 * <p>
 * Every implementation of this interface has a
 * <code>GrammarHandlerModeDesc</code> which describes the way a
 * certain input {@link org.jvoicexml.GrammarDocument} is processed and
 * converted to a {@link org.jvoicexml.GrammarImplementation}.
 * </p>
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
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
     *        The grammar document to transform.
     * @param type
     *        The target type of the grammar.
     *
     * @return The result of the transformation. A grammar
     *         representation which can be passed to an ASR engine.
     *
     * @exception NoresourceError
     *         Error creating a grammar from the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has been given.
     * @throws BadFetchError
     *         If the document could not be fetched successfully.
     */
    GrammarImplementation<? extends Object> createGrammar(final UserInput input,
            final GrammarDocument grammar,
            final GrammarType type)
               throws NoresourceError, UnsupportedFormatError, BadFetchError;

    /**
     * Returns the supported source media type.
     *
     * @return the supported source media type.
     *
     * @since 0.5.5
     */
    GrammarType getSourceType();

    /**
     * Returns the supported result media type.
     *
     * @return the supported result media type.
     */
    GrammarType getTargetType();
}
