/*
 * File:    $RCSfile: InputDemo.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2006 Dirk Schnelle (dirk.schnelle@web.de)
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

package org.jvoicexml.demo.inputdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.Nomatch;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Reprompt;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Demo implementation for an interaction with the user.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class InputDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(InputDemo.class);

    /** The JNDI context. */
    private Context context;

    /**
     * Do not create from outside.
     */
    private InputDemo() {
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);

            context = null;
        }
    }

    /**
     * Create the VoiceXML document.
     *
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    private VoiceXmlDocument createDocument() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();

        final Menu menu = vxml.addChild(Menu.class);
        menu.setId("mainmenu");

        final Prompt promptMenu = menu.addChild(Prompt.class);
        final Text textMenu = promptMenu.addText("Please enter");
        final Choice choiceList = menu.addChild(Choice.class);
        choiceList.setNext("#list");
        choiceList.setDtmf("1");
        final Text textList = choiceList.addText("1 to list the titles");
        final Choice choiceWatch = menu.addChild(Choice.class);
        choiceWatch.setNext("#watch");
        choiceWatch.setDtmf("2");
        final Text textWatch = choiceWatch.addText("2 to watch a movie");

        final Form formList = vxml.addChild(Form.class);
        formList.setId("list");
        final Block blockList = formList.addChild(Block.class);
        final Prompt promptList = blockList.addChild(Prompt.class);

        /** @todo Use object to create a real SSML contents. */
        final String titles = "lord of the rings "
                + "<break/> the magnificent seven "
                + "<break/> two thousand one a space odyssey "
                + "<break/> the matrix " + "<break/> finding nemo "
                + "<break/> spider man " + "<break/> mystic river "
                + "<break/> the italian job " + "<break/> chicago  "
                + "<break/> a beautiful mind " + "<break/> gladiator "
                + "<break/> american beauty "
                + "<break/> the magnificent seven "
                + "<break/> the magnificent seven ";
        /** @todo add other titles. */

        final Text textTitles = promptList.addText(titles);

        final Form formWatch = vxml.addChild(Form.class);
        formWatch.setId("watch");

        final Field field = formWatch.addChild(Field.class);
        final String fieldName = "movie";
        field.setName(fieldName);

        final Prompt prompt = field.addChild(Prompt.class);
        final Text text = prompt.addText("Which movie do you want to watch?");

        final Grammar grammar = field.addChild(Grammar.class);
        final File movies = new File("classes/movies.gram");
        grammar.setSrc(movies.toURI().toString());
        grammar.setType("application/x-jsgf");

        final Noinput noinput = field.addChild(Noinput.class);
        final Text noinputText = noinput.addText("Please say something!");
        final Reprompt repromptNoinput = noinput.addChild(Reprompt.class);

        final Noinput noinputSecond = field.addChild(Noinput.class);
        noinputSecond.setCount("2");
        final Text noinputSecondText =
                noinputSecond.addText("Please say a film title!");
        final Reprompt repromptNoinputSecond =
                noinputSecond.addChild(Reprompt.class);

        final Nomatch nomatch = field.addChild(Nomatch.class);
        final Text nomatchText = nomatch.addText("Please say a film title!");
        final Reprompt repromptNomatch = nomatch.addChild(Reprompt.class);

        /** @todo Move this into a filled section, when the scope works. */
        final Block block = formWatch.addChild(Block.class);
        final Text blockText = block.addText("You can watch the film");
        final Value blockValue = block.addChild(Value.class);
        blockValue.setExpr(fieldName);

        return document;
    }

    /**
     * Print the given VoiceXML document to <code>stdout</code>. Does nothing
     * if an error occurs.
     *
     * @param document
     * The VoiceXML document to print.
     * @return VoiceXML document as an XML string, <code>null</code> in case
     * of an error.
     */
    private String printDocument(final VoiceXmlDocument document) {
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
     * Add the given document as a single document application.
     *
     * @param document
     * The only document in this application.
     * @return Created application.
     */
    private Application registerApplication(final VoiceXmlDocument document) {
        ApplicationRegistry registry;
        try {
            registry = (ApplicationRegistry)
                       context.lookup("ApplicationRegistry");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the application registry", ne);

            return null;
        }

        MappedDocumentRepository repository;
        try {
            repository = (MappedDocumentRepository)
                         context.lookup("MappedDocumentRepository");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the documentrepository", ne);

            return null;
        }

        final URI uri = repository.getUri("/root");
        repository.addDocument(uri, document.toString());

        final Application application =
                registry.createApplication("inputdemo", uri);

        registry.register(application);

        return application;
    }

    /**
     * Call the voicexml interpreter context to process the given xml document.
     *
     * @param application
     * Id of the application.
     * @exception JVoiceXMLEvent
     *            Error processing the call
     */
    private void interpretDocument(final String application)
            throws JVoiceXMLEvent {
        JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);

            return;
        }

        final Session session = jvxml.createSession(null, application);

        session.call();

        final char dtmf = readDTMF();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("sending DTMF '" + dtmf + "'");
        }

        CharacterInput input = session.getCharacterInput();
        input.addCharacter(dtmf);

        session.waitSessionEnd();

        session.close();
    }

    /**
     * Read an input from the command line.
     * @return DTMF from the command line.
     */
    public char readDTMF() {
        LOGGER.info("Enter a DTMF and hit <return>. ");

        Reader reader = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(reader);

        try {
            String dtmf = br.readLine();
            dtmf = dtmf.trim();

            return dtmf.charAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * The main method.
     *
     * @param args
     * Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting 'input' demo for JVoiceXML...");
            LOGGER.info("(c) 2005-2006 by JVoiceXML group - "
                        + "http://jvoicexml.sourceforge.net/");
        }

        final InputDemo demo = new InputDemo();

        final VoiceXmlDocument document = demo.createDocument();
        if (document == null) {
            return;
        }

        final String xml = demo.printDocument(document);
        if (xml == null) {
            return;
        }

        final Application application = demo.registerApplication(document);
        if (application == null) {
            return;
        }

        try {
            demo.interpretDocument(application.getId());
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        }
    }
}
