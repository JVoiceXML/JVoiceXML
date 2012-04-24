/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.formitem;

import java.util.Collection;
import java.util.Locale;

import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Option;

/**
 * Converts a set of <code>&lt;option&gt;</code> nodes into a grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public interface OptionConverter {
    /**
     * Creates a voice grammar from the given options.
     * @param options the options to transfer into a voice grammar
     * @param language the language to use
     * @return created grammar, <code>null</code> if there is no voice grammar. 
     */
    Grammar createVoiceGrammar(final Collection<Option> options,
            final Locale language);

    /**
     * Creates a DTMF grammar from the given options.
     * @param options the options to transfer into a DTMF grammar
     * @param language the language to use
     * @return created grammar, <code>null</code> if there is no DTMF grammar. 
     */
    Grammar createDtmfGrammar(final Collection<Option> options,
            final Locale language);
}
