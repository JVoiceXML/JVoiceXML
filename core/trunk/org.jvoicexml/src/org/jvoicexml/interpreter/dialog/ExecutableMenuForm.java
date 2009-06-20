/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.dialog;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
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
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of a {@link Dialog} for the
 * <code>&lt;menu&gt;</code> tag.
 *
 * @see org.jvoicexml.xml.vxml.Menu
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 */
public final class ExecutableMenuForm
        implements Dialog {
    /** Maximum number that can be used for DTMF generation. */
    private static final int MAX_DTMF_VALUE = 9;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ExecutableMenuForm.class);

    /** The encapsulated tag. */
    private final Menu menu;

    /** Choices converted to prompts. */
    private final Collection<Prompt> choicePrompts;

    /**
     * Text content of the choice tags that can be used to fill the
     * <code>_prompt</code> variables.
     */
    private final Collection<String> specialVariablePrompt;

    /**
     * Text content of the choice dtmf attributes that can be used to fill the
     * <code>_dtmf</code> variables.
     */
    private final Collection<String> specialVariableDtmf;

    /** Id of this dialog. */
    private final String id;

    /**
     * Constructs a new object.
     *
     * @param tag
     *            The menu tag.
     */
    public ExecutableMenuForm(final Menu tag) {
        menu = tag;
        choicePrompts = new java.util.ArrayList<Prompt>();
        specialVariablePrompt = new java.util.ArrayList<String>();
        specialVariableDtmf = new java.util.ArrayList<String>();
        id = DialogIdFactory.getId(menu);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<XmlNode> getChildNodes() {
        return menu.getChildren();
    }

    /**
     * {@inheritDoc}
     *
     * Creates an anonymous field, which does not exist in the document.
     *
     * @throws BadFetchError
     *             Error converting choices.
     */
    public Collection<FormItem> getFormItems(
            final VoiceXmlInterpreterContext context) throws BadFetchError {
        final Collection<FormItem> items = new java.util.ArrayList<FormItem>();

        final Field field = createAnonymousField(context);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created anonymous field");
            LOGGER.debug(field);
        }

        final FormItem item = new FieldFormItem(context, field);

        items.add(item);

        return items;
    }

    /**
     * Creates the anonymous field for this menu.
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
        // Evaluate nested enumerate tags.
        for (Prompt prompt : field.getChildNodes(Prompt.class)) {
            final Collection<Enumerate> nestedEnumerates =
                prompt.getChildNodes(Enumerate.class);
            if (!nestedEnumerates.isEmpty()) {
                final Iterator<Enumerate> iterator = nestedEnumerates.iterator();
                final Enumerate nestedEnumerate = iterator.next();
                processEnumerate(prompt, nestedEnumerate);
                prompt.removeChild(nestedEnumerate);
            }
        }

        return field;
    }

    /**
     * Processes an enumerate that is a child of a prompt.
     * @param prompt the prompt
     * @param enumerate the nested enumerate.
     */
    private void processEnumerate(final Prompt prompt,
            final Enumerate enumerate) {
        NodeList children = enumerate.getChildNodes();
        final Iterator<String> dtmfIterator = specialVariableDtmf.iterator();
        for (String specialPrompt : specialVariablePrompt) {
            prompt.addText(" ");
            final String specialDtmf;
            if (dtmfIterator.hasNext()) {
                specialDtmf = dtmfIterator.next();
            } else {
                specialDtmf = null;
            }
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child instanceof Value) {
                    final Value value = (Value) child;
                    final String expr = value.getExpr();

                    if (Enumerate.PROMPT_VARIABLE.equalsIgnoreCase(expr)) {
                        prompt.addText(specialPrompt);
                    } else if (Enumerate.DTMF_VARIABLE.equalsIgnoreCase(expr)) {
                        prompt.addText(specialDtmf);
                    }
                } else if (!(child instanceof Enumerate)) {
                    Node node = child.cloneNode(true);
                    prompt.appendChild(node);
                }
            }
            prompt.addText(" ");
        }
    }

    /**
     * Adds all children of the menu to the newly created anonymous field, that
     * are neither choice tags nor enumerate tags.
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
                // Must be done in two steps to remove the choice tags
                if (iterator.hasNext()) {
                    final Prompt prompt = iterator.next();
                    field.appendChild(prompt);
                }
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

        final If iftag = filled.appendChild(If.class);

        //Configure grammars.
        final Grammar voiceGrammarTag = createVoiceGrammarNode(field);

        //Create root rule
        final Rule voiceRootRule = voiceGrammarTag.appendChild(Rule.class);
        voiceRootRule.setId(voiceGrammarTag.getRoot());
        voiceRootRule.setScope("public");

        //Create grammar option
        final OneOf voiceOneOf = voiceRootRule.appendChild(OneOf.class);

        final Collection<String> dtmfOptions =
            new java.util.ArrayList<String>();

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
                specialVariablePrompt.add(prompt.trim());
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
                dtmfOptions.add(dtmf);
                specialVariableDtmf.add(dtmf);
                if (cond != null) {
                    cond += " || " + name + "=='" + dtmf + "'";
                } else {
                    cond = name + "=='" + dtmf + "'";
                }
            }

            if (cond != null) {
                tag.setAttribute(If.ATTRIBUTE_COND, cond);
            }

            // Create a goto for the current choice.
            final Goto gototag = iftag.appendChild(Goto.class);
            final String next = choice.getNext();
            gototag.setNext(next);

            //Add a item to auto-grammar or specified grammar
            final Collection<Grammar> choiceGrammars =
                choice.getChildNodes(Grammar.class);
            if (choiceGrammars.size() > 0) {
                for (Grammar choiceGrammar : choiceGrammars) {
                    field.appendChild(choiceGrammar);
                }
            } else {
                //Fill grammar item's
                final String choiceText = choice.getFirstLevelTextContent();
                final String trimmedChoiceText = choiceText.trim();
                if (trimmedChoiceText.length() > 0) {
                    final Item item = voiceOneOf.appendChild(Item.class);
                    item.setTextContent(trimmedChoiceText);
                }
            }
        }

        if (dtmfOptions.size() > 0) {
            final Grammar dtmfGrammarTag = createDtmfGrammarNode(field);

            //Create root rule
            final Rule dtmfRootRule =
                dtmfGrammarTag.appendChild(Rule.class);
            dtmfRootRule.setId(dtmfGrammarTag.getRoot());
            dtmfRootRule.setScope("public");

            final OneOf dtmfOneOf = dtmfRootRule.appendChild(OneOf.class);
            for (String current : dtmfOptions) {
                final Item item = dtmfOneOf.appendChild(Item.class);
                item.addText(current);
            }
        }

        //Check if there isn't any choice without a specified grammar
        if (voiceOneOf.getChildNodes().getLength() < 1) {
            //Remove automatically generated grammar (because it's empty)
            field.removeChild(voiceGrammarTag);
        }

        // If all conditions fail: reprompt.
        filled.appendChild(Reprompt.class);
    }

    /**
     * Creates a grammar node for the new anonymous field.
     * @param field the created anonymous field.
     * @return created grammar node.
     */
    private Grammar createVoiceGrammarNode(final Field field) {
        final Grammar grammarTag = field.appendChild(Grammar.class);

        grammarTag.setRoot(field.getName());
        grammarTag.setVersion("1.0");
        grammarTag.setType(GrammarType.SRGS_XML);
        // Copy the lang attribute from the parent document.
        final VoiceXmlDocument owner =
            grammarTag.getOwnerXmlDocument(VoiceXmlDocument.class);
        final Vxml vxml = owner.getVxml();
        final String lang = vxml.getXmlLang();
        if (lang != null) {
            grammarTag.setXmlLang(lang);
            grammarTag.setMode(ModeType.VOICE);
        }
        return grammarTag;
    }

    /**
     * Creates a grammar node for the new anonymous field.
     * @param field the created anonymous field.
     * @return created grammar node.
     */
    private Grammar createDtmfGrammarNode(final Field field) {
        final Grammar grammarTag = field.appendChild(Grammar.class);

        grammarTag.setRoot(field.getName() + "-DTMF");
        grammarTag.setVersion("1.0");
        grammarTag.setType(GrammarType.SRGS_XML);
        grammarTag.setMode(ModeType.DTMF);
        return grammarTag;
    }

    /**
     * Creates a prompt for the given prompt and dtmf.
     * @param field the newly created anonymous field.
     * @param enumerate a template for the prompts, maybe <code>null</code>.
     * @param prompt the prompt text.
     * @param dtmf the current dtmf.
     */
    private void createPrompt(final Field field, final Enumerate enumerate,
            final String prompt, final String dtmf) {
        final Prompt childPrompt = field.addChild(Prompt.class);
        if (enumerate == null) {
            return;
        }
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

        choicePrompts.add(childPrompt);
    }

    /**
     * {@inheritDoc}
     *
     * @return <code>null</code> since the menu is transformed into an
     * anonymous <code>&lt;field&gt;</code> element.
     */
    public Collection<Filled> getFilledElements() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<AbstractCatchElement> getCatchElements() {
        if (menu == null) {
            return null;
        }

        final Collection<AbstractCatchElement> catches =
                new java.util.ArrayList<AbstractCatchElement>();
        final NodeList children = menu.getChildNodes();
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
