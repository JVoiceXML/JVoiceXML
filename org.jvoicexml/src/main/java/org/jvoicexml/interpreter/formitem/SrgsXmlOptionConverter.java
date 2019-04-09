/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.Option;

/**
 * An option converter for SRGS XML grammars. Grammars that are described
 * implicitly by a {@code <menu>} tag are expanded to an internal grammar
 * document.
 * @author Dirk Schnelle-Walka
 */
public final class SrgsXmlOptionConverter implements OptionConverter {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(SrgsXmlOptionConverter.class);

    /**
     * A sequence number to distinguish multiple grammars that are created
     * within a single millisecond.
     */
    private static long sequence = 0;

    /**
     * Creates a name for the root rule of a grammar.
     *
     * <p>
     * <code>
     * OG&lt;type&gt;&lt;Long.toHexString(System.currentTimeMillis())
     * &gt;S&lt;6-digit sequence number&gt;
     * </code>
     * </p>
     * @param type grammar type
     * @return Name for the root rule
     */
    private static synchronized String getName(final ModeType type) {
        // Simple algorithm to get an internal name.
        ++sequence;

        final String leadingZeros = "000000";
        String sequenceString = leadingZeros + Long.toHexString(sequence);
        sequenceString = sequenceString.substring(sequenceString.length()
                - leadingZeros.length());

        return "OG" + type + Long.toHexString(System.currentTimeMillis())
                + "S" + sequenceString;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grammar createVoiceGrammar(final Collection<Option> options,
            final Locale language) {
        if ((options == null) || options.isEmpty()) {
            return null;
        }
        try {
            final SrgsXmlDocument document = new SrgsXmlDocument();
            final Grammar grammar = document.getGrammar();
            grammar.setXmlLang(language);
            grammar.setType(GrammarType.SRGS_XML);
            final Rule rule = grammar.appendChild(Rule.class);
            final String name = getName(ModeType.VOICE);
            rule.setId(name);
            grammar.setRoot(rule);
            final OneOf oneof = rule.appendChild(OneOf.class);
            for (Option option : options) {
                final Item item = oneof.appendChild(Item.class);
                final String text = option.getTextContent();
                item.addText(text);
            }
            return grammar;
        } catch (ParserConfigurationException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grammar createDtmfGrammar(final Collection<Option> options) {
        if ((options == null) || options.isEmpty()) {
            return null;
        }
        try {
            final SrgsXmlDocument document = new SrgsXmlDocument();
            final Grammar grammar = document.getGrammar();
            grammar.setType(GrammarType.SRGS_XML);
            final Rule rule = grammar.appendChild(Rule.class);
            final String name = getName(ModeType.DTMF);
            rule.setId(name);
            grammar.setRoot(rule);
            final OneOf oneof = rule.appendChild(OneOf.class);
            boolean hasDtmf = false;
            for (Option option : options) {
                final String dtmf = option.getDtmf();
                if (dtmf != null) {
                    final Item item = oneof.appendChild(Item.class);
                    item.addText(dtmf);
                    hasDtmf = true;
                }
            }
            if (hasDtmf) {
                return grammar;
            } else {
                return null;
            }
        } catch (ParserConfigurationException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

}
