/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/dialog/TestExecutableMenuForm.java $
 * Version: $LastChangedRevision: 2612 $
 * Date:    $Date: 2011-02-28 11:58:33 -0600 (lun, 28 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.InputItem;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Catch;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Elseif;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Help;
import org.jvoicexml.xml.vxml.If;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.NodeList;

/**
 * This class tests the {@link ExecutableMenuForm}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2612 $
 * @since 0.6
 *
 */
public final class TestExecutableMenuForm {
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
            Assert.fail(pce.getMessage());

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
            if (item instanceof InputItem) {
                InputItem fieldItem = (InputItem) item;
                return (Field) fieldItem.getNode();
            }
        }

        Assert.fail("menu does not contain a field");

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
        Assert.assertEquals(1, filleds.size());
        Iterator<Filled> iterator = filleds.iterator();
        Filled filled = iterator.next();

        XmlNode node = getConditionNode(filled, condition);
        if (node == null) {
            Assert.fail("Condition '" + condition + "' not found.");
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

        Assert.fail("Prompt '" + text + "' not found.");

        return null;
    }
    /**
     * Convenience method to check if the given test is in one of the field's
     * prompts.
     * @param field the field
     * @param text the text of the prompt to check
     */
    private void isInPromptNode(final Field field, final String text) {
        final Collection<Prompt> prompts = field.getChildNodes(Prompt.class);
        for (Prompt prompt : prompts) {
            String currentText = prompt.getTextContent();
            if (currentText.indexOf(text) > 0) {
                return;
            }
        }

        Assert.fail("Prompt '" + text + "' not found.");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    @Test
    public void testExecutableMenuFormDTMFOnly() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");

        final Prompt promptMenu = menu.appendChild(Prompt.class);
        promptMenu.addText("Please enter 1 for option 1 and 2 for option 2");
        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("http://option1?param1=value1&param2=value2");
        choice1.setDtmf("1");
        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.setDtmf("2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());

        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(1, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());

        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    @Test
    public void testExecutableMenuFormGenerated() throws BadFetchError {
        final Vxml vxml = createDocument();
        vxml.setXmlLang("en_US");
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(1, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='option 2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    @Test
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
        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    @Test
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

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);
        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='2'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *         Test failed.
     */
    @Test
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

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='*'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='#'");
        getConditionNode(field, "testmenu=='option 3'");
        getConditionNode(field, "testmenu=='0'");
        getConditionNode(field, "testmenu=='option 4'");
        getConditionNode(field, "testmenu=='1'");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @throws BadFetchError
     *          expected error
     */
    @Test(expected = BadFetchError.class)
    public void testExecutableMenuFormDtmfOwnDtmfError() throws BadFetchError {
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

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        extractField(execMenu);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @exception BadFetchError
     *            Test failed.
     */
    @Test
    public void testExecutableMenuFormEnumerate() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        final Enumerate enumerate = menu.appendChild(Enumerate.class);
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

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='2'");
        getPromptNode(field, "For option 1 press 1 For option 2 press 2");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @exception BadFetchError
     *            Test failed.
     */
    @Test
    public void testExecutableMenuFormEnumerateEmpty() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        menu.appendChild(Enumerate.class);

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='2'");
        getPromptNode(field, "option 1 option 2");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @exception BadFetchError
     *            Test failed.
     */
    @Test
    public void testExecutableMenuFormEnumerateInPrompt() throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        final Prompt prompt = menu.appendChild(Prompt.class);
        prompt.addText("Welcome!");
        final Enumerate enumerate = prompt.appendChild(Enumerate.class);
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

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='2'");
        isInPromptNode(field, "For option 1 press 1 For option 2 press 2");
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.dialog.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     * @exception BadFetchError
     *            Test failed.
     */
    @Test
    public void testExecutableMenuFormEnumerateEmptyInPrompt()
        throws BadFetchError {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("testmenu");
        menu.setDtmf(true);

        final Prompt prompt = menu.appendChild(Prompt.class);
        prompt.addText("Please say one of ");
        prompt.appendChild(Enumerate.class);

        final Choice choice1 = menu.appendChild(Choice.class);
        choice1.setNext("#option1");
        choice1.addText("option 1");

        final Choice choice2 = menu.appendChild(Choice.class);
        choice2.setNext("#option2");
        choice2.addText("option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm();
        execMenu.setNode(menu);
        execMenu.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Field field = extractField(execMenu);

        final Collection<Grammar> grammars = field.getChildNodes(Grammar.class);
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final String vxmlLang = vxml.getXmlLang();
        Assert.assertEquals(vxmlLang, voiceGrammar.getXmlLang());
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        getConditionNode(field, "testmenu=='option 1'");
        getConditionNode(field, "testmenu=='1'");
        getConditionNode(field, "testmenu=='option 2'");
        getConditionNode(field, "testmenu=='2'");
        isInPromptNode(field, "option 1 option 2");
    }

    /**
     * Testcase for {@link ExecutaleMenuForm#getFilledElements()}.
     * @exception Exception test failed.
     */
    @Test
    public void testGetFilledElements() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Menu menu = vxml.appendChild(Menu.class);
        menu.appendChild(Noinput.class);
        menu.appendChild(Help.class);
        final Catch catchNode = menu.appendChild(Catch.class);
        catchNode.setEvent("test");

        final ExecutableMenuForm dialog = new ExecutableMenuForm();
        dialog.setNode(menu);
        dialog.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Collection<Filled> elements = dialog.getFilledElements();
        Assert.assertNull(elements);
    }

    /**
     * Testcase for {@link ExecutaleMenuForm#getFilledElements()}.
     * @exception Exception test failed.
     */
    @Test
    public void testGetCatchElements() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Menu menu = vxml.appendChild(Menu.class);
        final Noinput noinput = menu.appendChild(Noinput.class);
        final Help help = menu.appendChild(Help.class);
        final Catch catchNode = menu.appendChild(Catch.class);
        catchNode.setEvent("test");

        final ExecutableMenuForm dialog = new ExecutableMenuForm();
        dialog.setNode(menu);
        dialog.setChoiceConverter(new SrgsXmlChoiceConverter());
        final Collection<AbstractCatchElement> elements =
            dialog.getCatchElements();
        Assert.assertEquals(3, elements.size());
        for (AbstractCatchElement element : elements) {
            String tag = element.getTagName();
            if (tag.equals(Noinput.TAG_NAME)) {
                Assert.assertTrue("expected to find noinput element",
                        element.isEqualNode(noinput));
            } else if (tag.equals(Help.TAG_NAME)) {
                Assert.assertTrue("expected to find help element",
                        element.isEqualNode(help));
            } else if (tag.equals(Catch.TAG_NAME)) {
                Assert.assertTrue("expected to find catch element",
                        element.isEqualNode(catchNode));
            } else {
                Assert.fail("unknown tag: '" + tag + "'");
            }
        }
    }
}
