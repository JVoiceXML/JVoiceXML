/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Demo implementation to demonstrate scripting and var-handling.
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
 * @author Torben Hardt
 * @author Dirk Schnelle-Walka
 */
public final class ScriptDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(ScriptDemo.class);

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
     * Call the VoiceXML interpreter context to process the given XML document.
     *
     * @param uri
     *            URI of the first document to load
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

        final ConnectionInformation client = new BasicConnectionInformation(
                "desktop", "jsapi20", "jsapi20");
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(client, id);

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
        LOGGER.info("(c) 2005-2019 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");
        try {
            final ScriptDemo demo = new ScriptDemo();
            final URI uri = ScriptDemo.class.getResource("/scriptdemo.vxml")
                    .toURI();
            LOGGER.info("interpreting document '" + uri + "'...");
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
