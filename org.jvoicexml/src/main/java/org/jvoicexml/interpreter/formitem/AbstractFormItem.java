/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.formitem;

import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.FormItemLocalExecutableTagContainer;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Property;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base functionality of a {@link FormItem}.
 * <p>
 * Each {@link FormItem} has an associated variable with its name that can be
 * retrieved by {@link #getName()} in the {@link DataModel}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
abstract class AbstractFormItem
        implements FormItem, FormItemLocalExecutableTagContainer {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(AbstractFormItem.class);

    /** The current <code>VoiceXmlInterpreterContext</code>. */
    private final VoiceXmlInterpreterContext context;

    /** The corresponding xml node in the VoiceXML document. */
    private final VoiceXmlNode node;

    /**
     * The name of a dialog-scoped form item variable that will hold the name of
     * the form item.
     */
    private final String name;

    /**
     * Constructs a new form item as a template.
     */
    AbstractFormItem() {
        node = null;
        context = null;
        name = null;
    }

    /**
     * Create a new form item.
     *
     * @param ctx
     *            The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *            The corresponding XML node in the VoiceXML document.
     */
    AbstractFormItem(final VoiceXmlInterpreterContext ctx,
            final VoiceXmlNode voiceNode) {
        node = voiceNode;
        context = ctx;
        name = FormItemNameFactory.getName(node);
    }

    /**
     * Factory method to create a new instance from a template.
     * 
     * @param ctx
     *            The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *            The corresponding XML node in the VoiceXML document.
     * @return created form item
     * @since 0.7.6
     */
    public abstract AbstractFormItem newInstance(
            final VoiceXmlInterpreterContext ctx, final VoiceXmlNode voiceNode);

    /**
     * {@inheritDoc}
     * 
     * @return retrieves the value of the associated variable in the
     *         {@link DataModel}
     */
    @Override
    public final Object getFormItemVariable() {
        final DataModel model = context.getDataModel();
        try {
            return model.readVariable(name, Object.class);
        } catch (SemanticError ignore) {
            // In this case, the form item variable is simply undefined.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("retrieved undefined form item variable");
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int setFormItemVariable(final Object value) throws SemanticError {
        final DataModel model = context.getDataModel();
        return model.updateVariable(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object evaluateExpression(final DataModel model)
            throws SemanticError {
        final String expr = node.getAttribute("expr");
        if (expr == null) {
            return null;
        }
        final String unescapedExpr = StringEscapeUtils.unescapeXml(expr);
        return model.evaluateExpression(unescapedExpr, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluateCondition() throws SemanticError {
        final String condAttribute = node.getAttribute("cond");
        if (condAttribute == null) {
            return true;
        }
        final String unescapedCond = StringEscapeUtils
                .unescapeXml(condAttribute);
        final DataModel model = context.getDataModel();
        return model.evaluateExpression(unescapedCond, Boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelectable() throws SemanticError {
        final Object result = getFormItemVariable();
        final boolean cond = evaluateCondition();
        final DataModel model = context.getDataModel();
        final boolean selectable = ((result == model.getUndefinedValue()) 
                || (result == null)) && cond;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if '" + name +"' is selectable");
            final String logResult = model.toString(result);
            LOGGER.debug("value of   '" + name + "': '" + logResult + "'");
            LOGGER.debug("cond of    '" + name + "': '" + cond + "'");
            LOGGER.debug("selectable '" + name + "': " + selectable);
        }

        return selectable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final VoiceXmlNode getNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getNodeTagName() {
        if (node == null) {
            return null;
        }
        return node.getTagName();
    }

    /**
     * Retrieves the current {@link VoiceXmlInterpreterContext}.
     *
     * @return the current <code>VoiceXmlInterpreterContext</code>.
     */
    protected final VoiceXmlInterpreterContext getContext() {
        return context;
    }

    /**
     * Retrieves all nested catch elements.
     * 
     * @return all nested catch elements
     */
    public Collection<AbstractCatchElement> getCatchElements() {
        if (node == null) {
            return null;
        }

        final Collection<AbstractCatchElement> catches = new java.util.ArrayList<AbstractCatchElement>();
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof AbstractCatchElement) {
                final AbstractCatchElement catchElement = (AbstractCatchElement) child;
                catches.add(catchElement);
            }
        }

        return catches;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<VoiceXmlNode> getLocalExecutableTags() {
        if (node == null) {
            return null;
        }

        final Collection<VoiceXmlNode> nodes = new java.util.ArrayList<VoiceXmlNode>();
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof Property) {
                final VoiceXmlNode voiceXmlNode = (VoiceXmlNode) child;
                nodes.add(voiceXmlNode);
            }
        }

        return nodes;
    }
}
