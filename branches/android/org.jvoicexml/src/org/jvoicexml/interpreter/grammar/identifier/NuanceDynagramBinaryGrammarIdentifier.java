/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/grammar/identifier/NuanceDynagramBinaryGrammarIdentifier.java $
 * Version: $LastChangedRevision: 2888 $
 * Date:    $Date: 2012-01-13 01:55:43 -0600 (vie, 13 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.grammar.identifier;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Grammar identifier for Nuance compiled grammars.
 * 
 * @author Shuo Yang
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2888 $
 * @since 0.7.5
 */
public final class NuanceDynagramBinaryGrammarIdentifier
    implements GrammarIdentifier {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(NuanceDynagramBinaryGrammarIdentifier.class);

    /** Hex values. */
    private static final String HEXES = "0123456789ABCDEF";

    /** GSL Binary grammar header. */
    private static final String EXPECTED_BINARY_HEADER = "EFA6A91500000200";

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType identify(final GrammarDocument grammar) {
        /* make sure grammar is not null nor empty */
        if (grammar == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Grammar is null or empty");
            }
            return null;
        }
        if (grammar.isAscii()) {
            return null;
        }
        final byte[] grammarInBytes = grammar.getBuffer();
        final boolean isAccepted = identify(grammarInBytes);
        if (!isAccepted) {
            return null;
        }
        return GrammarType.GSL_BINARY;
    }

    /**
     * Identifies the grammar.
     * @param grammarInBytes the grammar as a byte array.
     * @return <code>true</code> if the grammar could be identified.
     */
    private boolean identify(final byte[] grammarInBytes) {
        if (grammarInBytes == null) {
            return false;
        }
        final String realHeader = getFirstSeveralHexes(grammarInBytes,
                    EXPECTED_BINARY_HEADER.length());
        return realHeader.equals(EXPECTED_BINARY_HEADER);
    }


    /**
     * Retrieves the first bytes from the grammar as hex.
     * @param raw the raw bytes.
     * @param neededHexLength number of bytes to convert.
     * @return hex string of the first <code>neededHexLength</code> bytes
     */
    private String getFirstSeveralHexes(final byte[] raw,
            final int neededHexLength) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(neededHexLength);
        for (int i = 0; i < neededHexLength / 2; i++) {
            final byte b = raw[i];
            hex.append(HEXES.charAt((b & 0xF0) >> 4));
            hex.append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getSupportedType() {
        return GrammarType.GSL_BINARY;
    }
}
