/*
 * File:    $RCSfile: SrgsAbnfGrammarTransformer.java,v $
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
package org.jvoicexml.interpreter.grammar.transformer;

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;

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
    /** Transfomer's type. */
    private static final String TYPE = "application/srgs";

    /**
     * Standard constructor to instantiate as much
     * <code>GrammarHandler</code> as you need.
     */
    public SrgsAbnfGrammarTransformer() {

    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar createGrammar(final UserInput input,
                                     final String grammar, final String type)
            throws NoresourceError, UnsupportedFormatError {
        if (!TYPE.equals(type)) {
            throw new UnsupportedFormatError();
        }

        final RuleGrammar ruleGrammar = input.newGrammar("testgrammar");

        return ruleGrammar;
    }

}
