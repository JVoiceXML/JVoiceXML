/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * This class implements the GrammarIdentifier interface. An instance
 * of this class is able to identify a JSGF grammar. The mime type of
 * the accepted grammar is <code>application/x-jsgf</code>.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision$
 */
public final class JsgfGrammarIdentifier
        implements GrammarIdentifier {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JsgfGrammarIdentifier.class);

    /** Common JSGGF header. **/
    private static final String JSGF_HEDAER = "#JSGF";

    /**
     * {@inheritDoc}
     *
     * A JSGF grammar must have a self identifying header
     * <code>#JSGF V1.0</code>.
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
        String document = grammar.getDocument();
        if (document.startsWith(JSGF_HEDAER)) {
            return GrammarType.JSGF;
        }
        // Overread a grammar node.
        int grammarStartPos = document.indexOf("<grammar");
        if (grammarStartPos >= 0) {
            int grammarEndPos = document.indexOf(">", grammarStartPos);
            document = document.substring(grammarEndPos + 1);
            document = document.trim();
        }
        int cdataStartPos = document.indexOf("<![CDATA[");
        if (cdataStartPos >= 0) {
            document = document.substring(cdataStartPos + "<![CDATA[".length());
            document = document.trim();
        }
        /*
         * cut grammar in pieces. Delimiter is ; followed by a
         * newline immediately
         */
        final StringTokenizer tok = new StringTokenizer(document, ";");
        if (!tok.hasMoreTokens()) {
            return null;
        }

        final String header = tok.nextToken();
        if (header.startsWith(JSGF_HEDAER)) {
            return GrammarType.JSGF;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSupportedType() {
        return GrammarType.JSGF;
    }
}
