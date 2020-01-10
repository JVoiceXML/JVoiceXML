/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.File;
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
 * Demo implementation to demonstrate mixed initiative.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.2
 */
public final class MixedInitiativeDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LogManager.getLogger(MixedInitiativeDemo.class);

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
     * Call the voicexml interpreter context to process the given xml document.
     *
     * @param uri
     *            uri of the first document to load
     * @exception JVoiceXMLEvent
     *                Error processing the call.
     */
    private void interpretDocument(final URI uri) throws JVoiceXMLEvent {
        final JVoiceXml jvxml;
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
     *            Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting mixed initiative demo for JVoiceXML...");
        LOGGER.info("(c) 2009-2019 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");
        try {
            final MixedInitiativeDemo demo = new MixedInitiativeDemo();
            final File file = new File("test.vxml");
            final URI uri = file.toURI();
            LOGGER.info("interpreting document '" + uri + "'...");
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
        	LOGGER.error("error processing the document", e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
