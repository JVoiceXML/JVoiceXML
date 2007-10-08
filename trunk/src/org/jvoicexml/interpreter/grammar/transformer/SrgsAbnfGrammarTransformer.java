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

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * This class implements the GrammarTransformer interface. An instance
 * of this class is able to transform a SRGS grammar with ABNF format
 * into RuleGrammar instance. The mime type of the accepted grammar is
 * application/srgs.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @see org.jvoicexml.interpreter.grammar.GrammarTransformer
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SrgsAbnfGrammarTransformer
        implements GrammarTransformer {
    /**
     * Standard constructor to instantiate as much
     * <code>GrammarHandler</code> as you need.
     */
    public SrgsAbnfGrammarTransformer() {

    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSourceType() {
        return GrammarType.SRGS_ABNF;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getTargetType() {
        return GrammarType.JSGF;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<? extends Object> createGrammar(
                final UserInput input,
                final GrammarDocument grammar,
                final GrammarType type)
            throws NoresourceError, UnsupportedFormatError {
        if (type != GrammarType.SRGS_ABNF) {
            throw new UnsupportedFormatError();
        }

        return input.newGrammar("testgrammar", GrammarType.SRGS_ABNF);
    }
}
