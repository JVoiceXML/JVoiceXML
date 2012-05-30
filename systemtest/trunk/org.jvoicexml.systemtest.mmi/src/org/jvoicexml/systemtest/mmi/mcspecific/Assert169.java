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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartRequestBuilder;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.systemtest.mmi.MMIEventListener;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 169: If the IM includes a value in the ContentURL or Content field
 * of the StartRequest event, the Modality Component MUST use this value.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class Assert169  implements MMIEventListener {
    /** The logger instance. */
    private static final Logger LOGGER = Logger.getLogger(Assert169.class);

    /** The source URI. */
    private URI source;

    /** Message lock. */
    private final Object lock;

    /** The last received MMI event. */
    private MMIEvent event;

    /**
     * Constructs a new object.
     */
    public Assert169() {
        lock = new Object();
    }

    /**
     * Retrieves the id of this test case.
     * @return
     */
    public int getId() {
        return 169;
    }

    /**
     * Sets the source URI.
     * @param uri the source URI.
     */
    public void setSource(final URI uri) {
        source = uri;
    }
    /**
     * Executes the test case.
     * @param report the current report
     * @exception Exception
     *            test failed
     */
    public void test() throws Exception {
        Socket client = new Socket("localhost", 4343);
        Thread.sleep(500);
        final StartRequestBuilder builder = new StartRequestBuilder();
        builder.setContextId("http://mmisystemtest/169");
        builder.setRequestId("4242");
        builder.setSource(source);
        final URI target = TcpUriFactory.createUri(
                (InetSocketAddress) client.getRemoteSocketAddress());
        builder.setTarget(target);
        final File file = new File("vxml/helloworld.vxml");
        final URI uri = file.toURI();
        builder.setHref(uri);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
        final StartRequest request = builder.toStartRequest();
        final OutputStream out = client.getOutputStream();
        marshaller.marshal(request, out);
        LOGGER.info("sent '" + request + "'");
        client.close();
        final MMIEvent startReponse = waitForResponse();
        if (!(startReponse instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse.getClass());
        }
        final MMIEvent doneNotification = waitForResponse();
        if (!(doneNotification instanceof DoneNotification)) {
            throw new TestFailedException(
                    "expected a DoneNotification but got a "
                    + startReponse.getClass());
        }
    }

    private MMIEvent waitForResponse()
            throws InterruptedException, TestFailedException {
        event = null;
        synchronized (lock) {
            lock.wait(60000);
        }
        if (event == null) {
            throw new TestFailedException("Timeout waiting for a response");
        }
        return event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivedEvent(final MMIEvent evt) {
        event = evt;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
