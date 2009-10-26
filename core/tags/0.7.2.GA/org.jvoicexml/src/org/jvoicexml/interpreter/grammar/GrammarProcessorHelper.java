/*
 * File:    $RCSfile: GrammarProcessorHelper.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;

/**
 * The <code>GrammarProcessorHelper</code> provides several methods
 * to the GrammarProcessor class. These methods are external, to get
 * them tested with unit testing.
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
public final class GrammarProcessorHelper {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GrammarProcessorHelper.class);


    /**
     * This method checks, if the grammar is an external grammar or an
     * inline grammar.
     *
     * @param grammar
     *        the grammar to be analyzed.
     * @return true, if the grammar is external, else false
     * @throws BadFetchError
     *         Exactly one of "src", "srcexpr", or an inline grammar
     *         must be specified; otherwise, an error.badfetch event
     *         is thrown.
     */
    boolean isExternalGrammar(final Grammar grammar)
            throws BadFetchError {
        /*
         * Exactly one of "src", "srcexpr", or an inline grammar must
         * be specified; otherwise, an error.badfetch event is thrown.
         */

        /* now check if there is a "src" attribute */
        if (grammar.getSrc() != null) {
            /*
             * yes, there is. Now check, if there is any inline or
             * srcexp
             */
            if (grammar.getSrcexpr() != null) {
                /* this is an error. */
                throw new BadFetchError("It's not allowed to provide src "
                                        + "and srcexp attribute.");
            }
            /* ok, no srcexp attribut, let's check for inline grammar */
            if (grammar.hasChildNodes()) {
                /* this is an error */
                throw new BadFetchError("It's not allowed to provide src "
                                        + "attribute and an inline grammar.");
            }
            /* this grammar is external */
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This Grammar is externally "
                             + "referenced by a src attribute.");
            }
            return true;
            /*
             * no src attribute provided, now check if there is a
             * "srcexpr" attribute
             */
        } else if (grammar.getSrcexpr() != null) {
            /*
             * yes, there is. Now check, if there is any inline
             * grammar
             */
            if (grammar.hasChildNodes()) {
                /* this is an error */
                throw new BadFetchError("It's not allowed to provide srcexp "
                                        + "attribute and an inline grammar.");
            }
            /* this grammar is external */
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This Grammar is externally "
                             + "referenced by a srcexpr attribute.");
            }
            return true;
            /*
             * no src or srcexp attribute provided, now check if there
             * is an inline grammar
             */
        } else if (grammar.hasChildNodes()) {
            /*
             * yes, there is. So this grammar is not external
             */
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This Grammar is an inline grammar.");
            }

            return false;

        }

        /*
         * non of the required attributes is provided. This is an
         * error too.
         */
        throw new BadFetchError("Exactly one of src, srcexpr, or an "
                                + "inline grammar must be specified");
    }
}
