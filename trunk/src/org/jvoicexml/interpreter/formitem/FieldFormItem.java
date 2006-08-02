/*
 * File:    $HeadURL$
 * Version: $Revision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group
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
import java.util.Iterator;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Prompt;

/**
 * An input item whose value is obtained via ASR or DTMF grammars.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class FieldFormItem
        extends InputItem {
    /** The shadow var container template. */
    private final static Class SHADOW_VAR_CONTAINER_TEMPLATE =
            FieldShadowVarContainer.class;

    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(FieldFormItem.class);

    /** The shadow var container for this filed. */
    private FieldShadowVarContainer shadowVarContainer;

    /**
     * Create a new field input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding xml node in the VoiceXML document.
     */
    public FieldFormItem(final VoiceXmlInterpreterContext context,
                         final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }

    /**
     * {@inheritDoc}
     */
    public EventHandler accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        return visitor.visitFieldFormItem(this);
    }

    /**
     * {@inheritDoc}
     *
     * Sets also the shadow variables.
     */
    @Override
    public void setFormItemVariable(final Object value) {
        super.setFormItemVariable(value);

        if (shadowVarContainer == null) {
            try {
                shadowVarContainer =
                        (FieldShadowVarContainer) createShadowVarContainer();
            } catch (SemanticError ex) {
                /** @todo Throw this exception. */
                LOGGER.warn("could not create shadow var container", ex);
            }
        }

        shadowVarContainer.setUtterance(value.toString());
    }

    /**
     * Sets the markname.
     * @param mark The name of the mark.
     *
     * @since 0.5
     */
    public void setMarkname(final String mark) {
        if (shadowVarContainer == null) {
            try {
                shadowVarContainer =
                        (FieldShadowVarContainer) createShadowVarContainer();
            } catch (SemanticError ex) {
                LOGGER.warn("could not create shadow var container", ex);
            }
        }

        shadowVarContainer.setMarkname(mark);
    }

    /**
     * Get the field belonging to this <code>FieldFormItem</code>.
     *
     * @return The related field or <code>null</code> if there is no field.
     */
    private Field getField() {
        final VoiceXmlNode node = getNode();

        if (node == null) {
            return null;
        }

        if (!(node instanceof Field)) {
            return null;
        }

        return (Field) node;
    }

    /**
     * Get all nested definitions of a <code>&lt;grammar&gt;</code>.
     *
     * @return Collection about all nested <code>&lt;grammar&gt;</code> tags.
     */
    public Collection<Grammar> getGrammars() {
        final Field field = getField();
        if (field == null) {
            return null;
        }

        return field.getChildNodes(Grammar.class);
    }

    /**
     * Get all nested <code>&lt;filled&gt;</code> elements.
     *
     * @return Collection about all nested <code>&lt;filled&gt;</code> tags.
     */
    public Collection<Filled> getFilledElements() {
        final Field field = getField();
        if (field == null) {
            return null;
        }

        return field.getChildNodes(Filled.class);
    }

    /**
     * Get the prompt belonging to this input field.
     *
     * @return Prompt to play, <code>null</code> if there is none.
     */
    public Prompt getPrompt() {
        final Field field = getField();
        if (field == null) {
            return null;
        }

        final Collection<Prompt> prompts = field.getChildNodes(Prompt.class);
        if (prompts.isEmpty()) {
            return null;
        }

        final Iterator<Prompt> iterator = prompts.iterator();
        return iterator.next();
    }

    /**
     * {@inheritDoc}
     */
    public Class getShadowVariableContainer() {
        return SHADOW_VAR_CONTAINER_TEMPLATE;
    }
}
