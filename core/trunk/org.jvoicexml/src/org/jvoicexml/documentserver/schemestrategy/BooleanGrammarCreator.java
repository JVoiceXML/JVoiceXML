/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

package org.jvoicexml.documentserver.schemestrategy;

import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;

/**
 * Creator for a boolean builtin grammar.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.1
 */
class BooleanGrammarCreator {
    /**
     * Creates the builtin grammar.
     * @param uri the URI for the builtin grammar to create.
     * @return created grammar
     * @exception BadFetchError
     *            error creating the grammar
     */
    public SrgsXmlDocument createGrammar(final URI uri) throws BadFetchError {
        final SrgsXmlDocument document;
        try {
            document = new SrgsXmlDocument();
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        final Grammar grammar = document.getGrammar();
        final String host = uri.getHost();
        final ModeType mode;
        try {
            mode = ModeType.valueOfAttribute(host);
        } catch (IllegalArgumentException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        grammar.setMode(mode);

        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("boolean");
        grammar.setRoot(rule.getId());
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item yes = oneof.appendChild(Item.class);
        yes.addText("yes");
        final Tag yesTag = yes.appendChild(Tag.class);
        yesTag.addText("true");
        final Item no = oneof.appendChild(Item.class);
        no.addText("no");
        final Tag noTag = no.appendChild(Tag.class);
        noTag.addText("false");
        return document;
    }
}
