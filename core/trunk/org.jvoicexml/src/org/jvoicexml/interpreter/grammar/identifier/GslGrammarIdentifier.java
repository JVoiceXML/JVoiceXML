/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/grammar/identifier/JsgfGrammarIdentifier.java $
 * Version: $LastChangedRevision: 2640 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar.identifier;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Grammar identifier for Nuance GSL grammars.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2640 $
 * @since 0.7.5
 */
public final class GslGrammarIdentifier
        implements GrammarIdentifier {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(GslGrammarIdentifier.class);

    /** Common JSGGF header. **/
    private static final String GSL_HEDAER = ";GSL2.0";

    /**
     * {@inheritDoc}
     *
     * A GSL grammar must have a self identifying header
     * <code>;GSL2.0</code>.
     *
     * @todo Evaluate encoding and version.
     */
    public GrammarType identify(final GrammarDocument grammar) {
        /* make sure grammar is neither null nor empty */
        if (grammar == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("grammar is null or empty");
            }
            return null;
        }
        if (!grammar.isAscii()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("can only handle ascii grammars");
            }
            return null;
        }
        final String document = grammar.getTextContent();
        if (document.startsWith(GSL_HEDAER)) {
            return GrammarType.GSL;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSupportedType() {
        return GrammarType.GSL;
    }
}
