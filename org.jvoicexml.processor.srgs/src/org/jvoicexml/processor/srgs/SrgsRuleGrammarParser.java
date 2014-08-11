/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.srgs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.processor.srgs.grammar.Rule;
import org.jvoicexml.processor.srgs.grammar.RuleAlternatives;
import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;
import org.jvoicexml.processor.srgs.grammar.RuleReference;
import org.jvoicexml.processor.srgs.grammar.RuleSequence;
import org.jvoicexml.processor.srgs.grammar.RuleSpecial;
import org.jvoicexml.processor.srgs.grammar.RuleTag;
import org.jvoicexml.processor.srgs.grammar.RuleToken;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A parser for SRGS grammars.
 * 
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1370 $
 */
public class SrgsRuleGrammarParser {

    private static EntityResolver entityResolver = new EmptyEntityResolver();
    private Map<String, String> attributes;

    public static class EmptyEntityResolver implements EntityResolver {
        @Override
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }

    public SrgsRuleGrammarParser() {
        attributes = new java.util.HashMap<String, String>();
    }

    public Rule[] load(final Reader reader) throws URISyntaxException {
        final InputSource source = new InputSource(reader);
        return load(source);
    }

    public Rule[] load(final InputStream stream) throws URISyntaxException {
        final InputSource source = new InputSource(stream);
        return load(source);
    }

    public Rule[] loadRule(final Reader reader) {
        try {
            final DocumentBuilder builder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            final InputSource source = new InputSource(reader);
            return parseGrammar(builder.parse(source));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rule[] loadRule(InputStream stream) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            return parseGrammar(builder.parse(new InputSource(stream)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Rule[] load(final InputSource inputSource)
            throws URISyntaxException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);

            final Document document = builder.parse(inputSource);
            final Node grammarNode = document.getFirstChild();

            final Rule[] rules = parseGrammar(grammarNode);

            // Extract header from grammar
            final NamedNodeMap docAttributes = grammarNode.getAttributes();
            for (int i = 0; i < docAttributes.getLength(); i++) {
                final Node node = docAttributes.item(i);
                attributes.put(node.getNodeName(), node.getNodeValue());
            }

            return rules;
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (SAXException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Rule[] parseGrammar(Node grammarNode) throws URISyntaxException {
        List<Rule> rules = new ArrayList<Rule>();
        NodeList childNodes = grammarNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node child = childNodes.item(i);
            final String nodeName = child.getNodeName();
            if (!nodeName.equalsIgnoreCase("rule")) {
                continue;
            }
            final NamedNodeMap attributes = child.getAttributes();
            final String ruleId = getAttribute(attributes, "id");
            int scope = Rule.PRIVATE;
            final String scopeStr = getAttribute(attributes, "scope");
            if (scopeStr != null) {
                if (scopeStr.equalsIgnoreCase("public")) {
                    scope = Rule.PUBLIC;
                }
            }

            final List<RuleComponent> components = evalChildNodes(child);
            if (components.size() == 1) {
                final Rule rule = new Rule(ruleId, components.get(0), scope);
                rules.add(rule);
            } else if (components.size() > 1) {
                final RuleSequence rs = new RuleSequence(
                        components.toArray(new RuleComponent[] {}));
                Rule rule = new Rule(ruleId, rs, scope);
                rules.add(rule);
            }
        }
        return rules.toArray(new Rule[] {});
    }

    private String getAttribute(NamedNodeMap attributes, String name) {
        Node attribute = attributes.getNamedItem(name);
        if (attribute == null) {
            return null;
        }
        return attribute.getNodeValue();
    }

    private RuleComponent evalNode(final Text node) {
        final String text = node.getWholeText().trim();
        if (text.length() == 0) {
            return null;
        }
        return new RuleToken(text);
    }

    private RuleComponent evalOneOf(final Node node) throws URISyntaxException {
        final List<RuleComponent> components = evalChildNodes(node);
        final RuleComponent[] alternatives = new RuleComponent[components
                .size()];
        components.toArray(alternatives);
        return new RuleAlternatives(alternatives);
    }

    private RuleComponent evalItem(final Node node) throws URISyntaxException {
        int repeatMin = -1;
        int repeatMax = -1;
        double repeatProb = -1;
        final NamedNodeMap attributes = node.getAttributes();
        final String repeatStr = getAttribute(attributes, "repeat");
        if (repeatStr != null) {
            int toIndex = repeatStr.indexOf('-');
            if (toIndex < 0) {
                repeatMin = Integer.parseInt(repeatStr);
                repeatMax = repeatMin;
            } else {
                String minStr = repeatStr.substring(0, toIndex);
                String maxStr = repeatStr.substring(toIndex + 1);
                if (minStr.trim().length() > 0) {
                    repeatMin = Integer.parseInt(minStr);
                }
                if (maxStr.trim().length() > 0) {
                    repeatMax = Integer.parseInt(maxStr);
                }
            }
        }

        final String repeatProbStr = getAttribute(attributes, "repeat-prob");
        if (repeatProbStr != null) {
            repeatProb = Double.parseDouble(repeatProbStr);
        }

        final List<RuleComponent> components = evalChildNodes(node);
        final RuleComponent component;
        if (components.size() == 1) {
            component = components.get(0);
        } else {
            final RuleComponent[] sequenceComponents = new RuleComponent[components
                    .size()];
            components.toArray(sequenceComponents);
            component = new RuleSequence(sequenceComponents);
        }
        if ((repeatMin != -1) && (repeatMax != -1) && (repeatProb != -1)) {
            return new RuleCount(component, repeatMin, repeatMax,
                    (int) (repeatProb * RuleCount.MAX_PROBABILITY));
        } else if ((repeatMin != -1) && (repeatMax != -1)) {
            return new RuleCount(component, repeatMin, repeatMax);
        } else if (repeatMin != -1) {
            if (repeatProb != -1) {
                return new RuleCount(component, repeatMin,
                        RuleCount.REPEAT_INDEFINITELY,
                        (int) (repeatProb * RuleCount.MAX_PROBABILITY));
            } else {
                return new RuleCount(component, repeatMin);
            }
        } else {
            return component;
        }
    }

    private RuleComponent evalReference(final Node node) throws URISyntaxException {
        final NamedNodeMap attributes = node.getAttributes();
        final String specialStr = getAttribute(attributes, "special");
        if (specialStr != null) {
            if (specialStr.equalsIgnoreCase("NULL")) {
                return RuleSpecial.NULL;
            } else if (specialStr.equalsIgnoreCase("VOID")) {
                return RuleSpecial.VOID;
            } else if (specialStr.equalsIgnoreCase("GARBAGE")) {
                return RuleSpecial.GARBAGE;
            }
        } else {
            final String uriStr = getAttribute(attributes, "uri");
            if (uriStr != null && uriStr.indexOf("#") == -1) {
                return new RuleReference(uriStr);
            } else if (uriStr != null) {
                final String ruleName = uriStr.substring(
                        uriStr.indexOf("#") + 1).trim();
                ;
                final String grammarName = uriStr.substring(0,
                        uriStr.indexOf("#"));
                final String typeStr = getAttribute(attributes, "type");
                if (grammarName.isEmpty()) {
                    return new RuleReference(ruleName);
                } else if (typeStr == null) {
                    final URI uri = new URI(grammarName);
                    return new RuleReference(uri, ruleName);
                } else {
                    final URI uri = new URI(grammarName);
                    return new RuleReference(uri, typeStr.trim());
                }
            }
        }
        return null;
    }

    private RuleComponent evalNode(final Node node) throws URISyntaxException {
        final String nodeName = node.getNodeName();
        if (nodeName.equalsIgnoreCase("#text")) {
            final Text textNode = (Text) node;
            return evalNode(textNode);
        } else if (nodeName.equalsIgnoreCase("one-of")) {
            return evalOneOf(node);
        } else if (nodeName.equalsIgnoreCase("item")) {
            return evalItem(node);
        } else if (nodeName.equalsIgnoreCase("ruleref")) {
            return evalReference(node);
        } else if (nodeName.equalsIgnoreCase("token")) {
            String tokenText = node.getTextContent();
            return new RuleToken(tokenText);
        } else if (nodeName.equalsIgnoreCase("tag")) {
            Object tagObject = node.getTextContent();
            return new RuleTag(tagObject);
        } else if (nodeName.equalsIgnoreCase("example")) {
            // Ignore
        }
        return null;
    }

    private List<RuleComponent> evalChildNodes(Node node)
            throws URISyntaxException {
        final List<RuleComponent> ruleComponents = new ArrayList<RuleComponent>();
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            final RuleComponent component = evalNode(child);
            if (component != null) {
                ruleComponents.add(component);
            }
        }

        return ruleComponents;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}
