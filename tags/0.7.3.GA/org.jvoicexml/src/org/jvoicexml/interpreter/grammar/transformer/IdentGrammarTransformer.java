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
package org.jvoicexml.interpreter.grammar.transformer;

import java.io.StringReader;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * A null transformer.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
public abstract class IdentGrammarTransformer
        implements GrammarTransformer {
    /**
     * Constructs a new object.
     */
    public IdentGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public final GrammarImplementation<?> createGrammar(
            final UserInput input, final GrammarDocument grammar,
            final GrammarType type) throws NoresourceError,
            UnsupportedFormatError, BadFetchError {
        final GrammarType sourceType = getSourceType();
        if (type != sourceType) {
            throw new UnsupportedFormatError("Grammar type must be "
                    + sourceType + " but was " + type);
        }

        // prepare a reader to read in the grammar string
        final GrammarType targetType = getTargetType();
        final StringReader reader = new StringReader(grammar.getDocument());
        final GrammarImplementation<?> impl;
        try {
            impl = input.loadGrammar(reader, targetType);
        } finally {
            reader.close();
        }
        return impl;
    }
}
