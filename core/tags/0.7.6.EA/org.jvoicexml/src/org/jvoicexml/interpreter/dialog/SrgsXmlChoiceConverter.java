/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.interpreter.dialog;

import java.util.Collection;
import java.util.Iterator;

import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.Choice;

/**
 * A <code>&lt;choice&gt;</code> converter for the SRGS XML grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class SrgsXmlChoiceConverter implements ChoiceConverter {
    /**
     * {@inheritDoc}
     */
    @Override
    public ConvertedChoiceOption convertChoice(final Choice choice,
            final ModeType mode, final ConvertedChoiceOption converted) {
        final Collection<Grammar> grammars =
            choice.getChildNodes(Grammar.class);
        if (grammars.isEmpty()) {
            final String input;
            if (mode == ModeType.DTMF) {
                final String dtmf = choice.getDtmf();
                if (dtmf != null) {
                    input = dtmf.trim();
                } else {
                    input = null;
                }
            } else {
                final String choiceText = choice.getFirstLevelTextContent();
                input = choiceText.trim();
            }
            if (input != null && !input.isEmpty()) {
                converted.addAcceptedInput(input);
            }
        } else {
            final Iterator<Grammar> iterator = grammars.iterator();
            final Grammar grammar = iterator.next();
            converted.setGrammar(grammar);
        }
        return converted;
    }
}
