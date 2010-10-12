/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.interpreter.event;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyRepository;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.w3c.dom.NodeList;

/**
 * Executor for {@link TagStrategy}s.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */

public final class TagStrategyExecutor {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TagStrategyExecutor.class);

    /** The factory for tag strategies. */
    private final static TagStrategyRepository REPOSITORY;

    static {
        final JVoiceXmlConfiguration configuration
            = JVoiceXmlConfiguration.getInstance();
        REPOSITORY = configuration.loadObject(TagStrategyRepository.class);
        try {
            REPOSITORY.init(configuration);
        } catch (Exception e) {
            LOGGER.fatal(e.getMessage(), e);
        }
    }
    /**
     * Constructs a new object.
     * @throws Exception
     *         error loading the tag strategy repository
     */
    public TagStrategyExecutor() {
    }

    /**
     * Execute the {@link TagStrategy}s for all child nodes of the given
     * {@link FormItem}.
     * @param context the current VoiceXML interpreter context
     * @param interpreter the current VoiceXML interpreter
     * @param fia the current Form Interpretation Algorithm
     * @param formItem the current {@link FormItem}.
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     */
    public void executeChildNodes(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem)
            throws JVoiceXMLEvent {
        final VoiceXmlNode currentNode = formItem.getNode();
        final NodeList children = currentNode.getChildNodes();

        executeChildNodes(context, interpreter, fia, formItem, children);
    }

    /**
     * Execute the {@link TagStrategy}s for all child nodes of the given
     * parent node.
     *
     * @param context the current VoiceXML interpreter context
     * @param interpreter the current VoiceXML interpreter
     * @param fia the current Form Interpretation Algorithm
     * @param formItem
     *        The current {@link FormItem}.
     * @param parent
     *        The parent node, which is in fact a child to item.
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     *
     * @see org.jvoicexml.interpreter.TagStrategy
     */
    public void executeChildNodes(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final VoiceXmlNode parent)
            throws JVoiceXMLEvent {
        final NodeList children = parent.getChildNodes();

        executeChildNodes(context, interpreter, fia, formItem, children);
    }

    /**
     * Execute the {@link TagStrategy}s for all nodes of the given list.
     *
     * @param context the current VoiceXML interpreter context
     * @param interpreter the current VoiceXML interpreter
     * @param fia the current Form Interpretation Algorithm
     * @param formItem
     *        The current {@link FormItem}.
     * @param list
     *        The list of nodes to execute.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     *
     * @see org.jvoicexml.interpreter.TagStrategy
     */
    public void executeChildNodes(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final NodeList list)
            throws JVoiceXMLEvent {
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.getLength(); i++) {
            final VoiceXmlNode node = (VoiceXmlNode) list.item(i);
            executeTagStrategy(context, interpreter, fia, formItem, node);
        }
    }

    /**
     * Executes the {@link TagStrategy} for the given node.
     * @param context the current VoiceXML interpreter context
     * @param interpreter the current VoiceXML interpreter
     * @param fia the current Form Interpretation Algorithm
     * @param formItem the current {@link FormItem}
     * @param node the node to execute.
     * @throws JVoiceXMLEvent
     *            Error or event executing the child node.
     * @since 0.6
     */
    public void executeTagStrategy(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,  final FormItem formItem,
            final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final String namespace = node.getNamespaceURI();
        final URI uri;
        if (namespace == null) {
            uri = null;
        } else {
            try {
                uri = new URI(namespace);
            } catch (URISyntaxException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        }
        final TagStrategy strategy = REPOSITORY.getTagStrategy(node, uri);

        if (strategy == null) {
            return;
        }

        // Execute the node.
        strategy.getAttributes(context, node);
        strategy.evalAttributes(context);
        if (LOGGER.isDebugEnabled()) {
            strategy.dumpNode(node);
        }
        strategy.validateAttributes();
        strategy.execute(context, interpreter, fia, formItem, node);
    }

}
