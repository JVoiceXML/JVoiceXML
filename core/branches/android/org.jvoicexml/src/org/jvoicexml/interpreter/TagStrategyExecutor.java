/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/TagStrategyExecutor.java $
 * Version: $LastChangedRevision: 2704 $
 * Date:    $Date: 2011-06-14 02:28:44 -0500 (mar, 14 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.Configurable;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.VoiceXmlNode;
import org.w3c.dom.NodeList;

/**
 * Executor for {@link TagStrategy}s.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2704 $
 * @since 0.7
 */

public final class TagStrategyExecutor implements Configurable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(TagStrategyExecutor.class);

    /** The factory for tag strategies. */
    private static TagStrategyRepository repository;

    /**
     * Constructs a new object.
     */
    public TagStrategyExecutor() {
    }


    /**
     * {@inheritDoc}
     * 
     * Loads all {@link org.jvoicexml.interpreter.TagStrategyFactory}s.
     */
    @Override
    public void init(final Configuration configuration)
        throws ConfigurationException {
        if (repository == null) {
            try {
                repository = configuration.loadObject(
                        TagStrategyRepository.class);
                repository.init(configuration);
            } catch (Exception e) {
                LOGGER.fatal(e.getMessage(), e);
            }
        }
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
     * Execute the {@link TagStrategy}s for all child nodes of the given
     * parent node.
     *
     * @param context the current VoiceXML interpreter context
     * @param interpreter the current VoiceXML interpreter
     * @param fia the current Form Interpretation Algorithm
     * @param formItem
     *        The current {@link FormItem}.
     * @param container
     *        the local form item container
     * @exception JVoiceXMLEvent
     *            Error or event executing the child node.
     *
     * @see org.jvoicexml.interpreter.TagStrategy
     */
    public void executeChildNodesLocal(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final FormItemLocalExecutableTagContainer container)
            throws JVoiceXMLEvent {
        final Collection<VoiceXmlNode> nodes =
            container.getLocalExecutableTags();
        for (VoiceXmlNode node : nodes) {
            final TagStrategy strategy = prepareTagStrategyExecution(
                    context, fia, node);
            if (strategy != null) {
                strategy.execute(context, interpreter, fia, formItem, node);
            }
        }
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
        final TagStrategy strategy = prepareTagStrategyExecution(context,
                fia, node);
        if (strategy != null) {
            strategy.execute(context, interpreter, fia, formItem, node);
        }
    }


    /**
     * Prepares the execution of the {@link TagStrategy}.
     * @param context the current VoiceXML interpreter context
     * @param fia the current Form Interpretation Algorithm
     * @param node the node to execute.
     * @return tag strategy to execute
     * @throws ErrorEvent
     *         error preparing the execution of the tag strategy
     * @since 0.7.5
     */
    private TagStrategy prepareTagStrategyExecution(
            final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia, final VoiceXmlNode node)
            throws ErrorEvent {
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
        final TagStrategy strategy = repository.getTagStrategy(node, uri);
        if (strategy == null) {
            return null;
        }

        // Execute the node.
        strategy.getAttributes(context, fia, node);
        strategy.evalAttributes(context);
        if (LOGGER.isDebugEnabled()) {
            strategy.dumpNode(node);
        }
        strategy.validateAttributes();
        return strategy;
    }
}
