/*
 * File:    $RCSfile: ExecutableMenuForm.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.form;

import java.util.Collection;

import org.jvoicexml.interpreter.ExecutableForm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Elseif;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.vxml.If;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Reprompt;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.jvoicexml.xml.vxml.Assign;

/**
 * Implementation of an <code>ExecutableForm</code> for the
 * <code>&lt;menu&gt;</code> tag.
 *
 * @see org.jvoicexml.xml.vxml.Menu
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class ExecutableMenuForm
        implements ExecutableForm {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ExecutableMenuForm.class);

    /** The encapsulated tag. */
    private final Menu menu;

    /**
     * Constructs a new object.
     * @param tag The menu tag.
     */
    public ExecutableMenuForm(final Menu tag) {
        menu = tag;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        final String formId = menu.getId();
        if (formId == null) {
            return ExecutableForm.UNNAMED_FORM;
        }

        return formId;
    }

    /**
     * {@inheritDoc}
     */
    public NodeList getChildNodes() {
        return menu.getChildNodes();
    }

    /**
     * {@inheritDoc}
     *
     * Creates an anonymous field, which does not exist in the document.
     */
    public Collection<FormItem> getFormItems(
            final VoiceXmlInterpreterContext context) {
        final Collection<FormItem> items = new java.util.ArrayList<FormItem>();

        final Field field = createAnonymousField(context);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created anonymous field");
            LOGGER.debug(field);
        }

        final FieldFormItem item = new FieldFormItem(context, field);

        items.add(item);

        return items;
    }

    /**
     * Creates the anonymos field for this menu.
     *
     * <p>
     * A <code>&lt;menu&gt;</code> is a convenient syntactic shorthand for a
     * form containing a single anonymous field that prompts the user to make
     * a choice and transitions to different places based on that choice.
     * </p>
     *
     * @param context VoiceXmlInterpreterContext
     * @return Field
     */
    private Field createAnonymousField(
            final VoiceXmlInterpreterContext context) {
        final Document document = menu.getOwnerDocument();
        final Node newNode = document.createElement(Field.TAG_NAME);
        final Field field = new Field(newNode);
        field.setName(getId());

        addNonChoiceChildren(field);

        final Collection<Choice> choices = menu.getChildNodes(Choice.class);

        if (choices.size() > 0) {
            convertChoices(field, choices);
        }

        return field;
    }

    /**
     * Adds all children of the menu to the newly created anonymous field,
     * that are no choice tags.
     * @param field The anonymous field.
     *
     * @since 0.5
     */
    private void addNonChoiceChildren(final Field field) {
        final NodeList children = menu.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (!(child instanceof Choice)) {
                field.appendChild(child);
            }
        }
    }

    /**
     * Convert all choices of the menu into appropriate tags of the newly
     * created anonymous field.
     * @param field The anonymous field.
     * @param choices Choices of the menu tag.
     *
     * @since 0.5
     */
    private void convertChoices(final Field field,
                                final Collection<Choice> choices) {
        final String name = field.getName();

        final Prompt choicePrompt = field.addChild(Prompt.class);

        final Filled filled = field.addChild(Filled.class);

        /** @todo Check why this is necessary. */
        final Assign assign = filled.addChild(Assign.class);
        assign.setName(name);
        assign.setExpr(name);

        final If iftag = filled.addChild(If.class);

        for (Choice choice : choices) {
            final VoiceXmlNode tag;
            if (iftag.hasChildNodes()) {
                tag = iftag.addChild(Elseif.class);
            } else {
                tag = iftag;
            }

            if (choice.hasChildNodes()) {
                final Text text = (Text) choice.getFirstChild();
                final String value = text.getNodeValue();
                String cond = name + "=='" + value.trim() + "'";

                choicePrompt.addText(value);

                final String dtmf = choice.getDtmf();
                if (dtmf != null) {
                    cond += " || " + name + "=='" + dtmf + "'";
                }

                tag.setAttribute(If.ATTRIBUTE_COND, cond);
            }

            final Goto gototag = iftag.addChild(Goto.class);
            final String next = choice.getNext();
            gototag.setNext(next);
        }

        filled.addChild(Reprompt.class);
    }
}
