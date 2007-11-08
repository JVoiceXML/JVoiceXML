/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.demo.voicexmlcreationdemo;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.xml.XmlDocument;
import org.jvoicexml.xml.ccxml.Ccxml;
import org.jvoicexml.xml.ccxml.CcxmlDocument;
import org.jvoicexml.xml.ccxml.Dialogterminate;
import org.jvoicexml.xml.ccxml.Disconnect;
import org.jvoicexml.xml.ccxml.Eventprocessor;
import org.jvoicexml.xml.ccxml.Transition;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.Meta;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.Var;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;


/**
 * Demo implementation of the VoiceXML class library.
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class VoiceXMLCreationDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(VoiceXMLCreationDemo.class);

    /**
     * Do not create from outside.
     */
    private VoiceXMLCreationDemo() {
    }

    /**
     * Create a simpe VoiceXML document using vars and values to display
     * the hello world phrase.
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    private VoiceXmlDocument createHelloWorldWithVar() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();

        Meta meta = vxml.appendChild(Meta.class);
        meta.setName("author");
        meta.setContent("John Doe");

        meta = vxml.appendChild(Meta.class);
        meta.setName("maintainer");
        meta.setContent("hello-support@hi.example.com");

        final Var var = vxml.appendChild(Var.class);
        var.setName("hi");
        var.setExpr("'Hello World!'");

        Form form = vxml.appendChild(Form.class);

        Block block = form.appendChild(Block.class);

        final Value value = block.appendChild(Value.class);
        value.setExpr("hi");

        final Goto gotoElem = block.appendChild(Goto.class);
        gotoElem.setNext("#say_goodbye");

        form = vxml.appendChild(Form.class);
        form.setId("say_goodbye");

        block = form.appendChild(Block.class);
        block.addText("Goodbye!");

        return document;
    }

    /**
     * Create a simple VoiceXML document using grammar and fields. This
     * example is taken from
     * <a href="http://www.w3.org/TR/voicexml20/#dml3.1.6.3">
     * section 3.1.6.3</a> of the JVoiceXml standard.
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    private VoiceXmlDocument createGrammarExample() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();

        Form form = vxml.appendChild(Form.class);
        form.setId("exampleForm");

        Grammar grammar = form.appendChild(Grammar.class);
        grammar.setSrc("formlevel.grxml");

        Initial initial = form.appendChild(Initial.class);
        initial.addText("Say Something.");

        Field field = form.appendChild(Field.class);
        field.setName("x");
        grammar = field.appendChild(Grammar.class);
        grammar.setSrc("fieldx.grxml");

        field = form.appendChild(Field.class);
        field.setName("z");
        field.setSlot("y");
        grammar = field.appendChild(Grammar.class);
        grammar.setSrc("fieldz.grxml");

        return document;
    }

    /**
     * CCXML demo.
     * @return return CCXML doucment.
     */
    private CcxmlDocument createCcxmlExample() {
        final CcxmlDocument document;

        try {
            document = new CcxmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Ccxml ccxml = document.getCcxml();

        org.jvoicexml.xml.ccxml.Var var = ccxml.appendChild(
                org.jvoicexml.xml.ccxml.Var.class);
        var.setName("dialogid");

        Eventprocessor eventprocessor = ccxml.appendChild(Eventprocessor.class);
        eventprocessor.setStatevariable("mystate");

        Transition transition = eventprocessor.appendChild(Transition.class);
        transition.setEvent("dialog.disconnect");
        transition.setName("myevent");

        org.jvoicexml.xml.ccxml.Assign assign = transition.appendChild(
                org.jvoicexml.xml.ccxml.Assign.class);
        assign.setName("dialogid");
        assign.setExpr("myevent.dialogid");

        Disconnect disconnect = transition.appendChild(Disconnect.class);
        disconnect.setConnectionid("myevent.connectionid");

        transition = eventprocessor.appendChild(Transition.class);
        transition.setEvent("connection.disconnected");

        Dialogterminate dialogterminate =
            transition.appendChild(Dialogterminate.class);

        dialogterminate.setDialogid("dialogid");

        return document;
    }

    /**
     * Print the given VoiceXML document to <code>stdout</code>. Does nothing
     * if an error occurs.
     * @param document The VoiceXML document to print.
     * @return VoiceXML document as an XML string, <code>null</code> in case
     * of an error.
     */
    private String printDocument(final XmlDocument document) {
        final String xml;
        try {
            xml = document.toXml();
        } catch (IOException ioe) {
            ioe.printStackTrace();

            return null;
        }

        System.out.println(xml);

        return xml;
    }

    /**
     * The main method.
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'creation' demo for JVoiceXML...");
        LOGGER.info("(c) 2005-2007 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        final VoiceXMLCreationDemo demo = new VoiceXMLCreationDemo();

        // Simple example with variables
        XmlDocument document = demo.createHelloWorldWithVar();
        if (document == null) {
            return;
        }
        demo.printDocument(document);

        // Example with grammars and fields
        document = demo.createGrammarExample();
        if (document == null) {
            return;
        }
        demo.printDocument(document);

        // CCXML example
        document = demo.createCcxmlExample();
        if (document == null) {
            return;
        }
        demo.printDocument(document);
    }
}
