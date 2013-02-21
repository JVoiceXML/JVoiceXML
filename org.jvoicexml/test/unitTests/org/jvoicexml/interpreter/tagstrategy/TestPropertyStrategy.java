/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter.tagstrategy;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Property;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link PromptStrategy}.
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestPropertyStrategy extends TagStrategyTestBase {
    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.PropertyStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     */
    @Test
    public void testExecute() {
        final VoiceXmlDocument document = createDocument();
        final Vxml vxml = document.getVxml();
        final Property property1 = vxml.appendChild(Property.class);
        final String name = "test";
        final String value1 = "value1";
        property1.setName(name);
        property1.setValue(value1);
        final Form form = createForm();
        final Property property2 = form.appendChild(Property.class);
        final String value2 = "value2";
        property2.setName(name);
        property2.setValue(value2);

        VoiceXmlInterpreterContext context = getContext();
        Assert.assertNull(context.getProperty(name));

        final PropertyStrategy strategy1 = new PropertyStrategy();
        try {
            executeTagStrategy(property1, strategy1);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(value1, context.getProperty(name));
        context.enterScope(Scope.DIALOG);
        Assert.assertEquals(value1, context.getProperty(name));
        final PropertyStrategy strategy2 = new PropertyStrategy();
        try {
            executeTagStrategy(property2, strategy2);
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(value2, context.getProperty(name));
        context.exitScope(Scope.DIALOG);
        Assert.assertEquals(value1, context.getProperty(name));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.PropertyStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * using an invalid name.
     */
    @Test
    public void testExecuteInvalidName() {
        final VoiceXmlDocument document1 = createDocument();
        final Vxml vxml1 = document1.getVxml();
        final Property property1 = vxml1.appendChild(Property.class);
        final String value1 = "value1";
        property1.setValue(value1);
        final PropertyStrategy strategy1 = new PropertyStrategy();
        SemanticError error1 = null;
        try {
            executeTagStrategy(property1, strategy1);
        } catch (SemanticError e) {
            error1 = e;
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull("error.sematic expected", error1);

        final VoiceXmlDocument document2 = createDocument();
        final Vxml vxml2 = document2.getVxml();
        final Property property2 = vxml2.appendChild(Property.class);
        final PropertyStrategy strategy2 = new PropertyStrategy();
        SemanticError error2 = null;
        try {
            executeTagStrategy(property2, strategy2);
        } catch (SemanticError e) {
            error2 = e;
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull("error.sematic expected", error2);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.PropertyStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}
     * using an invalid value.
     */
    @Test
    public void testExecuteInvalidNValue() {
        final VoiceXmlDocument document1 = createDocument();
        final Vxml vxml1 = document1.getVxml();
        final Property property1 = vxml1.appendChild(Property.class);
        final String name1 = "name1";
        property1.setName(name1);
        final PropertyStrategy strategy1 = new PropertyStrategy();
        SemanticError error1 = null;
        try {
            executeTagStrategy(property1, strategy1);
        } catch (SemanticError e) {
            error1 = e;
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull("error.sematic expected", error1);

        final VoiceXmlDocument document2 = createDocument();
        final Vxml vxml2 = document2.getVxml();
        final Property property2 = vxml2.appendChild(Property.class);
        final PropertyStrategy strategy2 = new PropertyStrategy();
        SemanticError error2 = null;
        try {
            executeTagStrategy(property2, strategy2);
        } catch (SemanticError e) {
            error2 = e;
        } catch (JVoiceXMLEvent e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull("error.sematic expected", error2);
    }
}
