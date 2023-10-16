/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.srgs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.implementation.grammar.GrammarParser;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.srgs.Tag;
import org.jvoicexml.xml.srgs.Token;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A parser of SRGS XML formatted grammars. This parser is not thread-safe and
 * will parse only one grammar at a time.
 * 
 * @author Jim Rush
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class SrgsSisrXmlGrammarParser
    implements GrammarParser<SrgsXmlDocument> {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(SrgsSisrXmlGrammarParser.class);

    /** The currently parsed grammar. */
    private SrgsSisrGrammar currentGrammar;

    /** Special Rules as per https://www.w3.org/TR/speech-grammar/#S2.2.3. */
    private Map<String, SrgsRule> specialRules;
    
    /**
     * Constructs a new object.
     */
    public SrgsSisrXmlGrammarParser() {
        specialRules = new java.util.HashMap<String, SrgsRule>();
        final SrgsRule voidRule = new VoidRule();
        specialRules.put(Ruleref.SPECIAL_VALUE_VOID, voidRule);
        final SrgsRule nullRule = new NullRule();
        specialRules.put(Ruleref.SPECIAL_VALUE_NULL, nullRule);
        final SrgsRule garbageRule = new GarbageRule();
        specialRules.put(Ruleref.SPECIAL_VALUE_GARBAGE, garbageRule);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarEvaluator parse(final GrammarDocument document)
            throws IOException, UnsupportedFormatError {
        final byte[] buffer = document.getBuffer();
        final InputStream stream = new ByteArrayInputStream(buffer);
        final InputSource source = new InputSource(stream);
        try {
            final SrgsXmlDocument grammar = new SrgsXmlDocument(source);
            final URI uri = document.getURI();
            return parse(grammar, uri);
        } catch (ParserConfigurationException | SAXException
                | SrgsSisrParsingException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public SrgsSisrGrammar parse(final SrgsXmlDocument document, final URI uri)
            throws SrgsSisrParsingException {
        final Map<URI, SrgsSisrGrammar> pool =
                new java.util.HashMap<URI, SrgsSisrGrammar>();
        return parse(document, uri, pool);
    }

    private SrgsSisrGrammar parse(final SrgsXmlDocument document, final URI uri,
            final Map<URI, SrgsSisrGrammar> grammarPool)
            throws SrgsSisrParsingException {
        final Grammar grammar = document.getGrammar();
        currentGrammar = new SrgsSisrGrammar(grammar, uri, grammarPool);
        grammarPool.put(uri, currentGrammar); // Added early to minimize grammar
                                              // recursion problems

        // Load global tags and identify rules(not going to parse in the first
        // pass)
        Node child = document.getGrammar().getFirstChild();
        while (child != null) {
            if (child instanceof Tag) {
                final String text = child.getTextContent();
                currentGrammar.addGlobalTagContent(text);
            } else if (child instanceof Rule) {
                final Rule rule = (Rule) child;
                final SrgsRule srgsrule = new SrgsRule(rule);
                currentGrammar.addRule(srgsrule);
            } else if (child instanceof org.jvoicexml.xml.Text) {
                // ignore
            } else {
                LOGGER.warn("Found unknown node type: "
                        + child.getClass().getCanonicalName() + " "
                        + child.getTextContent());
            }

            child = child.getNextSibling();
        }

        // Now that all rules are known, we can safely parse and properly link
        // rulerefs as we go along
        child = document.getGrammar().getFirstChild();
        while (child != null) {
            if (child instanceof Rule) {
                final Rule rule = (Rule) child;
                final String id = rule.getId();
                final SrgsRule grammarRule = currentGrammar.getRule(
                        id, false);
                parseRule(grammarRule, rule);
            }
            child = child.getNextSibling();
        }

        return currentGrammar;
    }

    private void parseRule(final SrgsRule grammarRule, final Rule node)
            throws SrgsSisrParsingException {
        RuleExpansion lastExpansion = null;

        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof Item) {
                final Item item = (Item) child;
                lastExpansion = parseItem(item);
                grammarRule.setRule(lastExpansion);
            } else if (child instanceof OneOf) {
                final OneOf oneof = (OneOf) child;
                lastExpansion = parseOneOf(oneof);
                grammarRule.setRule(lastExpansion);
            } else if (child instanceof Ruleref) {
                final Ruleref ruleref = (Ruleref) child;
                lastExpansion = parseRuleref(ruleref);
                grammarRule.setRule(lastExpansion);
            } else if (child instanceof Tag) {
                String textContent = child.getTextContent();
                if (currentGrammar.isLiteral()) {
                    textContent = "out='" + escapeSingleQuotes(textContent)
                            + "';";
                }
                if (lastExpansion == null) {
                    grammarRule.addInitialSemanticInterpretation(textContent);
                } else {
                    final SemanticInterpretationBlock interpretation =
                            new SemanticInterpretationBlock(textContent);
                    lastExpansion
                            .setExecutionSemanticInterpretation(interpretation);
                }
            } else if (child instanceof org.jvoicexml.xml.Text) {
                // ignore
            } else {
                LOGGER.warn("Unknown node type "
                        + child.getClass().getCanonicalName());
            }
        }
    }

    private String escapeSingleQuotes(final String textContent) {
        return textContent.replace("'", "\\'");
    }

    private ItemRuleExpansion parseItem(final Item itemNode)
            throws SrgsSisrParsingException {
        ItemRuleExpansion item = new ItemRuleExpansion();

        // Determine repeat
        String repeat = itemNode.getRepeat();
        if (repeat != null && repeat.length() > 0) {
            repeat = repeat.trim();
            int dashIdx = repeat.indexOf('-');
            if (dashIdx == -1) { // fixed count
                int cnt = safeIntParse(repeat, 1);
                item.setRepeat(cnt, cnt);
            } else if (dashIdx == repeat.length() - 1) { // endless max
                int cnt = safeIntParse(repeat.substring(0, dashIdx), 1);
                item.setRepeat(cnt, -1);
            } else {
                int min = safeIntParse(repeat.substring(0, dashIdx), 1);
                int max = safeIntParse(repeat.substring(dashIdx + 1), 1);
                item.setRepeat(min, max);
            }
        }

        RuleExpansion lastExpansion = null;
        NodeList children = itemNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode instanceof Item) {
                lastExpansion = parseItem((Item) childNode);
                item.addSubRule(lastExpansion);
            } else if (childNode instanceof OneOf) {
                lastExpansion = parseOneOf((OneOf) childNode);
                item.addSubRule(lastExpansion);
            } else if (childNode instanceof Ruleref) {
                lastExpansion = parseRuleref((Ruleref) childNode);
                item.addSubRule(lastExpansion);
            } else if (childNode instanceof Tag) {
                String textContent = childNode.getTextContent();
                if (currentGrammar.isLiteral()) {
                    textContent = "out='" + escapeSingleQuotes(textContent)
                            + "';";
                }

                if (lastExpansion == null) {
                    item.appendInitialSI(textContent);
                } else {
                    lastExpansion.setExecutionSemanticInterpretation(
                            new SemanticInterpretationBlock(
                                    textContent));
                }
            } else if (childNode instanceof Token
                    || childNode instanceof org.jvoicexml.xml.Text) {
                String text = childNode.getTextContent().trim();
                if (text.length() == 0) {
                    continue;
                }
                lastExpansion = generateTextItem(text);
                item.addSubRule(lastExpansion);
            } else {
                LOGGER.warn("Unknown node type "
                        + childNode.getClass().getCanonicalName());
            }
        }

        return item;
    }

    private RuleExpansion generateTextItem(final String textContent) {
        TokenRuleExpansion token = new TokenRuleExpansion(
                textContent.split(" "));

        return token;
    }

    private int safeIntParse(final String s, final int i) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            LOGGER.warn("Repeat string couldn't be parsed [" + s
                    + "] defaulting to " + i);
            return i;
        }
    }

    // Parse a one-of rule; recursing as needed
    private OneOfRuleExpansion parseOneOf(final OneOf oneOfNode)
            throws SrgsSisrParsingException {
        OneOfRuleExpansion rule = new OneOfRuleExpansion();

        // Process each of the children
        RuleExpansion lastExpansion = null;
        NodeList children = oneOfNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode instanceof Item) {
                lastExpansion = parseItem((Item) childNode);
                rule.addSubRule(lastExpansion);
            } else if (childNode instanceof OneOf) {
                lastExpansion = parseOneOf((OneOf) childNode);
                rule.addSubRule(lastExpansion);
            } else if (childNode instanceof Ruleref) {
                lastExpansion = parseRuleref((Ruleref) childNode);
                rule.addSubRule(lastExpansion);
            } else if (childNode instanceof Tag) {
                String textContent = childNode.getTextContent();
                if (currentGrammar.isLiteral()) {
                    textContent = "out='" + escapeSingleQuotes(textContent)
                            + "';";
                }
                if (lastExpansion == null) {
                    rule.addInitialSI(textContent);
                } else {
                    lastExpansion.setExecutionSemanticInterpretation(
                            new SemanticInterpretationBlock(
                                    textContent));
                }
            } else if (childNode instanceof Token) {
                String text = childNode.getTextContent().trim();
                if (text.length() == 0) {
                    continue;
                }
                lastExpansion = generateTextItem(text);
                rule.addSubRule(lastExpansion);
            } else if (childNode instanceof org.jvoicexml.xml.Text) {
                // ignore text children as I don't believe they are valid in
                // SRSG (DTD seems to allow it)
            } else {
                LOGGER.warn("Unknown node type "
                        + childNode.getClass().getCanonicalName());
            }
        }

        return rule;
    }

    private RuleRefExpansion parseRuleref(final Ruleref ruleNode)
            throws SrgsSisrParsingException {
        final String special = ruleNode.getSpecial();
        if (special != null) {
            final SrgsRule rule = specialRules.get(special);
            if (rule == null) {
                throw new SrgsSisrParsingException(
                        "Ruleref is referencing an unknown special '"
                                + special + "'");
            }
            return new RuleRefExpansion(rule);
        }
        final String uri = ruleNode.getUri();
        if (uri == null || uri.length() == 0) {
            throw new SrgsSisrParsingException(
                    "Ruleref is missing or has empty URI");
        }
        int poundIdx = uri.indexOf('#');

        // Fragment references a public rule
        if (poundIdx > 0) {
            return getExternalRuleRef(uri.substring(0, poundIdx),
                    uri.substring(poundIdx + 1));
        }

        // Use default rule defined with root attribute
        if (poundIdx == -1) {
            return getExternalRuleRef(uri, null);
        }

        // Rule in current grammar
        final String ruleName = uri.substring(1);
        final SrgsRule rule = currentGrammar.getRule(ruleName, false);
        if (rule == null) {
            throw new SrgsSisrParsingException("Unable to find rule ("
                    + ruleName + ") in current grammar.");
        }
        return new RuleRefExpansion(rule);
    }

    private RuleRefExpansion getExternalRuleRef(final String uriText, 
            final String ruleName) throws SrgsSisrParsingException {
        // Resolve to an absolute path
        URI uri = null;
        try {
            uri = currentGrammar.getURI().resolve(uriText);
        } catch (IllegalArgumentException e) {
            throw new SrgsSisrParsingException(
                    "Unable to parse external rule ref: " + uriText, e);
        }

        // Look to see if it is already loaded
        SrgsSisrGrammar externalGrammar = currentGrammar
                .getGrammarFromPool(uri);
        if (externalGrammar == null) {
            externalGrammar = fetchAndParseExternalGrammar(uri);
        }

        SrgsRule rule = externalGrammar.getRule(ruleName, true);
        if (rule == null) {
            throw new SrgsSisrParsingException(
                    "Unable to find a public rule with the name '" + ruleName
                            + "' in grammar " + uri);
        }
        return new RuleRefExpansion(externalGrammar, rule);
    }

    private SrgsSisrGrammar fetchAndParseExternalGrammar(final URI uri)
            throws SrgsSisrParsingException {
        final SrgsXmlDocument doc;
        try {
            final URL url = uri.toURL();
            final InputStream input = url.openStream();
            final InputSource inputSource = new InputSource(input);
            doc = new SrgsXmlDocument(inputSource);
        } catch (IllegalArgumentException e) {
            throw new SrgsSisrParsingException(
                    "IllegalArgument exception processing grammar: " + uri
                            + " " + e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new SrgsSisrParsingException(
                    "Parser Configuration exception processing grammar: " + uri
                            + " " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new SrgsSisrParsingException(
                    "XML parsing exception processing grammar: " + uri + " "
                            + e.getMessage(), e);
        } catch (IOException e) {
            throw new SrgsSisrParsingException(
                    "IO exception processing grammar: " + uri + " "
                            + e.getMessage(), e);
        }

        final SrgsSisrXmlGrammarParser parser = new SrgsSisrXmlGrammarParser();
        final Map<URI, SrgsSisrGrammar> pool =
                new java.util.HashMap<URI, SrgsSisrGrammar>();
        return parser.parse(doc, uri, pool);
    }

    public static String joinTokens(final List<String> list, final int start, 
            final int count) {
        if (count == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = start; i < start + count; i++) {
            if (first) {
                first = false;
            } else {
                sb.append(' ');
            }

            sb.append(list.get(i));
        }

        return sb.toString();
    }

    public URI resolve(final URI base, final URI uri) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolving URI '" + uri + "' using '" + base + "'");
        }

        if (uri == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("cannot resolve null");
            }
            return null;
        }
        final URI resolvedUri = base.resolve(uri);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolved to '" + resolvedUri + "'");
        }

        return resolvedUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarImplementation<SrgsXmlDocument> load(final URI uri)
            throws IOException {
        try {
            return fetchAndParseExternalGrammar(uri);
        } catch (SrgsSisrParsingException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
