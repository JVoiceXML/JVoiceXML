/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jvoicexml.Application;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Prompt;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser to transform the contents of a <code>&lt;prompt&gt;</code> or
 * <code>&lt;audio&gt;</code> into an SSML document.
 *
 * <p>
 * The parser processes a <code>&lt;prompt&gt;</code> or
 * <code>&lt;audio&gt;</code> node and transforms it into
 * an SSML document. All scripting expressions are evaluated.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Prompt
 * @see org.jvoicexml.xml.ssml.SsmlDocument
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.5
 */
public final class SsmlParser {
    /** Factory for parsing strategies. */
    private static final SsmlParsingStrategyFactory FACTORY;

    /** The prompt to convert. */
    private final VoiceXmlNode node;

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
     * @param vxmlNode
     *            the prompt.
     * @param interpreterContext
     *            the current VoiceXML interpreter context.
     */
    public SsmlParser(final VoiceXmlNode vxmlNode,
            final VoiceXmlInterpreterContext interpreterContext) {
        node = vxmlNode;
        context = interpreterContext;
        scripting = context.getScriptingEngine();
        URI uri;
        if (node instanceof Prompt) {
            final Prompt promptNode = (Prompt) node;
            try {
                uri = promptNode.getXmlBaseUri();
            } catch (URISyntaxException e) {
                uri = null;
            }
        } else {
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
        if (node instanceof Audio) {
            cloneChildNode(document, parent, node);
        } else {
            final Collection<VoiceXmlNode> children = node.getChildren();
            for (VoiceXmlNode current : children) {
                cloneChildNode(document, parent, current);
            }
        }

        // Perform a null transformation to remove splitted text passages.
        // These passages may occur e.g. if values are resolved.
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final Result result = new StreamResult(buffer);
        try {
            final TransformerFactory transformerFactory =
                TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final String encoding = System.getProperty("jvoicexml.xml.encoding",
                "UTF-8");
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            final Source source = new DOMSource(document.getSpeak());
            transformer.transform(source, result);
            final ByteArrayInputStream stream =
                new ByteArrayInputStream(buffer.toByteArray());
            final InputSource inputSource = new InputSource(stream);
            return new SsmlDocument(inputSource);
        } catch (TransformerException e) {
            throw new SemanticError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new SemanticError(e.getMessage(), e);
        } catch (IOException e) {
            throw new SemanticError(e.getMessage(), e);
        }
    }

    /**
     * Clones all child nodes of the given node.
     * @param document the target document
     * @param parent the parent node in the new document.
     * @param vxmlNode the node to clone.
     * @throws SemanticError
     *         Error evaluating the node to clone.
     */
    public void cloneNode(final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode vxmlNode)
        throws SemanticError {
        if (parent == null) {
            return;
        }

        final Collection<VoiceXmlNode> children = vxmlNode.getChildren();
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
     * @param vxmlNode
     *            Child node of the original prompt.
     * @return Created node.
     * @exception SemanticError
     *                Error evaluating a scripting expression.
     */
    public SsmlNode cloneChildNode(final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode vxmlNode)
        throws SemanticError {
        if ((parent == null) || (vxmlNode == null)) {
            return null;
        }
        final SsmlParsingStrategy strategy =
            FACTORY.getParsingStrategy(vxmlNode);
        final SsmlNode clonedNode;
        if (strategy != null) {
            strategy.getAttributes(context, vxmlNode);
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
                strategy.cloneNode(this, scripting, document, parent, vxmlNode);
        } else {
            // Copy the node.
            final String tag = vxmlNode.getNodeName();
            clonedNode = (SsmlNode) parent.addChild(tag);

            // Clone all attributes.
            cloneAttributes(document, vxmlNode, clonedNode);

        }

        if (clonedNode == null) {
            return null;
        }

        // Clone all child nodes.
        final Collection<VoiceXmlNode> children = vxmlNode.getChildren();
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
     * @param vxmlNode
     *            The node to clone.
     * @param clonedNode
     *            The cloned node.
     * @exception SemanticError
     *                Error evaluating a scripting expression.
     */
    private void cloneAttributes(final SsmlDocument document,
            final XmlNode vxmlNode, final XmlNode clonedNode)
        throws SemanticError {
        final NamedNodeMap attributes = vxmlNode.getAttributes();
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
