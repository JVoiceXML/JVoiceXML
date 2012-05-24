/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.systemtest.mmi.mcspecific;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartRequestBuilder;

/**
 * Assertion 169.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class Assert169 {
    /** The logger instance. */
    private static final Logger LOGGER = Logger.getLogger(Assert169.class);

    /**
     * Executes the test case.
     */
    public void test() {
        try {
            final Socket client = new Socket("localhost", 4242);
            Thread.sleep(500);
            final StartRequestBuilder builder = new StartRequestBuilder();
            builder.setContextId("http://mmisystemtest/169");
            builder.setRequestId("4242");
            final File file = new File("vxml/helloworld.vxml");
            final URI uri = file.toURI();
            builder.setHref(uri);
            final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Marshaller marshaller = ctx.createMarshaller();
            final StartRequest request = builder.toStartRequest();
            final OutputStream out = client.getOutputStream();
            marshaller.marshal(request, out);
            LOGGER.info("sent '" + request + "'");
            out.flush();
            Thread.sleep(2000);
            out.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
