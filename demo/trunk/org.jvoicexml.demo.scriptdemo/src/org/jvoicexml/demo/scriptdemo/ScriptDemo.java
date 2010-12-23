/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.demo.scriptdemo;

import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

/**
 * Demo implementation to demonstrate scripting and var-handling.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 *
 * @author Torben Hardt
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class ScriptDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ScriptDemo.class);

    /** The JNDI context. */
    private Context context;

    /**
     * Do not create from outside.
     */
    private ScriptDemo() {
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error creating initial context", ne);

            context = null;
        }
    }

    /**
     * Add the given document.
     *
     * @param path
     *            the path where to add the document.
     * @param document
     *            the only document in this application.
     * @return URI of the document.
     */
    private URI addDocument(final String path,
            final VoiceXmlDocument document) {
        MappedDocumentRepository repository;
        try {
            repository = (MappedDocumentRepository)
                context.lookup("MappedDocumentRepository");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the documentrepository", ne);

            return null;
        } catch (Exception e) {
            LOGGER.error("error obtaining the documentrepository", e);
            return null;
        }

        final URI uri;
        try {
            uri = repository.getUri(path);
        } catch (URISyntaxException e) {
            LOGGER.error("error creating the URI", e);
            return null;
        }
        repository.addDocument(uri, document.toString());

        return uri;
    }

    /**
     * Call the voicexml interpreter context to process the given xml document.
     *
     * @param uri
     *            uri of the first document to load
     * @exception JVoiceXMLEvent
     *                Error processing the call.
     */
    private void interpretDocument(final URI uri) throws JVoiceXMLEvent {
        JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);

            return;
        }

        final ConnectionInformation client =
            new BasicConnectionInformation("dummy", "jsapi10", "jsapi10");
        final Session session = jvxml.createSession(client);

        session.call(uri);

        /** @todo Enable remote access to the scripting engine. */
        // final VoiceXmlInterpreterContext context =
        // session.getVoiceXmlInterpreterContext();
        // final Session session = jvxml.createSession(null, application);
        // // add a test-var to the application, see test1.xml how to use it.
        // final ScriptingEngine scripting = context.getScriptingEngine();
        // scripting.setVariable("demovar1", "'test me please!'");
        session.waitSessionEnd();
        session.hangup();
    }

    /**
     * The main method.
     *
     * @param args
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting sripting demo for JVoiceXML...");
        LOGGER.info("(c) 2005-2010 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");
        try {
            final ScriptDemo demo = new ScriptDemo();
            final InputSource rootInput = new InputSource("root.vxml");
            final VoiceXmlDocument root = new VoiceXmlDocument(rootInput);
            final InputSource startInput = new InputSource("scriptdemo.vxml");
            final VoiceXmlDocument document = new VoiceXmlDocument(startInput);

            demo.addDocument("/root", root);
            final URI uri = demo.addDocument("/start", document);
            if (uri == null) {
                return;
            }

            LOGGER.info("interpreting document '" + uri + "'...");
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
        	LOGGER.error("error processing the document", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
