/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.helloworlddemo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.vxml.Meta;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Demo implementation of the venerable "Hello World".
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class HelloWorldDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(HelloWorldDemo.class);

    /** The JNDI context. */
    private Context context;

    /**
     * Do not create from outside.
     */
    private HelloWorldDemo() {
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);

            context = null;
        }
    }

    /**
     * Create a simple VoiceXML document containing the hello world phrase.
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    private VoiceXmlDocument createHelloWorld() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();

        final Meta author = vxml.appendChild(Meta.class);
        author.setName("author");
        author.setContent("JVoiceXML group");

        final Meta copyright = vxml.appendChild(Meta.class);
        copyright.setName("copyright");
        copyright.setContent("2005-2010 JVoiceXML group - "
                             + "http://jvoicexml.sourceforge.net");

        final Form form = vxml.appendChild(Form.class);
        form.setId("say_hello");
        final Block block = form.appendChild(Block.class);
        block.addText("Hello World!");

        final Goto next = block.appendChild(Goto.class);
        next.setNext("#say_goodbye");

        final Form goodbyeForm = vxml.appendChild(Form.class);
        goodbyeForm.setId("say_goodbye");
        final Block goodbyeBlock = goodbyeForm.appendChild(Block.class);
        final Prompt prompt = goodbyeBlock.appendChild(Prompt.class);
        prompt.addText("Goodbye!");
//        final Audio audio = prompt.appendChild(Audio.class);
//        final File file = new File("test.wav");
//        final URI src = file.toURI();
//        audio.setSrc(src);

        return document;
    }

    /**
     * Print the given VoiceXML document to <code>stdout</code>. Does nothing
     * if an error occurs.
     * @param document The VoiceXML document to print.
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
     * @param document The only document in this application.
     * @return URI of the first document.
     */
    private URI addDocument(final VoiceXmlDocument document) {
        MappedDocumentRepository repository;
        try {
            repository = (MappedDocumentRepository)
                         context.lookup("MappedDocumentRepository");
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
     * Calls the VoiceXML interpreter context to process the given XML document.
     * @param uri URI of the first document to load
     * @exception JVoiceXMLEvent
     *            Error processing the call.
     */
    private void interpretDocument(final URI uri)
        throws JVoiceXMLEvent {
        JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);

            return;
        }

        final ConnectionInformation client = new BasicConnectionInformation(
                "dummy", "jsapi10", "jsapi10");
        final Session session = jvxml.createSession(client);

        session.call(uri);

        session.waitSessionEnd();

        session.hangup();
    }

    /**
     * The main method.
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'hello world' demo for JVoiceXML...");
        LOGGER.info("(c) 2005-2010 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        final HelloWorldDemo demo = new HelloWorldDemo();

        final VoiceXmlDocument document = demo.createHelloWorld();
        if (document == null) {
            return;
        }

        final String xml = demo.printDocument(document);
        if (xml == null) {
            return;
        }

        final URI uri = demo.addDocument(document);
        if (uri == null) {
            return;
        }

        try {
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        }
    }
}
