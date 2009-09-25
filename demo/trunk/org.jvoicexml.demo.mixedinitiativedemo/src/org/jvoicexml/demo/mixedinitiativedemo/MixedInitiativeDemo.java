/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/demo/trunk/org.jvoicexml.demo.scriptdemo/src/org/jvoicexml/demo/scriptdemo/ScriptDemo.java $
 * Version: $LastChangedRevision: 1718 $
 * Date:    $Date: 2009-07-22 09:12:18 +0200 (Mi, 22 Jul 2009) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.mixedinitiativedemo;

import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicRemoteClient;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.XmlDocument;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

/**
 * Demo implementation to demonstrate mixed initiative.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1718 $
 * @since 0.7.2
 */
public final class MixedInitiativeDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MixedInitiativeDemo.class);

    /** The JNDI context. */
    private Context context;

    /**
     * Do not create from outside.
     */
    private MixedInitiativeDemo() {
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
            final XmlDocument document) {
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

        final RemoteClient client = new BasicRemoteClient("jsapi10", "jsapi10",
            "jsapi10");
        final Session session = jvxml.createSession(client);

        session.call(uri);

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
        LOGGER.info("Starting mixed initiative demo for JVoiceXML...");
        LOGGER.info("(c) 2009 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");
        try {
            final MixedInitiativeDemo demo = new MixedInitiativeDemo();
            final InputSource vxml = new InputSource("pizza.vxml");
            final VoiceXmlDocument document = new VoiceXmlDocument(vxml);
            final InputSource grxml = new InputSource("pizza.grxml");
            final SrgsXmlDocument grxmldoc = new SrgsXmlDocument(grxml);

            demo.addDocument("/pizza.grxml", grxmldoc);
            final URI uri = demo.addDocument("/start", document);
            if (uri == null) {
                return;
            }

            LOGGER.info("interpreting document '" + uri + "'...");
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
        	LOGGER.error("error processing the document", e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
