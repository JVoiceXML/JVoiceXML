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
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;

/**
 * Creator for a boolean builtin grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
class BooleanGrammarCreator extends AbstractGrammarCreator {
    /** Name of the parameter for <em>no</em>. */
    private static final String NO_PARAMETER_NAME = "n";

    /** Name of the parameter for <em>yes</em>. */
    private static final String YES_PARAMETER_NAME = "y";

    /** Name of the builtin type. */
    public static final String TYPE_NAME = "boolean";

    /**
     * {@inheritDoc}
     */
    public SrgsXmlDocument createGrammar(final URI uri) throws BadFetchError {
        final Map<String, String> parameters = getParameters(uri);
        final ModeType mode = getMode(uri);

        if (mode == ModeType.VOICE) {
            // TODO retrieve the value from a resource bundle
            parameters.put(YES_PARAMETER_NAME, "yes");
        } else {
            if (!parameters.containsKey(YES_PARAMETER_NAME)) {
                parameters.put(YES_PARAMETER_NAME, "1");
            }
        }
        if (mode == ModeType.VOICE) {
            // TODO retrieve the value from a resource bundle
            parameters.put(NO_PARAMETER_NAME, "no");
        } else {
            if (!parameters.containsKey(NO_PARAMETER_NAME)) {
                parameters.put(NO_PARAMETER_NAME, "2");
            }
        }
        return createGrammar(parameters, mode);
    }

    /**
     * Creates the grammar.
     * @param parameters current parameters
     * @param mode mode type of the grammar
     * @return created grammar
     * @throws BadFetchError
     *         error creating the grammar
     */
    private SrgsXmlDocument createGrammar(final Map<String, String> parameters,
            final ModeType mode) throws BadFetchError {
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
        grammar.setAttribute("xmlns", "http://www.w3.org/2001/06/grammar");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("boolean");
        grammar.setRoot(rule.getId());
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item yes = oneof.appendChild(Item.class);
        yes.addText(parameters.get(YES_PARAMETER_NAME));
        final Tag yesTag = yes.appendChild(Tag.class);
        yesTag.addText("true");
        final Item no = oneof.appendChild(Item.class);
        no.addText(parameters.get(NO_PARAMETER_NAME));
        final Tag noTag = no.appendChild(Tag.class);
        noTag.addText("false");
        return document;
    }
}
