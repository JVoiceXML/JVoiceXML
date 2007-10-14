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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.SrgsNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Token;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An instance of this class is able to transform a SRGS grammar with XML format
 * into RuleGrammarinstance.
 * The mime type of the accepted grammar is application/srgs+xml.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SrgsXml2JsgfGrammarTransformer
        implements GrammarTransformer {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SrgsXml2JsgfGrammarTransformer.class);

    /** Line delimiter. */
    private static final String LINE_SEPARATOR =
        System.getProperty("line.separator");

    /**
     * Standard constructor to instantiate as much
     * <code>GrammarTransformer</code> as you need.
     */
    public SrgsXml2JsgfGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSourceType() {
        return GrammarType.SRGS_XML;
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
            final UserInput input, final GrammarDocument grammar,
            final GrammarType type) throws NoresourceError,
            UnsupportedFormatError, BadFetchError {
        /* First make sure, the type is supported */
        if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new SRGS XML grammar");
        }

        // prepare a reader to read in the grammar string
        final StringReader reader = new StringReader(grammar.getDocument());

        // create a xml input source from the grammar
        final InputSource inputSource = new InputSource(reader);

        SrgsXmlDocument doc;
        try {
            doc = new SrgsXmlDocument(inputSource);
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }

        StringBuilder str = new StringBuilder();
        processGrammar(doc, str);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transformed JSGF grammar " + str);
        }

        final StringReader jsgfReader = new StringReader(str.toString());
        return input.loadGrammar(jsgfReader, GrammarType.JSGF);
    }

    /**
     * Processes all child nodes of the given node into the rule grammar.
     * @param node the node to parse.
     * @param str the grammar to create.
     */
    private void processChildNodes(final SrgsNode node,
            final StringBuilder str) {
        Collection<SrgsNode> children = node.getChildren();
        for (SrgsNode child : children) {
            if (child instanceof Rule) {
                final Rule rule = (Rule) child;
                processRule(rule, str);
            } else if (child instanceof Ruleref) {
                final Ruleref ruleref = (Ruleref) child;
                processRuleref(ruleref, str);
            } else if (child instanceof OneOf) {
                final OneOf oneof = (OneOf) child;
                processOneof(oneof, str);
            } else if (child instanceof Item) {
                final Item item = (Item) child;
                processItem(item, str);
            } else if (child instanceof Token) {
                final Token token = (Token) child;
                processToken(token, str);
            }
        }
    }

    /**
     * Transforms the <code>&lt;grammar&gt;</code> node of the given document.
     * @param document the document to transform.
     * @param str transformed JSGF grammar.
     */
    private void processGrammar(final SrgsXmlDocument document,
            final StringBuilder str) {
        final Grammar grammar = document.getGrammar();

        final String root = grammar.getRoot();

        str.append("#JSGF V1.0;");
        str.append(LINE_SEPARATOR);
        str.append(LINE_SEPARATOR);

        str.append("grammar ");
        str.append(root);
        str.append(";");
        str.append(LINE_SEPARATOR);
        str.append(LINE_SEPARATOR);

        processChildNodes(grammar, str);
    }

    /**
     * Transforms the <code>&lt;rule&gt;</code>.
     * @param rule the node to transform.
     * @param str transformed JSGF grammar.
     */
    private void processRule(final Rule rule, final StringBuilder str) {
        final String scope = rule.getScope();
        final String id = rule.getId();

        str.append(scope);
        str.append(" <");
        str.append(id);
        str.append("> = ");

        processChildNodes(rule, str);

        str.append(";");
        str.append(LINE_SEPARATOR);
        str.append(LINE_SEPARATOR);
    }

    /**
     * Transforms the <code>&lt;ruleref&gt;</code>.
     * @param ruleref the node to transform.
     * @param str transformed JSGF grammar.
     */
    private void processRuleref(final Ruleref ruleref,
            final StringBuilder str) {
        final String special = ruleref.getSpecial();
        str.append("<");
        if (special != null) {
            str.append(special);
        } else {
            final String uri = ruleref.getUri();
            final int refPos = uri.indexOf("#");
            if (refPos >= 0) {
                if (refPos > 0) {
                    LOGGER.warn("unable to process an exteranl rule '" + uri
                            + "'");
                }
                String ref = uri.substring(refPos + 1);
                str.append(ref);
            } else {
                // TODO How to evaluate a referenced grammar?
                LOGGER.warn("unable to process an exteranl rule '" + uri + "'");
                str.append(uri);
            }

        }
        str.append(">");
    }

    /**
     * Transforms the <code>&lt;oneof&gt;</code>.
     * @param oneof the node to transform.
     * @param str transformed JSGF grammar.
     */
    private void processOneof(final OneOf oneof, final StringBuilder str) {
        boolean addedItem = false;


        Collection<Item> items = oneof.getChildNodes(Item.class);
        if (items.size() > 1) {
            str.append("(");
        }

        for (Item item : items) {
            if (addedItem) {
                str.append(" | ");
            }
            processItem(item, str);
            addedItem = true;
        }

        if (items.size() > 1) {
            str.append(")");
        }
    }

    /**
     * Transforms the <code>&lt;item&gt;</code>.
     * @param item the node to transform.
     * @param str transformed JSGF grammar.
     */
    private void processItem(final Item item, final StringBuilder str) {
        String text = item.getFirstLevelTextContent();
        text = text.trim();

        if (item.isOptional()) {
            str.append("[");
        }

        str.append(text);

        processChildNodes(item, str);

        if (item.isOptional()) {
            str.append("]");
        }
    }

    /**
     * Transforms the <code>&lt;token&gt;</code>.
     * @param token the node to transform.
     * @param str transformed JSGF grammar.
     */
    private void processToken(final Token token, final StringBuilder str) {
        String text = token.getFirstLevelTextContent();
        text = text.trim();

        str.append(text);
    }
}
