/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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
package org.jvoicexml.interpreter.grammar.transformer;

import java.io.StringReader;

import org.jvoicexml.TypedGrammar;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.jsapi10.RuleGrammarImplementation;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * This class implements the GrammarTransformer interface. An instance
 * of this class is able to transform a SRGS grammar with XML format
 * into RuleGrammar instance. The mime type of the accepted grammar is
 * application/x-jsgf.
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
public final class JsgfGrammarTransformer
        implements GrammarTransformer {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public TypedGrammar<RuleGrammarImplementation> createGrammar(
                final UserInput input,
                final String grammar,
                final GrammarType type)
            throws NoresourceError, UnsupportedFormatError, BadFetchError {
        if (type != GrammarType.JSGF) {
            throw new UnsupportedFormatError();
        }

        final StringReader reader = new StringReader(grammar);

        return input.loadGrammar(reader, GrammarType.JSGF);
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSupportedType() {
        return GrammarType.JSGF;
    }

}
