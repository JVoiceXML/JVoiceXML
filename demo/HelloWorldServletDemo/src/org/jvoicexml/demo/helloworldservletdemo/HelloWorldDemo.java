/*
 * File:    $RCSfile: HelloWorldDemo.java,v $
 * Version: $Revision: 1.10 $
 * Date:    $Date: 2006/05/22 13:47:17 $
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

package org.jvoicexml.demo.helloworldservletdemo;

import java.net.URI;
import java.net.URL;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Demo implementation of the venerable "Hello World". This demo requires a
 * servlet container to deliver the VoiceXML documents.
 *
 * @author <a href="mailto:dirk.schnelle@web.de">Dirk Schnelle</a>
 * @version $Revision: 1.10 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class HelloWorldDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(HelloWorldDemo.class);

    /** The name of the protocol to use. */
    private static final String PROTOCOL = "http";

    /** Host, where the servlet engine is running. */
    private static final String HOST = "127.0.0.1";

    /** Port of the servlet engine. */
    private static final int PORT = 8080;

    /** File on the host. */
    private static final String FILE = "/helloworldservletdemo/JVoiceXML";

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
     * Add the URL as the single document application.
     *
     * @return Created application.
     */
    private Application registerApplication() {
        ApplicationRegistry registry;
        try {
            registry = (ApplicationRegistry)
                       context.lookup("ApplicationRegistry");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the application registry", ne);

            return null;
        }

        final URI uri;

        try {
            final URL url = new URL(PROTOCOL, HOST, PORT, FILE);
            uri = url.toURI();
        } catch (java.net.MalformedURLException mue) {
            mue.printStackTrace();

            return null;
        } catch (java.net.URISyntaxException urise) {
            urise.printStackTrace();

            return null;
        }

        final Application application =
                registry.createApplication("helloworldservlet", uri);

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
            LOGGER.info("Starting 'hello world' servlet demo for JVoiceXML...");
            LOGGER.info("(c) 2005-2006 by JVoiceXML group - "
                        + "http://jvoicexml.sourceforge.net/");
        }

        final HelloWorldDemo demo = new HelloWorldDemo();

        final Application application = demo.registerApplication();
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
