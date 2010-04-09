/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy.builtin;

import java.net.URI;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Creator for a digit builtin grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
class DigitGrammarCreator extends AbstractGrammarCreator {
    /** The maximal digit. */
    private static final int MAX_DIGIT = 10;

    /** Name of the builtin type. */
    public static final String TYPE_NAME = "digit";

    /**
     * {@inheritDoc}
     */
    public SrgsXmlDocument createGrammar(final URI uri) throws BadFetchError {
        final ModeType mode = getMode(uri);
        final Map<String, String> parameters = getParameters(uri);
        final SrgsXmlDocument document;
        try {
            document = new SrgsXmlDocument();
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        final Grammar grammar = document.getGrammar();
        grammar.setMode(mode);
        if (mode == ModeType.VOICE) {
            grammar.setAttribute("xml:lang", "en");
        }
        grammar.setType(GrammarType.SRGS_XML);
        grammar.setVersion(Grammar.VERSION_1_0);
        final Rule digit = grammar.appendChild(Rule.class);
        digit.setId("digit");
        final OneOf oneof = digit.appendChild(OneOf.class);
        for (int i = 0; i < MAX_DIGIT; i++) {
            final Item item = oneof.appendChild(Item.class);
            item.addText(Integer.toString(i));
        }
        final Rule digits = grammar.appendChild(Rule.class);
        digits.setId("digits");
        final Item digitsItem = digits.appendChild(Item.class);
        final int length = getIntParameter(parameters, "length", -1);
        if (length < 0) {
            final int min = getIntParameter(parameters, "minlength", 1);
            final int max = getIntParameter(parameters, "maxlength", -1);
            try {
                digitsItem.setRepeat(min, max);
            } catch (IllegalArgumentException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        } else {
            try {
                digitsItem.setRepeat(length);
            } catch (IllegalArgumentException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        }
        final Ruleref ref = digitsItem.appendChild(Ruleref.class);
        ref.setUri(digit);
        grammar.setRoot(digits.getId());
        return document;
    }

    /**
     * Converts the parameter with the given name to an integer.
     * @param parameters all parameters
     * @param name name of the parameter
     * @param defValue default value
     * @return value of the parameter, <code>defValue</code> if there is no
     *         value
     * @since 0.7.1
     */
    private int getIntParameter(final Map<String, String> parameters,
            final String name, final int defValue) {
        final String value = parameters.get(name);
        if (value == null) {
            return defValue;
        }
        return Integer.parseInt(value);
    }
}
