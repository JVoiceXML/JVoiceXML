/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/AbstractFormItem.java $
 * Version: $LastChangedRevision: 2715 $
 * Date:    $Date: 2011-06-21 12:23:54 -0500 (mar, 21 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.FormItemLocalExecutableTagContainer;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Property;
import org.mozilla.javascript.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base functionality of a {@link FormItem}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2715 $
 */
abstract class AbstractFormItem
        implements FormItem, FormItemLocalExecutableTagContainer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractFormItem.class);

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
     * Create a new form item.
     *
     * @param ctx
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding XML node in the VoiceXML document.
     */
    public AbstractFormItem(final VoiceXmlInterpreterContext ctx,
                            final VoiceXmlNode voiceNode) {
        node = voiceNode;
        context = ctx;

        name = FormItemNameFactory.getName(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object getFormItemVariable() {
        final ScriptingEngine scripting = context.getScriptingEngine();
        try {
            return scripting.eval(name + ";");
        } catch (SemanticError ignore) {
            // In this case, the form item variable is simply undefined.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("retrieved undefined form item variable");
            }
        }

        return Context.getUndefinedValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormItemVariable(final Object value) throws SemanticError {
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable(name, value);
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
    public final Object getExpression() throws SemanticError {
        final String expr = node.getAttribute("expr");
        final ScriptingEngine scripting = context.getScriptingEngine();
        return scripting.eval(expr + ";");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getCondition() throws SemanticError {
        final String condAttribute = node.getAttribute("cond");
        if (condAttribute == null) {
            return true;
        } else {
            final ScriptingEngine scripting = context.getScriptingEngine();
            final Object condResult = scripting.eval(condAttribute + ";");
            if (condResult == Context.getUndefinedValue()) {
                return false;
            } else {
                final Boolean bool = (Boolean) condResult;
                return bool.booleanValue();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelectable() throws SemanticError {
        final Object result = getFormItemVariable();
        final boolean cond = getCondition();
        final boolean selectable = ((result == Context.getUndefinedValue())
                || (result == null))
            && cond;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if selectable");
            LOGGER.debug("value of   '" + name + "': '" + result + "'");
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
     * @return all nested catch elements
     */
    public Collection<AbstractCatchElement> getCatchElements() {
        if (node == null) {
            return null;
        }

        final Collection<AbstractCatchElement> catches =
                new java.util.ArrayList<AbstractCatchElement>();
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof AbstractCatchElement) {
                final AbstractCatchElement catchElement =
                        (AbstractCatchElement) child;
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

        final Collection<VoiceXmlNode> nodes =
                new java.util.ArrayList<VoiceXmlNode>();
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
