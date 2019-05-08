/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.FormItemLocalExecutableTagContainer;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.VoiceXmlNode;
import org.w3c.dom.NodeList;

/**
 * Executor for {@link TagStrategy}s.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class TagStrategyExecutor {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(TagStrategyExecutor.class);

    /** The factory for tag strategies. */
    private final TagStrategyFactory factory;

    /**
     * Constructs a new object with the given profile.
     * 
     * @param profile the profile to use
     */
    public TagStrategyExecutor(final Profile profile) {
        factory = profile.getTagStrategyFactory();
    }

    /**
     * Execute the {@link TagStrategy}s for all child nodes of the given
     * {@link FormItem}.
     * 
     * @param context
     *            the current VoiceXML interpreter context
     * @param interpreter
     *            the current VoiceXML interpreter
     * @param fia
     *            the current Form Interpretation Algorithm
     * @param formItem
     *            the current {@link FormItem}.
     * @exception JVoiceXMLEvent
     *                Error or event executing the child node.
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
     * Execute the {@link TagStrategy}s for all child nodes of the given parent
     * node.
     *
     * @param context
     *            the current VoiceXML interpreter context
     * @param interpreter
     *            the current VoiceXML interpreter
     * @param fia
     *            the current Form Interpretation Algorithm
     * @param formItem
     *            The current {@link FormItem}.
     * @param parent
     *            The parent node, which is in fact a child to item.
     * @exception JVoiceXMLEvent
     *                Error or event executing the child node.
     *
     * @see org.jvoicexml.profile.TagStrategy
     */
    public void executeChildNodes(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final VoiceXmlNode parent) throws JVoiceXMLEvent {
        final NodeList children = parent.getChildNodes();

        executeChildNodes(context, interpreter, fia, formItem, children);
    }

    /**
     * Execute the {@link TagStrategy}s for all child nodes of the given parent
     * node.
     *
     * @param context
     *            the current VoiceXML interpreter context
     * @param interpreter
     *            the current VoiceXML interpreter
     * @param fia
     *            the current Form Interpretation Algorithm
     * @param formItem
     *            The current {@link FormItem}.
     * @param container
     *            the local form item container
     * @exception JVoiceXMLEvent
     *                Error or event executing the child node.
     *
     * @see org.jvoicexml.profile.TagStrategy
     */
    public void executeChildNodesLocal(
            final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final FormItemLocalExecutableTagContainer container)
            throws JVoiceXMLEvent {
        final Collection<VoiceXmlNode> nodes = container
                .getLocalExecutableTags();
        for (VoiceXmlNode node : nodes) {
            executeTagStrategy(context, interpreter, fia, formItem, node);
        }
    }

    /**
     * Execute the {@link TagStrategy}s for all nodes of the given list.
     *
     * @param context
     *            the current VoiceXML interpreter context
     * @param interpreter
     *            the current VoiceXML interpreter
     * @param fia
     *            the current Form Interpretation Algorithm
     * @param formItem
     *            The current {@link FormItem}.
     * @param list
     *            The list of nodes to execute.
     *
     * @exception JVoiceXMLEvent
     *                Error or event executing the child node.
     *
     * @see org.jvoicexml.profile.TagStrategy
     */
    public void executeChildNodes(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final NodeList list) throws JVoiceXMLEvent {
        if (list == null || list.getLength() == 0) {
            return;
        }
        
        // Execute the tag strategy per child node.
        for (int i = 0; i < list.getLength(); i++) {
            final VoiceXmlNode node = (VoiceXmlNode) list.item(i);
            executeTagStrategy(context, interpreter, fia, formItem, node);
        }
    }

    /**
     * Executes the {@link TagStrategy} for the given node.
     * 
     * @param context
     *            the current VoiceXML interpreter context
     * @param interpreter
     *            the current VoiceXML interpreter
     * @param fia
     *            the current Form Interpretation Algorithm
     * @param formItem
     *            the current {@link FormItem}
     * @param node
     *            the node to execute.
     * @throws JVoiceXMLEvent
     *             Error or event executing the child node.
     * @since 0.6
     */
    public void executeTagStrategy(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final TagStrategy strategy = prepareTagStrategyExecution(context, fia,
                node);
        if (strategy != null) {
            strategy.execute(context, interpreter, fia, formItem, node);
        }
    }

    /**
     * Prepares the execution of the {@link TagStrategy}.
     * 
     * @param context
     *            the current VoiceXML interpreter context
     * @param fia
     *            the current Form Interpretation Algorithm
     * @param node
     *            the node to execute.
     * @return tag strategy to execute
     * @throws ErrorEvent
     *             error preparing the execution of the tag strategy
     * @since 0.7.5
     */
    private TagStrategy prepareTagStrategyExecution(
            final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia, final VoiceXmlNode node)
            throws ErrorEvent {
        final TagStrategy strategy = factory.getTagStrategy(node);
        if (strategy == null) {
            return null;
        }

        // Prepare execution of the node.
        strategy.getAttributes(context, fia, node);
        strategy.evalAttributes(context);
        final DataModel model = context.getDataModel();
        if (LOGGER.isDebugEnabled()) {
            strategy.dumpNode(model, node);
        }
        strategy.validateAttributes(model);
        return strategy;
    }
}
