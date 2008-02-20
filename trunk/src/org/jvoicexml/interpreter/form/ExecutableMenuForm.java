/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.ExecutableForm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Assign;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Elseif;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.vxml.If;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Reprompt;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.jvoicexml.xml.srgs.ModeType;

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
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
public final class ExecutableMenuForm
        implements ExecutableForm {
    /** Maximum number that can be used for DTMF generation. */
    private static final int MAX_DTMF_VALUE = 9;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ExecutableMenuForm.class);

    /** The encapsulated tag. */
    private final Menu menu;

    /** Choices converted to propmpts. */
    private Collection<Prompt> choicePrompts;

    /**
     * Constructs a new object.
     *
     * @param tag
     *            The menu tag.
     */
    public ExecutableMenuForm(final Menu tag) {
        menu = tag;
        choicePrompts = new java.util.ArrayList<Prompt>();
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
     *
     * @throws BadFetchError
     *             Error converign chices.
     */
    public Collection<FormItem> getFormItems(
            final VoiceXmlInterpreterContext context) throws BadFetchError {
        final Collection<FormItem> items = new java.util.ArrayList<FormItem>();

        final Field field = createAnonymousField(context);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created anonymous field");
            LOGGER.debug(field);
        }

        final InputItem item = new FieldFormItem(context, field);

        items.add(item);

        return items;
    }

    /**
     * Creates the anonymos field for this menu.
     *
     * <p>
     * A <code>&lt;menu&gt;</code> is a convenient syntactic shorthand for a
     * form containing a single anonymous field that prompts the user to make a
     * choice and transitions to different places based on that choice.
     * </p>
     *
     * @param context
     *            VoiceXmlInterpreterContext
     * @return Field
     * @throws BadFetchError
     *             Error converting choices.
     */
    private Field createAnonymousField(final VoiceXmlInterpreterContext context)
            throws BadFetchError {
        final Document document = menu.getOwnerDocument();
        final Node newNode = document.createElement(Field.TAG_NAME);
        final Field field = new Field(newNode);
        field.setName(getId());

        final Collection<Choice> choices = menu.getChildNodes(Choice.class);
        final Collection<Enumerate> enumerates =
            menu.getChildNodes(Enumerate.class);
        final Enumerate enumerate;
        if (enumerates.isEmpty()) {
            enumerate = null;
        } else {
            final Iterator<Enumerate> iterator = enumerates.iterator();
            enumerate = iterator.next();
        }
        final boolean generateDtmf = menu.isDtmf();
        if (choices.size() > 0) {
            convertChoices(field, choices, generateDtmf, enumerate);
        }

        addChildren(field);

        return field;
    }

    /**
     * Adds all children of the menu to the newly created anonymous field, that
     * are no choice tags.
     *
     * @param field
     *            The anonymous field.
     *
     * @since 0.5
     */
    private void addChildren(final Field field) {
        final Iterator<Prompt> iterator = choicePrompts.iterator();
        final NodeList children = menu.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof Choice) {
                final Prompt prompt = iterator.next();
                field.appendChild(prompt);
            } else if (!(child instanceof Enumerate)) {
                field.appendChild(child);
            }
        }
    }

    /**
     * Convert all choices of the menu into appropriate tags of the newly
     * created anonymous field.
     *
     * @param field
     *            The anonymous field.
     * @param choices
     *            Choices of the menu tag.
     * @param generateDtmf
     *            flag if DTMF values should be generated.
     * @param enumerate
     *            an <code>&lt;enumerate&gt;</code> tag appears within the
     *            <code>&lt;enumerate&gt;</code> tag. May be <code>null</code>.
     *
     * @exception BadFetchError
     *                A choice specified an own value for dtmf although dtmf was
     *                set to <code>true</code> in the menu.
     * @since 0.5
     */
    private void convertChoices(final Field field,
            final Collection<Choice> choices, final boolean generateDtmf,
            final Enumerate enumerate) throws BadFetchError {
        final String name = field.getName();

        final Filled filled = field.appendChild(Filled.class);

        /** @todo Check why this is necessary. */
        final Assign assign = filled.appendChild(Assign.class);
        assign.setName(name);
        assign.setExpr(name);

        final If iftag = filled.appendChild(If.class);

        //Configure grammar
        final Grammar grammarTag = field.appendChild(Grammar.class);
        grammarTag.setMode(ModeType.VOICE);
        grammarTag.setRoot("main");
        grammarTag.setVersion("1.0");
        grammarTag.setType(GrammarType.SRGS_XML);
        grammarTag.setXmlLang(grammarTag.getOwnerDocument().getDocumentElement().getAttribute("xml:lang"));

        //Create root rule
        final Rule rootRule = grammarTag.appendChild(Rule.class);
        rootRule.setId("main");
        rootRule.setScope("public");

        //Create grammar option
        final OneOf oneOf = rootRule.appendChild(OneOf.class);

        int choiceNumber = 1;
        for (Choice choice : choices) {
            final VoiceXmlNode tag;
            if (iftag.hasChildNodes()) {
                tag = iftag.appendChild(Elseif.class);
            } else {
                tag = iftag;
            }

            String cond = null;
            String prompt = null;
            if (choice.hasChildNodes()) {
                prompt = choice.getTextContent();
                cond = name + "=='" + prompt.trim() + "'";
            }

            String dtmf = choice.getDtmf();
            if (generateDtmf) {
                if (dtmf == null) {
                    if (choiceNumber <= MAX_DTMF_VALUE) {
                        dtmf = Integer.toString(choiceNumber);
                        ++choiceNumber;
                    }
                } else {
                    if (!dtmf.equals("#") && !dtmf.equals("*")
                            && !dtmf.equals("0")) {
                        throw new BadFetchError("Choice specified DTM '" + dtmf
                                + "' although dtmf attribute of menu "
                                + "was set to true!");
                    }
                }
            }
            createPrompt(field, enumerate, prompt, dtmf);
            if (dtmf != null) {
                if (cond != null) {
                    cond += " || " + name + "=='" + dtmf + "'";
                } else {
                    cond = name + "=='" + dtmf + "'";
                }
            }

            if (cond != null) {
                tag.setAttribute(If.ATTRIBUTE_COND, cond);
            }

            final Goto gototag = iftag.appendChild(Goto.class);
            final String next = choice.getNext();
            gototag.setNext(next);

            //Fill grammar item's
            final Item item = oneOf.appendChild(Item.class);
            item.setTextContent(choice.getFirstLevelTextContent().trim());
        }

        // If all conditions fail: reprompt.
        filled.appendChild(Reprompt.class);
    }

    /**
     * Creates a prompt for the given prompt and dtmf.
     * @param field the newly created anonymous field.
     * @param enumerate a tempalte for the prompts, mayb <code>null</code>.
     * @param prompt the prompt text.
     * @param dtmf the current dtmf.
     */
    private void createPrompt(final Field field, final Enumerate enumerate,
            final String prompt, final String dtmf) {
        final Prompt childPrompt = field.addChild(Prompt.class);
        if (enumerate == null) {
            childPrompt.addText(prompt);
        } else {
            NodeList children = enumerate.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Value) {
                    final Value value = (Value) child;
                    final String expr = value.getExpr();

                    if (Enumerate.PROMPT_VARIABLE.equalsIgnoreCase(expr)) {
                        childPrompt.addText(prompt);
                    } else if (Enumerate.DTMF_VARIABLE.equalsIgnoreCase(expr)) {
                        childPrompt.addText(dtmf);
                    }
                } else if (!(child instanceof Enumerate)) {
                    Node node = child.cloneNode(true);
                    childPrompt.appendChild(node);
                }
            }
        }

        choicePrompts.add(childPrompt);
    }
}
