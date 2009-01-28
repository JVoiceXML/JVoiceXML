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

package org.jvoicexml.demo.helloworldservletdemo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Demo implementation of the venerable "Hello World". This demo requires a
 * servlet container to deliver the VoiceXML documents.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
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
     * Calls the VoiceXML interpreter context to process the given xml document.
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

        final Session session = jvxml.createSession(null);

        session.call(uri);

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
            LOGGER.info("(c) 2005-2007 by JVoiceXML group - "
                        + "http://jvoicexml.sourceforge.net/");
        }

        final HelloWorldDemo demo = new HelloWorldDemo();

        URI uri;
        try {
            final URL url = new URL(PROTOCOL, HOST, PORT, FILE);
            uri = url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        try {
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        }
    }
}
