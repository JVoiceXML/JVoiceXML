/*
 * File:    $RCSfile: ScriptDemo.java,v $
 * Version: $Revision: 1.13 $
 * Date:    $Date: 2006/04/19 11:05:18 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.xml.sax.InputSource;

/**
 * Demo implementation to demonstrate scripting and var-handling.
 *
 * @author Torben Hardt
 * @author Dirk Schnelle
 *
 * @author <a href="mailto:dirk.schnelle@web.de">Dirk Schnelle</a>
 * @version $Revision: 1.13 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/</a>
 * </p>
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
                registry.createApplication("scriptdemo", uri);

        System.out.println(application);
        registry.register(application);

        return application;
    }

    /**
     * Call the voicexml interpreter context to process the given xml document.
     *
     * @param application
     * Id of the application.
     * @exception JVoiceXMLEvent
     *            Error processing the call.
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

        /** @todo Enable remote access to the scripting engine. */
//        final VoiceXmlInterpreterContext context =
//                session.getVoiceXmlInterpreterContext();
//        final Session session = jvxml.createSession(null, application);
//        // add a test-var to the application, see test1.xml how to use it.
//        final ScriptingEngine scripting = context.getScriptingEngine();
//        scripting.setVariable("demovar1", "'test me please!'");

        session.call();
        session.waitSessionEnd();
        session.close();
    }

    /**
     * The main method.
     *
     * @param args
     * Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Starting sripting demo for JVoiceXML...");
            LOGGER.info("(c) 2005-2006 by JVoiceXML group - "
                        + "http://jvoicexml.sourceforge.net/");
        }
        try {
            final ScriptDemo demo = new ScriptDemo();
            VoiceXmlDocument document = null;

            if (args.length > 0) {
                document = new VoiceXmlDocument(new InputSource(args[0]));
            } else {
                LOGGER.warn("No commandline parameter given, exiting");
            }

            if (document == null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
