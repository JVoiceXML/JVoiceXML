/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation;

import org.jvoicexml.Configurable;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;

/**
 * The {@link ImplementationGrammarProcessor} is the main entry point for
 * grammar processing within the implementation platform.
 *
 * <p>
 * This class provides a lean method interface to process a grammar
 * in a VoiceXML file into a format that can be understood by the
 * implementation platforms.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Christoph Buente
 *
 * @version $Revision$
 *
 * @since 0.3
 */
public interface ImplementationGrammarProcessor extends Configurable {
    /**
     * Processes the given grammar into a format that can be understood by
     * the implementation platform.
     *
     * @param input
     *        The user input
     * @param grammar
     *        The grammar to process
     * @return the transformed grammar
     * @exception NoresourceError
     *         Error accessing the input device.
     * @exception UnsupportedFormatError
     *         If an unsupported grammar has to be processed.
     * @exception BadFetchError
     *         If the document could not be fetched successfully.
     */
    GrammarImplementation<?> process(final UserInput input,
                final GrammarDocument grammar)
            throws NoresourceError, BadFetchError, UnsupportedFormatError;

}
