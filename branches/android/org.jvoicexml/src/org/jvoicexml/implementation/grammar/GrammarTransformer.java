/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/GrammarTransformer.java $
 * Version: $LastChangedRevision: 2592 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.grammar;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The {@link GrammarTransformer} interface defines a couple of
 * methods to process a grammar document from a source type into a target type.
 *
 * <p>
 * Grammar transformations may result in loss of accuracy since the different
 * grammar formats may support only part of the features of the other grammar.
 * This is also the reason why there is no central intermediate grammar format.
 * </p>
 * 
 * <p>
 * Each implementation of this interface has a
 * <code>GrammarHandlerModeDesc</code> which describes the way a
 * certain input {@link org.jvoicexml.GrammarDocument} is processed and
 * converted to a {@link org.jvoicexml.implementation.GrammarImplementation}.
 * </p>
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision: 2592 $
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
    GrammarImplementation<?> transformGrammar(final UserInput input,
            final GrammarDocument grammar)
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
