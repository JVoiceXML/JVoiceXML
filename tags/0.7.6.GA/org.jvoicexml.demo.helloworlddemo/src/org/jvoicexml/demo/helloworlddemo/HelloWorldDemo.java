/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.helloworlddemo;

import java.io.File;
import java.net.URI;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.client.GenericClient;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;
import org.jvoicexml.event.ErrorEvent;

/**
 * Demo implementation of the venerable "Hello World".
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and
 * the <code>config</code> folder added to the classpath.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class HelloWorldDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(HelloWorldDemo.class);

    /**
     * The main method.
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'hello world' demo for JVoiceXML...");
        LOGGER.info("(c) 2005-2013 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        final GenericClient client = new GenericClient();
        final File file = new File("helloworld.vxml");
        final URI dialog = file.toURI();
        try {
            Session session = client.call(dialog, "jsapi10", "jsapi10",
                    "dummy");
            session.waitSessionEnd();
            session.hangup();
        } catch (NamingException e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        } catch (ErrorEvent e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        } finally {
            client.close();
        }
    }
}