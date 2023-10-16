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
import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.client.jndi.RemoteJVoiceXml;
import org.jvoicexml.event.JVoiceXMLEvent;

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
     * Call the VoiceXML interpreter context to process the given XML document.
     *
     * @param uri
     *            URI of the first document to load
     * @exception JVoiceXMLEvent
     *                Error processing the call
     * @throws RemoteException 
     *                  JVoiceXML not found
     */
    private void interpretDocument(final URI uri) 
            throws JVoiceXMLEvent, RemoteException {
        RemoteJVoiceXml jvxml;
        try {
            final String jndi = RemoteJVoiceXml.class.getSimpleName();
            jvxml = (RemoteJVoiceXml) context.lookup(jndi);
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining JVoiceXml", ne);

            return;
        }

        final ConnectionInformation client = new BasicConnectionInformation(
                "desktop", "jsapi20", "jsapi20");
        final SessionIdentifier id = new UuidSessionIdentifier();
        final Session session = jvxml.createSession(client, id);

        session.call(uri);

        final DtmfInput input = session.getDtmfInput();
        final char dtmf = readDTMF();
        if (dtmf > 0) {
            LOGGER.info("sending DTMF '" + dtmf + "'");
            input.addDtmf(dtmf);
            session.waitSessionEnd();
        }

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
            // Reading from stdin does not work from within gradle
            String dtmf = br.readLine();
            if (dtmf == null) {
                System.out.println(
                        "No input received. Did you start from gradle?");
                return 0;
            }
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
        LOGGER.info("(c) 2005-2023 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        final InputDemo demo = new InputDemo();

        try {
            final URI uri = InputDemo.class.getResource("/movies.vxml").toURI();
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent | URISyntaxException
                | RemoteException e) {
            LOGGER.error("error processing the document", e);
        }
    }
}
