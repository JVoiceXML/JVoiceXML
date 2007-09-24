/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Elseif;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.If;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.NodeList;

/**
 * This class tests the {@link ExecutableMenuForm}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestExecutableMenuForm extends TestCase {
    /**
     * Create the VoiceXML document.
     *
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    private Vxml createDocument() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            fail(pce.getMessage());

            return null;
        }

        return document.getVxml();
    }

    /**
     * Extracts the generated field from the menu.
     * @param menu the menu.
     * @return the created field.
     * @throws BadFetchError
     *         Error getting the form items.
     */
    private Field extractField(final ExecutableMenuForm menu)
        throws BadFetchError {
        Collection<FormItem> items = menu.getFormItems(null);

        for (FormItem item : items) {
            if (item instanceof FieldFormItem) {
                FieldFormItem fieldItem = (FieldFormItem) item;
                return (Field) fieldItem.getNode();
            }
        }

        fail("menu does not contain a field");

        return null;
    }

    /**
     * Convenience method to retrieve a child node with the given condition.
     * @param node the node to check.
     * @param condition the condition to check.
     * @return The node with the given condition.
     */
    private XmlNode getConditionNode(final XmlNode node,
            final String condition) {
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final XmlNode child = (XmlNode) children.item(i);
            if ((child instanceof If) || (child instanceof Elseif)) {
                final String cond = child.getAttribute(If.ATTRIBUTE_COND);

                if (cond.equals(condition)) {
                    return child;
                } else {
                    XmlNode subchild = getConditionNode(child, condition);
                    if (subchild != null) {
                        return subchild;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Convenience method to retrieve the node with the given condition.
     * @param field the field
     * @param condition the condition to check.
     * @return node with the given condition.
     */
    private XmlNode getConditionNode(final Field field,
            final String condition) {
        final Collection<Filled> filleds = field.getChildNodes(Filled.class);
        assertEquals(1, filleds.size());
        Iterator<Filled> iterator = filleds.iterator();
        Filled filled = iterator.next();

        XmlNode node = getConditionNode(filled, condition);
        if (node == null) {
            fail("Condition '" + condition + "' not found.");
        }

        return node;
    }

    /**
     * Convenience method to retrieve the node with the given text.
     * @param field the field
     * @param text the text of the prompt to check
     * @return node with the given text.
     */
    private XmlNode getPromptNode(final Field field,
            final String text) {
        final Collection<Prompt> prompts = field.getChildNodes(Prompt.class);
        for (Prompt prompt : prompts) {
            String currentText = prompt.getTextContent();
            if (text.equals(currentText)) {
                return prompt;
            }
        }

        fail("Prompt '" + text + "' not found.");

        return null;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    public void testExecutableMenuFormDTMFOnly() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");

        final Prompt promptMenu = menu.appendChild(Prompt.class);
        promptMenu.addText("Please enter 1 for option 1 and 2 for option 2");
        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.setDtmf("1");
        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.setDtmf("2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    public void testExecutableMenuFormGenerated() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='option 2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    public void testExecutableMenuFormMixed() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.setDtmf("1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.setDtmf("2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        getConditionNode(field, "testmenu=='option 1' || testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2' || testmenu=='2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    public void testExecutableMenuFormDtmf() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        getConditionNode(field, "testmenu=='option 1' || testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2' || testmenu=='2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    public void testExecutableMenuFormDtmfOwnDtmf() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");
        choice1.setDtmf("*");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");
        choice2.setDtmf("#");

        final Choice choice3 = menu.appendChild(Choice.class);
        choice3.setNext("#option3");
        choice3.addText("option 3");
        choice3.setDtmf("0");

        final Choice choice4 = menu.appendChild(Choice.class);
        choice4.setNext("#option4");
        choice4.addText("option 4");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        getConditionNode(field, "testmenu=='option 1' || testmenu=='*'");
        getConditionNode(field, "testmenu=='option 2' || testmenu=='#'");
        getConditionNode(field, "testmenu=='option 3' || testmenu=='0'");
        getConditionNode(field, "testmenu=='option 4' || testmenu=='1'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     */
    public void testExecutableMenuFormDtmfOwnDtmfError() {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");
        choice2.setDtmf("2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        BadFetchError error = null;
        try {
            extractField(execMenu);
        } catch (BadFetchError e) {
            error = e;
        }

        assertNotNull("BadFetchError expected", error);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @exception BadFetchError
     *            Test failed.
     */
    public void testExecutableMenuFormEnumerate() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        Enumerate enumerate = menu.appendChild(Enumerate.class);
        enumerate.addText("For ");
        enumerate.addPromptVariable();
        enumerate.addText(" press ");
        enumerate.addDtmfVariable();

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        getConditionNode(field, "testmenu=='option 1' || testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2' || testmenu=='2'");
        getPromptNode(field, "For option 1 press 1");
        getPromptNode(field, "For option 2 press 2");
    }
}
