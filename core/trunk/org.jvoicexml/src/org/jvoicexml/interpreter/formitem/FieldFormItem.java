/*
 * File:    $HeadURL$
 * Version: $Revision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group
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
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * An input item whose value is obtained via ASR or DTMF grammars.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 */
public final class FieldFormItem
        extends AbstractGrammarContainer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(FieldFormItem.class);

    /** The shadow var container template. */
    private static final Class<? extends Object> SHADOW_VAR_CONTAINER_TEMPLATE =
            FieldShadowVarContainer.class;

    /** The shadow var container for this filed. */
    private FieldShadowVarContainer shadowVarContainer;

    /**
     * Creates a new field input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding XML node in the VoiceXML document.
     */
    public FieldFormItem(final VoiceXmlInterpreterContext context,
                         final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        visitor.visitFieldFormItem(this);
    }

    /**
     * Lazy instantiation of the field shadow var container.
     * @return the field shadow var container.
     * @exception SemanticError
     *            error creating the shadow var container.
     * @since 0.6
     */
    private FieldShadowVarContainer getShadowVarContainer()
        throws SemanticError {
        if (shadowVarContainer == null) {
                shadowVarContainer =
                        (FieldShadowVarContainer) createShadowVarContainer();
            shadowVarContainer.setField(this);
        }

        return shadowVarContainer;
    }

    /**
     * {@inheritDoc}
     *
     * Sets also the shadow variables.
     */
    @Override
    public void setFormItemVariable(final Object value) {
        final RecognitionResult result = (RecognitionResult) value;
        super.setFormItemVariable(result.getUtterance());

        FieldShadowVarContainer container = null;
        try {
            container = getShadowVarContainer();
        } catch (SemanticError e) {
            LOGGER.error("error creating the shadow var container", e);
        }
        container.setResult(result);
    }

    /**
     * Sets the markname.
     * @param mark The name of the mark.
     *
     * @since 0.5
     */
    public void setMarkname(final String mark) {
        FieldShadowVarContainer container = null;
        try {
            container = getShadowVarContainer();
        } catch (SemanticError e) {
            LOGGER.error("error creating the shadow var container", e);
        }

        container.setMarkname(mark);
    }

    /**
     * Gets the field belonging to this <code>FieldFormItem</code>.
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
     * {@inheritDoc}
     */
    @Override
    protected void addCustomGrammars(final Collection<Grammar> grammars) {
        final Field field = getField();

        // If a type is given, create a nested grammar with a builtin URI. 
        final String type = field.getType();
        if (type == null) {
            return;
        }

        final VoiceXmlDocument document =
            field.getOwnerXmlDocument(VoiceXmlDocument.class);
        final Vxml vxml = document.getVxml();
        final String language = vxml.getXmlLang();

        final Grammar dtmfGrammar = field.appendChild(Grammar.class);
        dtmfGrammar.setSrc("builtin://dtmf/" + type);
        dtmfGrammar.setXmlLang(language);
        grammars.add(dtmfGrammar);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added builtin grammar '"
                    + dtmfGrammar.getSrc() + "'");
        }
        final Grammar voiceGrammar = field.appendChild(Grammar.class);
        voiceGrammar.setSrc("builtin://voice/" + type);
        voiceGrammar.setXmlLang(language);
        grammars.add(voiceGrammar);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added builtin grammar '"
                    + voiceGrammar.getSrc() + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModal() {
        final Field field = getField();
        if (field == null) {
            return false;
        }

        return field.isModal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Object> getShadowVariableContainer() {
        return SHADOW_VAR_CONTAINER_TEMPLATE;
    }
}
