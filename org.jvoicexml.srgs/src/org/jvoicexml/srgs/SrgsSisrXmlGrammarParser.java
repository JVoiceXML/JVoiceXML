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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.srgs.sisr.SemanticInterpretationBlock;
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

public class SrgsSisrXmlGrammarParser {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(SrgsSisrXmlGrammarParser.class);
    SrgsSisrGrammar currentGrammar;

    public SrgsSisrGrammar parse(SrgsXmlDocument document, URI uri)
            throws SrgsSisrParsingException {
        return parse(document, uri, new HashMap<URI, SrgsSisrGrammar>());
    }

    private SrgsSisrGrammar parse(SrgsXmlDocument document, URI uri,
            HashMap<URI, SrgsSisrGrammar> grammarPool)
            throws SrgsSisrParsingException {
        currentGrammar = new SrgsSisrGrammar(document.getGrammar(), uri,
                grammarPool);
        grammarPool.put(uri, currentGrammar); // Added early to minimize grammar
                                              // recursion problems

        // Load global tags and identify rules(not going to parse in the first
        // pass)
        Node childNode = document.getGrammar().getFirstChild();
        while (childNode != null) {
            if (childNode instanceof Tag) {
                currentGrammar.addGlobalTagContent(childNode.getTextContent());
            } else if (childNode instanceof Rule) {
                currentGrammar.addRule(new SrgsRule((Rule) childNode));
            } else if (childNode instanceof org.jvoicexml.xml.Text) {
                // ignore
            } else {
                LOGGER.warn("Found unknown node type: "
                        + childNode.getClass().getCanonicalName() + " "
                        + childNode.getTextContent());
            }

            childNode = childNode.getNextSibling();
        }

        // Now that all rules are known, we can safely parse and properly link
        // rulerefs as we go along
        childNode = document.getGrammar().getFirstChild();
        while (childNode != null) {
            if (childNode instanceof Rule) {
                SrgsRule grammarRule = currentGrammar.getRule(
                        ((Rule) childNode).getId(), false);
                parseRule(grammarRule, (Rule) childNode);
            }
            childNode = childNode.getNextSibling();
        }

        return currentGrammar;
    }

    private void parseRule(SrgsRule grammarRule, Rule node)
            throws SrgsSisrParsingException {
        RuleExpansion lastExpansion = null;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);
            if (childNode instanceof Item) {
                lastExpansion = parseItem((Item) childNode);
                grammarRule.setRule(lastExpansion);
            } else if (childNode instanceof OneOf) {
                lastExpansion = parseOneOf((OneOf) childNode);
                grammarRule.setRule(lastExpansion);
            } else if (childNode instanceof Ruleref) {
                lastExpansion = parseRuleref((Ruleref) childNode);
                grammarRule.setRule(lastExpansion);
            } else if (childNode instanceof Tag) {
                String textContent = childNode.getTextContent();
                if (currentGrammar.isLiteral())
                    textContent = "out='" + escapeSingleQuotes(textContent)
                            + "';";

                if (lastExpansion == null)
                    grammarRule.addInitialSI(textContent);
                else
                    lastExpansion.setExecutionSemanticInterpretation(new SemanticInterpretationBlock(textContent));
            } else if (childNode instanceof org.jvoicexml.xml.Text) {
                // ignore
            } else {

                LOGGER.warn("Unknown node type "
                        + childNode.getClass().getCanonicalName());
            }
        }

    }

    private String escapeSingleQuotes(String textContent) {
        return textContent.replace("'", "\\'");
    }

    private ItemRuleExpansion parseItem(Item itemNode)
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
                if (currentGrammar.isLiteral())
                    textContent = "out='" + escapeSingleQuotes(textContent)
                            + "';";

                if (lastExpansion == null)
                    item.appendInitialSI(textContent);
                else
                    lastExpansion.setExecutionSemanticInterpretation(new SemanticInterpretationBlock(textContent));
            } else if (childNode instanceof Token
                    || childNode instanceof org.jvoicexml.xml.Text) {
                String text = childNode.getTextContent().trim();
                if (text.length() == 0)
                    continue;
                lastExpansion = generateTextItem(text);
                item.addSubRule(lastExpansion);
            } else {
                LOGGER.warn("Unknown node type "
                        + childNode.getClass().getCanonicalName());
            }
        }

        return item;
    }

    private RuleExpansion generateTextItem(String textContent) {
        TokenRuleExpansion token = new TokenRuleExpansion(
                textContent.split(" "));

        return token;
    }

    private int safeIntParse(String s, int i) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            LOGGER.warn("Repeat string couldn't be parsed [" + s
                    + "] defaulting to " + i);
            return i;
        }
    }

    // Parse a one-of rule; recursing as needed
    private OneOfRuleExpansion parseOneOf(OneOf oneOfNode)
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
                if (currentGrammar.isLiteral())
                    textContent = "out='" + escapeSingleQuotes(textContent)
                            + "';";

                if (lastExpansion == null)
                    rule.addInitialSI(textContent);
                else
                    lastExpansion.setExecutionSemanticInterpretation(new SemanticInterpretationBlock(textContent));
            } else if (childNode instanceof Token) {
                String text = childNode.getTextContent().trim();
                if (text.length() == 0)
                    continue;
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

    private RuleRefExpansion parseRuleref(Ruleref ruleNode)
            throws SrgsSisrParsingException {
        String uri = ruleNode.getUri();
        if (uri == null || uri.length() == 0)
            throw new SrgsSisrParsingException(
                    "Ruleref is missing or has empty URI");
        int poundIdx = uri.indexOf('#');

        // Fragment references a public rule
        if (poundIdx > 0)
            return getExternalRuleRef(uri.substring(0, poundIdx),
                    uri.substring(poundIdx + 1));

        // Use default rule defined with root attribute
        if (poundIdx == -1)
            return getExternalRuleRef(uri, null);

        // Rule in current grammar
        String ruleName = uri.substring(1);
        SrgsRule rule = currentGrammar.getRule(ruleName, false);
        if (rule == null)
            throw new SrgsSisrParsingException("Unable to find rule ("
                    + ruleName + ") in current grammar.");

        return new RuleRefExpansion(rule);
    }

    private RuleRefExpansion getExternalRuleRef(String uriText, String ruleName)
            throws SrgsSisrParsingException {
        // Resolve to an absolute path
        URI uri = null;
        try {
            uri = currentGrammar.getUri().resolve(uriText);
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
        if (rule == null)
            throw new SrgsSisrParsingException(
                    "Unable to find a public rule with the name '" + ruleName
                            + "' in grammar " + uri);

        return new RuleRefExpansion(externalGrammar, rule);
    }

    private SrgsSisrGrammar fetchAndParseExternalGrammar(URI uri)
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

        return new SrgsSisrXmlGrammarParser().parse(doc, uri,
                currentGrammar.getGrammarPool());
    }

    public static String joinTokens(List<String> list, int start, int count) {
        if (count == 0)
            return "";

        StringBuffer sb = new StringBuffer();
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

}
