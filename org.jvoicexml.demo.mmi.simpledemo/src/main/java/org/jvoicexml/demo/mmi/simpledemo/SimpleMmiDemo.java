/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.mmi.simpledemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.StartRequest;

/**
 * A simple demo for JVoiceXML using the MMI callmanager.
 * <p>
 * Must be run with the system property
 * <code>-Djava.security.policy=${config}/jvoicexml.policy</code> and the
 * <code>config</code> folder added to the classpath.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 */
public final class SimpleMmiDemo {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(SimpleMmiDemo.class);

    /** Synchronization lock. */
    private final Object lock;

    /**
     * Constructs a new object.
     */
    public SimpleMmiDemo() {
        lock = new Object();
    }

    /**
     * Notifies about the end of the session
     */
    public void notifySessionEnd() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * Waits for the end of the session.
     */
    private void waitSessionEnd() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Executes a session with a predefindedVoiceXML document.
     * 
     * @throws URISyntaxException
     * @throws JAXBException
     * @throws IOException
     * @throws ClientProtocolException
     */
    private void call() throws URISyntaxException, JAXBException, IOException,
            ClientProtocolException {
        final String context = UUID.randomUUID().toString();
        int requestId = 4242;
        final Mmi mmi = new Mmi();
        final StartRequest start = new StartRequest();
        mmi.setStartRequest(start);
        final URI source = new URI("http://localhost:9092");
        start.setSource(source.toString());
        final URI target = new URI("http://localhost:9090");
        start.setTarget(target.toString());
        start.setContext(context);
        start.setRequestId(Integer.toString(requestId));
        final URI uri = SimpleMmiDemo.class.getResource("/simpleexample.vxml").toURI();
        start.setContentURL(uri);
        send(mmi, target);
    }

    public void send(final Mmi mmi, final URI target) throws JAXBException,
            IOException {
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(mmi, out);
        final HttpClient client = new DefaultHttpClient();
        final HttpPost post = new HttpPost(target);
        final HttpEntity entity = new StringEntity(out.toString(),
                ContentType.APPLICATION_XML);
        post.setEntity(entity);
        client.execute(post);
        LOGGER.info("sending " + mmi + " to '" + target + "'");
    }

    /**
     * The main method.
     * 
     * @param args
     *            Command line arguments. None expected.
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        LOGGER.info("Starting 'simple mmi' demo for JVoiceXML...");
        LOGGER.info("(c) 2014 by JVoiceXML group - "
                + "http://jvoicexml.sourceforge.net/");

        // Start the web server
        final Server server = new Server(9092);
        final SimpleMmiDemo demo = new SimpleMmiDemo();
        final Handler handler = new MmiHandler(demo);
        server.setHandler(handler);
        server.start();

        demo.call();

        // Wait for the end of the session
        demo.waitSessionEnd();
        server.stop();
    }
}
