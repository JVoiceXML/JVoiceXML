/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/demo/trunk/org.jvoicexml.demo.helloworlddemo/src/org/jvoicexml/demo/helloworlddemo/HelloWorldDemo.java $
 * Version: $LastChangedRevision: 2440 $
 * Date:    $Date: 2010-12-23 09:18:46 +0100 (Do, 23 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2011-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.marcdemo;

import java.io.File;
import java.net.URI;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Demo implementation of the venerable "Hello World".
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2440 $
 */
public final class MarcDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MarcDemo.class);

    /** The JNDI context. */
    private Context context;

    /**
     * Do not create from outside.
     * @exception NamingException
     *            error creating the initial context
     */
    private MarcDemo() throws NamingException {
        context = new InitialContext();
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
                "dummy", "marc", "jsapi10");
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
        LOGGER.info("Starting marc demo for JVoiceXML...");
        LOGGER.info("(c) 2011-2012 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        try {
            final MarcDemo demo = new MarcDemo();
            final File dialog = new File("hello.vxml");
            final URI uri = dialog.toURI();
            demo.interpretDocument(uri);
        } catch (org.jvoicexml.event.JVoiceXMLEvent e) {
            LOGGER.error("error processing the document: " + e.getMessage(), e);
        } catch (NamingException e) {
            LOGGER.error("error obtaining the initial context: "
                    + e.getMessage(), e);
        }
    }
}
