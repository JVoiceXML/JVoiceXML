/*
 * File:    $HeadURL$
 * Version: $Revision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group
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
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Option;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mozilla.javascript.ScriptableObject;

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

    /** The converter for <code>&lt;option&gt;</code> tags. */
    private OptionConverter converter;

    /**
     * Constructs a new object as a template.
     */
    public FieldFormItem() {
    }

    /**
     * Creates a new field input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding XML node in the VoiceXML document.
     * @throws IllegalArgumentException
     *         if the given node is not a {@link Field}.
     */
    public FieldFormItem(final VoiceXmlInterpreterContext context,
                         final VoiceXmlNode voiceNode)
                                 throws IllegalArgumentException {
        super(context, voiceNode);
        if (!(voiceNode instanceof Field)) {
            throw new IllegalArgumentException("Node must be a <field>");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractFormItem newInstance(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlNode voiceNode) {
        return new FieldFormItem(ctx, voiceNode);
    }

    /**
     * Sets the option converter to use for the conversion of
     * <code>&lt;option</code> tags into a grammar.
     * @param optionConverter the option converter
     * @since 0.7.6
     */
    public void setOptionConverter(final OptionConverter optionConverter) {
        converter = optionConverter;
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
     * Sets the form item variable with values from the given recognition
     * result.
     * @param result the observed recognition result
     * @exception SemanticError
     *            error passing the values to the scripting engine
     * @since 0.7.6
     */
    public void setFormItemVariable(final RecognitionResult result)
            throws SemanticError {
        // Propagate the result to the shadow var container.
        final FieldShadowVarContainer container = getShadowVarContainer();
        container.setResult(result);

        final Object interpretation =
            result.getSemanticInterpretation();
        if (interpretation == null) {
            super.setFormItemVariable(result.getUtterance());
        } else {
            final String slot = getSlot();
            final VoiceXmlInterpreterContext context = getContext();
            final ScriptingEngine scripting = context.getScriptingEngine();
            Object slotValue;
            try {
                if (interpretation instanceof ScriptableObject) {
                    slotValue = scripting.eval(getShadowVarContainerName()
                            + ".interpretation." + slot + ";");
                } else {
                    slotValue = interpretation;
                }
            } catch (SemanticError e) {
                // Ignore, since this means that there is no match
                slotValue = null;
            }
            super.setFormItemVariable(slotValue);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Sets also the shadow variables.
     */
    @Override
    public void setFormItemVariable(final Object value) throws SemanticError {
        if (value instanceof RecognitionResult) {
            final RecognitionResult result = (RecognitionResult) value;
            setFormItemVariable(result);
        } else {
            final FieldShadowVarContainer container = getShadowVarContainer();
            container.setUtterance(value.toString());
            super.setFormItemVariable(value);
        }
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
        return (Field) node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addCustomGrammars(final Collection<Grammar> grammars) {
        // Determine general parameters for possible grammar additions
        final Field field = getField();
        final VoiceXmlDocument document =
                field.getOwnerXmlDocument(VoiceXmlDocument.class);
        final Vxml vxml = document.getVxml();
        final Locale locale = vxml.getXmlLangObject();

        // Possibly add custom grammars
        addCustomBuiltinGrammars(grammars, field, locale);
        addCustomOptionGrammars(grammars, field, locale);
    }

    /**
     * Check, if there are builtin grammars defined and if so, add them as
     * custom gramamrs.
     * @param grammars current grammars of the field
     * @param field the field
     * @param locale the locale to use
     * @since 0.7.6
     */
    private void addCustomBuiltinGrammars(final Collection<Grammar> grammars,
            final Field field, final Locale locale) {
        // If a type is given, create a nested grammar with a builtin URI. 
        final String type = field.getType();
        if (type == null) {
            return;
        }

        // Add builtin grammars
        if (type.startsWith("builtin:")) {
            final Grammar grammar = addCustomGrammar(field, type, locale);
            grammars.add(grammar);
        } else {
            final Grammar dtmfGrammar = addCustomGrammar(field,
                    "builtin:dtmf/" + type, locale);
            dtmfGrammar.setMode(ModeType.DTMF);
            grammars.add(dtmfGrammar);
            final Grammar voiceGrammar = addCustomGrammar(field,
                    "builtin:voice/" + type, locale);
            voiceGrammar.setMode(ModeType.VOICE);
            grammars.add(voiceGrammar);
        }
    }

    /**
     * Check, if there are builtin grammars defined and if so, add them as
     * custom grammars.
     * @param grammars current grammars of the field
     * @param field the field
     * @param locale the locale to use
     * @since 0.7.6
     */
    private void addCustomOptionGrammars(final Collection<Grammar> grammars,
            final Field field, final Locale locale) {
        final Collection<Option> options = field.getChildNodes(Option.class);
        if (converter == null) {
            if (!options.isEmpty()) {
                LOGGER.warn("no converter defined. unable to process options");
            }
            return;
        }
        final Grammar optionVoiceGrammar =
                converter.createVoiceGrammar(options, locale);
        if (optionVoiceGrammar != null) {
            grammars.add(optionVoiceGrammar);
        }
        final Grammar optionDtmfGrammar =
                converter.createDtmfGrammar(options);
        if (optionDtmfGrammar != null) {
            grammars.add(optionDtmfGrammar);
        }
    }


    /**
     * Adds a new grammar to the field.
     * @param field the current field
     * @param type the type of grammar to add
     * @param language the grammar language
     * @return the created grammar.
     * @since 0.7.5
     */
    private Grammar addCustomGrammar(final Field field, final String type,
            final Locale language) {
        final Grammar grammar = field.appendChild(Grammar.class);
        grammar.setSrc(type);
        grammar.setXmlLang(language);
        grammar.setType(GrammarType.SRGS_XML);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added builtin grammar '" + grammar.getSrc() + "'");
        }
        return grammar;
    }

    /**
     * Retrieves the slot of the related field in mixed initiative dialogs.
     * @return the slot if defined, the name otherwise.
     * @since 0.7.2
     */
    public String getSlot() {
        final Field field = getField();
        final String slot = field.getSlot();
        if (slot != null) {
            return slot;
        }
        return field.getName();
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetShadowVarContainer() throws SemanticError {
        shadowVarContainer = null;
    }
}
