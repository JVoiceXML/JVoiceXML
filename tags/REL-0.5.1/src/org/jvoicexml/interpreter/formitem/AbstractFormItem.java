/*
 * File:    $RCSfile: AbstractFormItem.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.mozilla.javascript.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Form items are the elements that can be visited in the main loop of the form
 * interpretation algorithm. Input items direct the FIA to gather a result for a
 * specific element. When the FIA selects a control item, the control item may
 * contain a block of procedural code to execute, or it may tell the FIA to set
 * up the initial prompt-and-collect for a mixed initiative form.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public abstract class AbstractFormItem
        implements FormItem {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractFormItem.class);

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
     *        The corresponding xml node in the VoiceXML document.
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
    public final Object getFormItemVariable() {
        final ScriptingEngine scripting = context.getScriptingEngine();
        try {
            return scripting.eval(name);
        } catch (SemanticError ignore) {
            // In this case, the form item variable is simply undefined.
        }

        return Context.getUndefinedValue();
    }

    /**
     * {@inheritDoc}
     */
    public void setFormItemVariable(final Object value) {
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public final String getExpr() {
        return node.getAttribute("expr");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelectable() {
        final Object result = getFormItemVariable();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking if selectable");
            LOGGER.debug("value of '" + name + "': '" + result + "'");
            LOGGER.debug("selectable: "
                         + (result == Context.getUndefinedValue()));
        }

        return result == Context.getUndefinedValue();
    }

    /**
     * {@inheritDoc}
     */
    public final VoiceXmlNode getNode() {
        return node;
    }

    /**
     * Selector for the current <code>VoiceXmlInterpreterContext</code>.
     *
     * @return Current <code>VoiceXmlInterpreterContext</code>.
     */
    protected final VoiceXmlInterpreterContext getContext() {
        return context;
    }

    /**
     * {@inheritDoc}
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
}