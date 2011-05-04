/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/srgs/GrammarType.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 09:23:51 +0200 (Mi, 25 Aug 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Grammar implementation for Nuance compiled grammars.
 * 
 * @author Shuo Yang
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public final class NuanceDynagramBinaryGrammarImplementation
        implements GrammarImplementation<BinaryGrammar> {
    /** The binary grammar. */
    private final BinaryGrammar grammar;
    
    /**
     * Constructs a new object.
     * @param binary the binary grammar.
     */
    public NuanceDynagramBinaryGrammarImplementation(
            final BinaryGrammar binary) {
        grammar = binary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return GrammarType.GSL_BINARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return ModeType.VOICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryGrammar getGrammar() {
        return grammar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(final RecognitionResult result) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final GrammarImplementation<BinaryGrammar> other) {
        // TODO implement equals
        return true;
    }
}
