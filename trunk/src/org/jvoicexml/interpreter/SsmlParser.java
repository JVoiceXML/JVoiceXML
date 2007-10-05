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

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Prompt;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Parser to transform the contents of a <code>&lt;prompt&gt;</code> into an
 * SSML document.
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
    /** Factory for parsing strategies. */
    private static final SsmlParsingStrategyFactory FACTORY;

    /** The prompt to convert. */
    private final Prompt prompt;

    /** The VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** Scripting engine to evaluate scripting expressions. */
    private final ScriptingEngine scripting;

    static {
        FACTORY = new org.jvoicexml.interpreter.tagstrategy.
            JvoiceXmlSsmlParsingStrategyFactory();
    }

    /**
     * Constructs a new object.
     *
     * @param node
     *            The prompt.
     * @param interpreterContext
     *            The current VoiceXML interpreter context.
     */
    public SsmlParser(final Prompt node,
            final VoiceXmlInterpreterContext interpreterContext) {
        prompt = node;
        context = interpreterContext;
        scripting = context.getScriptingEngine();
    }

    /**
     * Retrieves the parsed SSML document.
     *
     * @return Parsed SSML document.
     * @exception ParserConfigurationException
     *                Error creating the empty document.
     * @exception SemanticError
     *                Error evaluating a scripting expression.
     */
    public SsmlDocument getDocument() throws ParserConfigurationException,
            SemanticError {
        final SsmlDocument document = new SsmlDocument();
        final SsmlNode parent = document.getSpeak();

        final Collection<VoiceXmlNode> children = prompt.getChildren();
        for (VoiceXmlNode node : children) {
            cloneChildNode(document, parent, node);
        }

        return document;
    }

    public void cloneNode(final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode node)
        throws SemanticError {
        if (parent == null) {
            return;
        }

        final Collection<VoiceXmlNode> children = node.getChildren();
        for (VoiceXmlNode child : children) {
            SsmlNode clonedNode = cloneChildNode(document, parent, child);
            cloneNode(document, clonedNode, child);
        }
    }

    /**
     * Performs a deep clone of the given node into the new SSML document.
     *
     * @param document
     *            The SSML document to create.
     * @param parent
     *            Current node in the new document.
     * @param node
     *            Child node of the original prompt.
     * @return Created node.
     * @exception SemanticError
     *                Error evaluating a scripting expression.
     */
    public SsmlNode cloneChildNode(final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode node)
        throws SemanticError {
        SsmlParsingStrategy strategy = FACTORY.getParsingStrategy(node);
        if (strategy != null) {
            strategy.getAttributes(context, node);
            strategy.evalAttributes(context);
            try {
                strategy.validateAttributes();
            } catch (SemanticError e) {
                throw e;
            } catch (ErrorEvent e) {
                throw new SemanticError(e);
            }

            return strategy.cloneNode(this, scripting, document, parent, node);
        }
        final String tag = node.getNodeName();

        // Append a clone of the current node.
        final SsmlNode clonedNode = (SsmlNode) parent.addChild(tag);
        if (clonedNode == null) {
            return null;
        }

        // Clone all attributes.
        cloneAttributes(document, node, clonedNode);

        return clonedNode;
    }


    /**
     * Clones the attributes of <code>node</code> into <code>clonedNode</code>.
     *
     * @param document
     *            The current document.
     * @param node
     *            The node to clone.
     * @param clonedNode
     *            The cloned node.
     * @exception SemanticError
     *                Error evaluating a scripting expression.
     */
    private void cloneAttributes(final SsmlDocument document,
            final XmlNode node, final XmlNode clonedNode) throws SemanticError {
        final NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            final Node attribute = attributes.item(i);

            String name = attribute.getNodeName();
            final String value = attribute.getNodeValue();
            clonedNode.setAttribute(name, value);
        }
    }
}
