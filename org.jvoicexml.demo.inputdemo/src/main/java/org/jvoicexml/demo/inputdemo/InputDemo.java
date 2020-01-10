/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2019 JVoiceXML group
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.ssml.Break;
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
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and the
 * <code>config</code> folder added to the classpath.
 * </p>
 * <p>
 * This demo requires that JVoiceXML is configured with the jsapi20
 * implementation platform.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
public final class InputDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(InputDemo.class);

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
     * @return Created VoiceXML document, <code>null</code> if an error occurs.
     * @throws URISyntaxException
     *             error creating the document
     * @throws ParserConfigurationException
     *             error creating the document
     */
    private VoiceXmlDocument createDocument()
            throws URISyntaxException, ParserConfigurationException {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang(Locale.US);

        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("mainmenu");

        final Prompt promptMenu = menu.appendChild(Prompt.class);
        promptMenu.addText(
                "Please enter 1 to list the titles or 2 to watch a movie");
        final Choice choiceList = menu.appendChild(Choice.class);
        choiceList.setNext("#list");
        choiceList.setDtmf("1");
        final Choice choiceWatch = menu.appendChild(Choice.class);
        choiceWatch.setNext("#watch");
        choiceWatch.setDtmf("2");

        final Form formList = vxml.appendChild(Form.class);
        formList.setId("list");
        final Block blockList = formList.appendChild(Block.class);
        final Prompt promptList = blockList.appendChild(Prompt.class);

        promptList.addText("lord of the rings");
        promptList.appendChild(Break.class);
        promptList.addText("the magnificent seven");
        promptList.appendChild(Break.class);
        promptList.addText("two thousand one a space odyssey");
        promptList.appendChild(Break.class);
        promptList.addText("the matrix");
        promptList.appendChild(Break.class);
        promptList.addText("finding nemo");
        promptList.appendChild(Break.class);
        promptList.addText("spider man");
        promptList.appendChild(Break.class);
        promptList.addText("mystic river");
        promptList.appendChild(Break.class);
        promptList.addText("the italian job");
        promptList.appendChild(Break.class);
        promptList.addText("chicago");
        promptList.appendChild(Break.class);
        promptList.addText("a beautiful mind");
        promptList.appendChild(Break.class);
        promptList.addText("gladiator");
        promptList.appendChild(Break.class);
        promptList.addText("american beauty");
        promptList.appendChild(Break.class);
        promptList.addText("the magnificant seven");

        final Form formWatch = vxml.appendChild(Form.class);
        formWatch.setId("watch");

        final Field field = formWatch.appendChild(Field.class);
        final String fieldName = "movie";
        field.setName(fieldName);

        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Which movie do you want to watch?");
        prompt.setTimeout("10s");

        final Grammar grammar = field.appendChild(Grammar.class);
        final URI grammarUri = InputDemo.class.getResource("/movies.srgs")
                .toURI();
        grammar.setSrc(grammarUri);
        grammar.setType(GrammarType.SRGS_XML);

        final Noinput noinput = field.appendChild(Noinput.class);
        noinput.addText("Please say something!");
        noinput.appendChild(Reprompt.class);

        final Noinput noinputSecond = field.appendChild(Noinput.class);
        noinputSecond.setCount("2");
        noinputSecond.addText("Please say a film title!");
        noinputSecond.appendChild(Reprompt.class);

        final Nomatch nomatch = field.appendChild(Nomatch.class);
        nomatch.addText("Please say a film title!");
        nomatch.appendChild(Reprompt.class);

        /** @todo Move this into a filled section, when the scope works. */
        final Block block = formWatch.appendChild(Block.class);
        block.addText("You can watch the film");
        final Value blockValue = block.appendChild(Value.class);
        blockValue.setExpr(fieldName);

        return document;
    }

    /**
     * Print the given VoiceXML document to <code>stdout</code>. Does nothing if
     * an error occurs.
     *
     * @param document
     *            The VoiceXML document to print.
     * @return VoiceXML document as an XML string, <code>null</code> in case of
     *         an error.
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
     *            The only document in this application.
     * @return URI of the first document.
     */
    private URI addDocument(final VoiceXmlDocument document) {
        MappedDocumentRepository repository;
        try {
            repository = (MappedDocumentRepository) context
                    .lookup("MappedDocumentRepository");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the documentrepository", ne);

            return null;
        }

        final URI uri;
        try {
            uri = repository.getUri("/root");
        } catch (URISyntaxException e) {
            LOGGER.error("error creating the URI", e);
            return null;
        }
        repository.addDocument(uri, document.toString());

        return uri;
    }

    /**
     * Call the VoiceXML interpreter context to process the given XML document.
     *
     * @param uri
     *            URI of the first document to load
     * @exception JVoiceXMLEvent
     *                Error processing the call
     */
    private void interpretDocument(final URI uri) throws JVoiceXMLEvent {
        JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);

            return;
        }

        final ConnectionInformation client = new BasicConnectionInformation(
                "desktop", "jsapi20", "jsapi20");
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(client, id);

        session.call(uri);

        final char dtmf = readDTMF();

        LOGGER.info("sending DTMF '" + dtmf + "'");

        DtmfInput input = session.getDtmfInput();
        input.addDtmf(dtmf);

        session.waitSessionEnd();

        session.hangup();
    }

    /**
     * Read an input from the command line.
     * 
     * @return DTMF from the command line.
     */
    public char readDTMF() {
        LOGGER.info("Enter a DTMF and hit <return>. ");

        final Reader reader = new InputStreamReader(System.in);
        final BufferedReader br = new BufferedReader(reader);

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
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'input' demo for JVoiceXML...");
        LOGGER.info("(c) 2005-2019 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        final InputDemo demo = new InputDemo();

        try {
            final VoiceXmlDocument document = demo.createDocument();
            demo.printDocument(document);
            final URI uri = demo.addDocument(document);

            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent | URISyntaxException
                | ParserConfigurationException e) {
            LOGGER.error("error processing the document", e);
        }
    }
}
