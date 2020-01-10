/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Demo implementation of the venerable "Hello World". This demo requires a
 * servlet container to deliver the VoiceXML documents.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 * <p>
 * This demo requires that JVoiceXML is configured with the jsapi20
 * implementation platform.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
public final class HelloWorldDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(HelloWorldDemo.class);

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

        final ConnectionInformation client =
            new BasicConnectionInformation("desktop", "jsapi20", "jsapi20");
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(client, id);

        session.call(uri);

        session.waitSessionEnd();

        session.hangup();
    }

    /**
     * The main method.
     *
     * @param args
     * Command line arguments.
     * <ol>
     * <li><code>&lt;URL&gt;</code> URL of the application root document.
     * </ol>
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'hello world' servlet demo for JVoiceXML...");
        LOGGER.info("(c) 2005-2019 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        if (args.length != 1) {
            System.out.println("usage:");
            System.out.println("java " + HelloWorldDemo.class.getName()
                    + " <URL>");
            System.exit(-1);
        }

        final HelloWorldDemo demo = new HelloWorldDemo();

        URI uri = null;
        try {
            final URL url = new URL(args[0]);
            uri = url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        }
    }
}
