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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.Application;
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

    /** The base URI to convert a given URI into a hierarchical URI. */
    private final URI baseUri;

    static {
        FACTORY = new org.jvoicexml.interpreter.tagstrategy.
            JvoiceXmlSsmlParsingStrategyFactory();
    }

    /**
     * Constructs a new object.
     *
     * @param node
     *            the prompt.
     * @param interpreterContext
     *            the current VoiceXML interpreter context.
     */
    public SsmlParser(final Prompt node,
            final VoiceXmlInterpreterContext interpreterContext) {
        prompt = node;
        context = interpreterContext;
        scripting = context.getScriptingEngine();
        URI uri;
        try {
            uri = prompt.getXmlBaseUri();
        } catch (URISyntaxException e) {
            uri = null;
        }
        baseUri = uri;
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

    /**
     * Clones all child nodes of the given node.
     * @param document the target document
     * @param parent the parent node in the new document.
     * @param node the node to clone.
     * @throws SemanticError
     *         Error evaluating the node to clone.
     */
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
        if ((parent == null) || (node == null)) {
            return null;
        }
        final SsmlParsingStrategy strategy = FACTORY.getParsingStrategy(node);
        final SsmlNode clonedNode;
        if (strategy != null) {
            strategy.getAttributes(context, node);
            strategy.evalAttributes(context);
            try {
                strategy.validateAttributes();
            } catch (SemanticError e) {
                // Catch the semantic error since this one is also an
                // ErrorEvent.
                throw e;
            } catch (ErrorEvent e) {
                throw new SemanticError(e);
            }

            clonedNode =
                strategy.cloneNode(this, scripting, document, parent, node);
        } else {
            // Copy the node.
            final String tag = node.getNodeName();
            clonedNode = (SsmlNode) parent.addChild(tag);

            // Clone all attributes.
            cloneAttributes(document, node, clonedNode);

        }

        if (clonedNode == null) {
            return null;
        }

        // Clone all child nodes.
        final Collection<VoiceXmlNode> children = node.getChildren();
        for (VoiceXmlNode child : children) {
            final SsmlNode clonedChild =
                cloneChildNode(document, clonedNode, child);
            if (clonedChild != null) {
                cloneChildNode(document, clonedChild, child);
            }
        }
        return null;
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

    /**
     * Converts the given <code>uri</code> into a hierarchical URI. If the given
     * <code>uri</code> is a relative URI, it is expanded using base uri.
     * @param uriString the URI to resolve.
     * @return Hierarchical URI.
     * @exception SemanticError
     *            Error resolving the uri.
     */
    public URI resolve(final String uriString) throws SemanticError {
        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new SemanticError(e.getMessage(), e);
        }
        final Application application = context.getApplication();
        if (application == null) {
            if (baseUri == null) {
                return uri;
            }
            return baseUri.resolve(uri);
        }
        return application.resolve(baseUri, uri);
    }
}
