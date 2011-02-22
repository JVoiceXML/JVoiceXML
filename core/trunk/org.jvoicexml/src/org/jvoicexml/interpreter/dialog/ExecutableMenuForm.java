/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.GenericVoiceXmlEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.TextContainer;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.AcceptType;
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
import org.jvoicexml.xml.vxml.Throw;
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
 * <p>
 * Since a menu is a shorthand notation for a field, this implementation creates
 * a new field from the menu which is handled by the interpreter.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Menu
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 */
public final class ExecutableMenuForm
        implements Dialog {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ExecutableMenuForm.class);

    /** Maximum number that can be used for DTMF generation. */
    private static final int MAX_DTMF_VALUE = 9;

    /** The encapsulated tag. */
    private final Menu menu;

    /** Id of this dialog. */
    private final String id;

    /** The created anonymous field. */
    private Field field;

    /**
     * Constructs a new object.
     *
     * @param tag
     *            The menu tag.
     */
    public ExecutableMenuForm(final Menu tag) {
        menu = tag;
        id = DialogIdFactory.getId(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @return child nodes of the created anonymous field, <code>null</code>
     *         if the field has not been created.
     */
    @Override
    public Collection<XmlNode> getChildNodes() {
        if (field == null) {
            LOGGER.warn("anonymous field for '" + id
                    + "' has not been yet created");
            return null;
        }
        return field.getChildren();
    }

    /**
     * {@inheritDoc}
     *
     * Creates an anonymous field, which does not exist in the document.
     *
     * @throws BadFetchError
     *             Error converting choices.
     */
    @Override
    public Collection<FormItem> getFormItems(
            final VoiceXmlInterpreterContext context) throws BadFetchError {
        if (field == null) {
            field = createAnonymousField(context);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("created anonymous field");
                LOGGER.debug(field);
            }
        }

        final Collection<FormItem> items = new java.util.ArrayList<FormItem>();
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
        field = new Field(newNode);
        field.setName(getId());

        final Collection<Choice> choices = menu.getChildNodes(Choice.class);
        final Collection<ConvertedChoiceOption> converted;
        try {
            final boolean dtmf = menu.isDtmf();
            converted = convertChoices(choices, dtmf);
        } catch (URISyntaxException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        createGrammars(converted, ModeType.VOICE);
        createGrammars(converted, ModeType.DTMF);
        createFilled(converted);
        copyPrompts(converted);
        expandEnumerates(converted);
        copyRemainigNodes();

        return field;
    }

    /**
     * Create the {@link ConvertedChoiceOption}s from the choices.
     * @param choices choices in the menu
     * @param dtmf <code>true</code> if implict DTMF sequences should be
     *        generated
     * @return converted choices
     * @throws URISyntaxException
     *         if the value of the next attribute could not be converted into
     *         a valid URI
     * @throws BadFetchError
     *         invalid arguments in choices
     * @since 0.7.5
     */
    private Collection<ConvertedChoiceOption> convertChoices(
            final Collection<Choice> choices, final boolean dtmf)
            throws URISyntaxException, BadFetchError {
        final ChoiceConverter converter = new SrgsXmlChoiceConverter();
        final Collection<ConvertedChoiceOption> converted =
            new java.util.ArrayList<ConvertedChoiceOption>();
        int count = 1;
        for (Choice choice : choices) {
            final ConvertedChoiceOption conv =
                new ConvertedChoiceOption(field);

            // Do all the choice local stuff
            final AcceptType accept;
            if (choice.isAcceptSpecified()) {
                accept = choice.getAcceptObject();
            } else {
                accept = menu.getAcceptObject();
            }
            conv.setAccept(accept);
            final URI uri = choice.getNextUri();
            conv.setNext(uri);
            final String event = choice.getEvent();
            final String message = choice.getMessage();
            final JVoiceXMLEvent e = createEvent(event, message);
            conv.setEvent(e);
            final String choiceText = choice.getFirstLevelTextContent();
            final String text = choiceText.trim();
            conv.setText(text);
            if (dtmf) {
                final String dtmfValue = choice.getDtmf();
                if (dtmfValue == null) {
                    choice.setDtmf(Integer.toString(count));
                    count++;
                    if (count > MAX_DTMF_VALUE) {
                        throw new BadFetchError("More than " + MAX_DTMF_VALUE
                                + " choices in menu '" + id + "'");
                    }
                } else if (!dtmfValue.equals("0") && !dtmfValue.equals("#")
                        && !dtmfValue.endsWith("*")) {
                    throw new BadFetchError("menu '" + id
                       + "' set dtmf to true but choice has an invalid value ('"
                       + dtmfValue + "')");
                }
            }
            final String dtmfValue = choice.getDtmf();
            conv.setDtmf(dtmfValue);

            // Run the converter to add missing info
            final ConvertedChoiceOption convDtmf = conv.clone();
            convDtmf.setMode(ModeType.DTMF);
            conv.setMode(ModeType.VOICE);
            converter.convertChoice(choice, ModeType.VOICE, conv);
            converter.convertChoice(choice, ModeType.DTMF, convDtmf);

            // Add it to the list of known converted options
            converted.add(conv);
            converted.add(convDtmf);
        }
        return converted;
    }

    /**
     * Create grammars from the determined converted choices.
     * @param converted the converted choices
     * @param mode the current mode
     * @since 0.7.5
     */
    private void createGrammars(
            final Collection<ConvertedChoiceOption> converted,
            final ModeType mode) {
        final Collection<String> items = new java.util.ArrayList<String>(); 
        for (ConvertedChoiceOption conv : converted) {
            if (conv.getMode() == mode) {
                final Collection<String> inputs = conv.getAcceptedInputs();
                if (inputs != null) {
                    items.addAll(inputs);
                }
            }
        }
        if (items.isEmpty()) {
            return;
        }
        final Grammar grammar = field.appendChild(Grammar.class);
        grammar.setRoot(field.getName() + "-" + mode);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        // Copy the lang attribute from the parent document.
        final VoiceXmlDocument owner =
            grammar.getOwnerXmlDocument(VoiceXmlDocument.class);
        final Vxml vxml = owner.getVxml();
        final String lang = vxml.getXmlLang();
        if (lang == null) {
            LOGGER.warn("No xml:lang attribute specified in vxml. "
                    + "Can not set xml:lang in created voice grammar");
        } else {
            grammar.setXmlLang(lang);
        }
        grammar.setMode(mode);
        //Create root rule
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId(grammar.getRoot());
        rule.setScope("public");
        final OneOf oneOf = rule.appendChild(OneOf.class);
        for (String current : items) {
            final Item item = oneOf.appendChild(Item.class);
            item.addText(current);
        }
    }

    /**
     * Creates the filled clause for the generated grammars.
     * @param converted the converted choice options
     * @since 0.7.5
     */
    private void createFilled(
            final Collection<ConvertedChoiceOption> converted) {
        final Filled filled = field.appendChild(Filled.class);
        final If iftag = filled.appendChild(If.class);
        XmlNode node = iftag;
        final String name = field.getName();
        for (ConvertedChoiceOption conv : converted) {
            final Collection<String> inputs = conv.getAcceptedInputs();
            if (inputs != null) {
                for (String input : inputs) {
                    if (iftag.hasChildNodes()) {
                        node = iftag.appendChild(Elseif.class);
                    } else {
                        node = iftag;
                    }
                    final StringBuilder str = new StringBuilder();
                    str.append(name);
                    str.append("=='");
                    str.append(input);
                    str.append("'");
                    final String condition = str.toString();
                    node.setAttribute(If.ATTRIBUTE_COND, condition);
                    if (conv.getEvent() != null) {
                        final JVoiceXMLEvent event = conv.getEvent();
                        final String type = event.getEventType();
                        final String message = event.getMessage();
                        final Throw throwTag = iftag.appendChild(Throw.class);
                        throwTag.setEvent(type);
                        throwTag.setMessage(message);
                    } else {
                        final Goto gotoTag = iftag.appendChild(Goto.class);
                        final URI uri = conv.getNext();
                        gotoTag.setNext(uri);
                    }
                }
            }
        }

        // If anything fails: reprompt.
        filled.appendChild(Reprompt.class);
    }

    /**
     * Copies the existing prompts from the <code>&lt;menu&gt;</code> to the
     * anonymous <code>&lt;field&gt;</code>.
     * @param converted converted choice options
     * @since 0.7.5
     */
    private void copyPrompts(
            final Collection<ConvertedChoiceOption> converted) {
        final Collection<Prompt> prompts = menu.getChildNodes(Prompt.class);
        for (Prompt prompt : prompts) {
            copyPrompt(prompt, converted);
        }
    }

    /**
     * Copies the given prompt.
     * @param prompt the prompt to copy
     * @param converted converted choice options
     * @since 0.7.5
     */
    private void copyPrompt(final Prompt prompt,
            final Collection<ConvertedChoiceOption> converted) {
        final Prompt createdPrompt = field.appendChild(Prompt.class);
        final Collection<XmlNode> nodes = prompt.getChildren();
        for (XmlNode node : nodes) {
            if (node instanceof Enumerate) {
                final Enumerate enumerate = (Enumerate) node;
                expandEnumerate(createdPrompt, enumerate, converted);
            } else if (node instanceof TextContainer) {
                final TextContainer container = (TextContainer) node;
                final String text = container.getTextContent();
                createdPrompt.addText(text);
            } else if (node instanceof Text) {
                final Text text = (Text) node;
                final String value = text.getTextContent();
                createdPrompt.addText(value);
            } else {
                final Node clonedNode = node.cloneNode(true);
                createdPrompt.appendChild(clonedNode);
            }
        }
    }

    /**
     * Creates an event object from the given data.
     * @param event type of the event
     * @param message message specifying additional content
     * @return created message or <code>null</code> if no message was created
     * @since 0.7.5
     */
    private JVoiceXMLEvent createEvent(final String event,
            final String message) {
        if (event == null) {
            return null;
        }
        return new GenericVoiceXmlEvent(event, message);
    }

    /**
     * Expand the enumerates.
     * @param converted the converted choice options.
     * @since 0.7.5
     */
    private void expandEnumerates(
            final Collection<ConvertedChoiceOption> converted) {
        final Collection<Enumerate> enumerates =
            menu.getChildNodes(Enumerate.class);
        for (Enumerate enumerate : enumerates) {
            final Prompt prompt = field.appendChild(Prompt.class);
            expandEnumerate(prompt, enumerate, converted);
        }
    }

    /**
     * Expands the given <code>&lt;enumerate&gt;</code> node.
     * @param prompt the current prompt
     * @param enumerate a template for the prompts, maybe <code>null</code>.
     * @param converted the converted choice options
     */
    private void expandEnumerate(final Prompt prompt, final Enumerate enumerate,
            final Collection<ConvertedChoiceOption> converted) {
        for (ConvertedChoiceOption conv : converted) {
            if (conv.getMode() == ModeType.VOICE) {
                final Collection<XmlNode> nodes = enumerate.getChildren();
                if (nodes.isEmpty()) {
                    final String text = conv.getText();
                    prompt.addText(text);
                } else {
                    for (XmlNode node : nodes) {
                        if (node instanceof Value) {
                            final Value value = (Value) node;
                            final String expr = value.getExpr();
    
                            if (Enumerate.PROMPT_VARIABLE.equalsIgnoreCase(
                                    expr)) {
                                final String text = conv.getText();
                                prompt.addText(text);
                            } else if (Enumerate.DTMF_VARIABLE.equalsIgnoreCase(
                                    expr)) {
                                final String dtmf = conv.getDtmf();
                                prompt.addText(dtmf);
                            } else {
                                final Node clonedNode = node.cloneNode(true);
                                prompt.appendChild(clonedNode);
                            }
                        } else if (node instanceof TextContainer) {
                            final TextContainer container =
                                (TextContainer) node;
                            final String text = container.getTextContent();
                            prompt.addText(text);
                        } else if (node instanceof Text) {
                            final Text text = (Text) node;
                            final String value = text.getTextContent();
                            prompt.addText(value);
                        } else {
                            final Node clonedNode = node.cloneNode(true);
                            prompt.appendChild(clonedNode);
                        }
                    }
                }
            }
        }
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
     * Copy everything except enuemrate, choices and prompts from the
     * <code>&lt;menu&gt;</code> to the generated <code>&lt;field&gt</code>.
     * 
     * @since 0.7.5
     */
    private void copyRemainigNodes() {
        final Collection<XmlNode> nodes = menu.getChildren();
        for (XmlNode node : nodes) {
            if (!(node instanceof Prompt) && !(node instanceof Choice)
                    && !(node instanceof Enumerate)) {
                final Node clonedNode = node.cloneNode(true);
                field.appendChild(clonedNode);
            }
        }
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
