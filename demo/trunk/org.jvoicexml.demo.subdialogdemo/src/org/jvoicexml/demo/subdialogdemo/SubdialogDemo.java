/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/demo/trunk/org.jvoicexml.demo.helloworlddemo/src/org/jvoicexml/demo/helloworlddemo/HelloWorldDemo.java $
 * Version: $LastChangedRevision: 1824 $
 * Date:    $Date: 2009-09-25 09:28:12 +0200 (Fr, 25 Sep 2009) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.demo.subdialogdemo;

import java.io.File;
import java.net.URI;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.client.GenericClient;
import org.jvoicexml.event.ErrorEvent;

/**
 * Demo implementation of the subdialog tag.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.4
 */
public final class SubdialogDemo {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(SubdialogDemo.class);

    /**
     * The main method.
     * @param args Command line arguments. None expected.
     */
    public static void main(final String[] args) {
        LOGGER.info("Starting 'subdialog' demo for JVoiceXML...");
        LOGGER.info("(c) 2010 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");
        final GenericClient client = new GenericClient();
        final File file = new File("subdialog.vxml");
        final URI subdialog = file.toURI();
        try {
            Session session = client.call(subdialog, "jsapi10", "jsapi10",
                    "jsapi10");
            session.waitSessionEnd();
            session.hangup();
        } catch (NamingException e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        } catch (ErrorEvent e) {
            LOGGER.fatal(e.getMessage(), e);
            return;
        }
    }

}
