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

import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Elseif;
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
     */
    private Field extractField(final ExecutableMenuForm menu) {
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
     * Convenience method to check, if the given conddition is present in the
     * given field.
     * @param node the node to check.
     * @param condition the condition to check.
     * @return <code>true</code> if the condition was found.
     */
    private boolean checkCondition(final XmlNode node, final String condition) {
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final XmlNode child = (XmlNode) children.item(i);
            if ((child instanceof If) || (child instanceof Elseif)) {
                final String cond = child.getAttribute(If.ATTRIBUTE_COND);

                if (cond.equals(condition)) {
                    return true;
                } else  if (checkCondition(child, condition)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Convenience method to check, if the given conddition is present in the
     * given field.
     * @param field the field
     * @param condition the condition to check.
     */
    private void checkCondition(final Field field, final String condition) {
        final Collection<Filled> filleds = field.getChildNodes(Filled.class);
        assertEquals(1, filleds.size());
        Iterator<Filled> iterator = filleds.iterator();
        Filled filled = iterator.next();

        if (!checkCondition(filled, condition)) {
            fail("Condition '" + condition + "' not found.");
        }
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.form.ExecutableMenuForm#ExecutableMenuForm(org.jvoicexml.xml.vxml.Menu)}.
     */
    public void testExecutableMenuFormDTMFOnly() {
        final Vxml vxml = createDocument();
        final Menu menu = vxml.addChild(Menu.class);
        menu.setId("testmenu");

        final Prompt promptMenu = menu.addChild(Prompt.class);
        promptMenu.addText("Please enter");
        final Choice choiceList = menu.addChild(Choice.class);
        choiceList.setNext("#option1");
        choiceList.setDtmf("1");
        choiceList.addText("1 for option 1");
        final Choice choiceWatch = menu.addChild(Choice.class);
        choiceWatch.setNext("#option2");
        choiceWatch.setDtmf("2");
        choiceWatch.addText("2 for option 2");

        final ExecutableMenuForm execMenu = new ExecutableMenuForm(menu);
        final Field field = extractField(execMenu);

        checkCondition(field, "testmenu=='1'");
        checkCondition(field, "testmenu=='2'");
    }
}
