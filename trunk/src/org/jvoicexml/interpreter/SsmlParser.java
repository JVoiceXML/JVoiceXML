/*
 * File:    $RCSfile: SsmlParser.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.interpreter;

import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Option;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Value;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser to transform the contents of a <code>&lt;prompt&gt;</code>
 * into an SSML document.
 *
 * <p>
 * The parser processes a <code>&lt;prompt&gt;</code> and transforms it into
 * an SSML document. All scripting expressions are evaluated.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Prompt
 * @see org.jvoicexml.xml.ssml.SsmlDocument
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public final class SsmlParser {
    /** The prompt to convert. */
    private final Prompt prompt;

    /** Scripting engine to evaluate scripting expressions. */
    private final ScriptingEngine scripting;

    /**
     * Constructs a new object.
     * @param node The prompt.
     * @param scriptingEngine The scripting engine to evaluate expressions.
     */
    public SsmlParser(final Prompt node,
                      final ScriptingEngine scriptingEngine) {
        prompt = node;
        scripting = scriptingEngine;
    }

    /**
     * Retrieves the parsed SSML document.
     * @return Parsed SSML document.
     * @exception ParserConfigurationException
     *             Error creating the empty document.
     * @exception SemanticError
     *            Error evaluating a scripting expression.
     */
    public SsmlDocument getDocument()
            throws ParserConfigurationException, SemanticError {
        final SsmlDocument document = new SsmlDocument();
        final XmlNode parent = document.getSpeak();

        final NodeList children = prompt.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final XmlNode node = (XmlNode) children.item(i);

            cloneNode(document, parent, node);
        }

        return document;
    }

    /**
     * Performs a deep clone of the given node into the new SSML document.
     * @param document The SSML document to create.
     * @param parent Current node in the new document.
     * @param node Child node of the original prompt.
     * @return Created node.
     * @exception SemanticError
     *            Error evaluating a scripting expression.
     */
    private XmlNode cloneNode(final SsmlDocument document, final XmlNode parent,
                              final XmlNode node)
            throws SemanticError {
        final String tag = node.getNodeName();
        if (Text.TAG_NAME.equalsIgnoreCase(tag)) {
            final String text = node.getNodeValue();

            return appendTextNode(document, parent, text);
        }

        if (Value.TAG_NAME.equalsIgnoreCase(tag)) {
            final Value value = (Value) node;

            final String expr = value.getExpr();
            final String text = scripting.eval(expr).toString();

            return appendTextNode(document, parent, text);
        }

        if (Enumerate.TAG_NAME.equalsIgnoreCase(tag)) {
            final Enumerate enumerate = (Enumerate) node;
            final Field field = enumerate.getField();
            if (field != null) {
                final Collection<Option> options =
                    field.getChildNodes(Option.class);
                final StringBuilder str = new StringBuilder();
                for (Option option : options) {
                    String text = option.getTextContent();
                    if (str.length() > 0) {
                        str.append(';');
                    }
                    str.append(text);
                }

                return appendTextNode(document, parent, str.toString());
            }
        }

        final XmlNode clonedNode = parent.addChild(tag);
        if (clonedNode == null) {
            return null;
        }

        cloneAttributes(document, node, clonedNode);

        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final XmlNode child = (XmlNode) children.item(i);

            cloneNode(document, clonedNode, child);
        }

        return clonedNode;
    }

    /**
     * Creates a text node as a child to the given parent node.
     * @param document The current docment.
     * @param parent The parent node.
     * @param text The text to add as a child to parent.
     * @return Created node.
     */
    private XmlNode appendTextNode(final SsmlDocument document,
                                   final XmlNode parent, final String text) {
        final Node textNode = document.createTextNode(text);
        parent.appendChild(textNode);

        return null;
    }

    /**
     * Clones the attributes of <code>node</code> into <code>clonedNode</code>.
     * @param document The current document.
     * @param node The node to clone.
     * @param clonedNode The cloned node.
     * @exception SemanticError
     *            Error evaluating a scripting expression.
     */
    private void cloneAttributes(final SsmlDocument document,
                                 final XmlNode node, final XmlNode clonedNode)
            throws SemanticError {
        final String tag = node.getNodeName();
        final boolean isAudio = Audio.TAG_NAME.equalsIgnoreCase(tag);

        final NamedNodeMap attributes = node.getAttributes();
        final NamedNodeMap clonedAttributes = clonedNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node attribute = attributes.item(i);

            String name = attribute.getNodeName();
            final String value;
            if (isAudio && name.equalsIgnoreCase(Audio.ATTRIBUTE_EXPR)) {
                name = Audio.ATTRIBUTE_SRC;

                final Audio audio = (Audio) node;
                final String expr = audio.getExpr();

                value = (String) scripting.eval(expr);
            } else {
                value = attribute.getNodeValue();
            }

            final Node item = document.createAttribute(name);
            item.setNodeValue(value);
            clonedAttributes.setNamedItem(item);
        }
    }
}
