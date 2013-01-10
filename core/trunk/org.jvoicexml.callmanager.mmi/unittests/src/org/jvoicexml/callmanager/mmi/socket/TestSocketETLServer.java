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
package org.jvoicexml.callmanager.mmi.socket;

import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.xml.Mmi;
import org.jvoicexml.mmi.events.xml.StartRequest;
import org.jvoicexml.mmi.events.xml.StartRequestBuilder;

/**
 * Test cases for {@link SocketETLServer}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class TestSocketETLServer implements MMIEventListener {
    /** Notification mechanism. */
    private Object lock;

    /** The received event. */
    private DecoratedMMIEvent event;

    /**
     * Set up the test environment
     */
    @Before
    public void setUp() {
        lock = new Object();
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.mmi.socket.SocketETLServer#run()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testRun() throws Exception {
        final SocketETLProtocolAdapter adapter = new SocketETLProtocolAdapter();
        adapter.addMMIEventListener(this);
        final SocketETLServer server = new SocketETLServer(adapter, 4242);
        server.start();
        Thread.sleep(500);
        final Socket client = new Socket("localhost", 4242);
        final StartRequestBuilder builder = new StartRequestBuilder();
        builder.setContextId("http://nowhere");
        builder.setRequestId("4242");
        final File file = new File("unittests/vxml/hello.vxml");
        final URI uri = file.toURI();
        builder.setHref(uri);
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
        final StartRequest request = builder.toStartRequest();
        final OutputStream out = client.getOutputStream();
        marshaller.marshal(request, out);
        out.flush();
        out.close();
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertTrue(event.getEvent() instanceof StartRequest);
        client.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivedEvent(final DecoratedMMIEvent evt) {
        event = evt;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
