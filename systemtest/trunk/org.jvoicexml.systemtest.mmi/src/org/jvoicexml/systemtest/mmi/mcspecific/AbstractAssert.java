package org.jvoicexml.systemtest.mmi.mcspecific;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.CommonAttributeAdapter;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.systemtest.mmi.MMIEventListener;
import org.jvoicexml.systemtest.mmi.TestFailedException;

public abstract class AbstractAssert implements MMIEventListener {
    /** The logger instance. */
    private static final Logger LOGGER = Logger.getLogger(AbstractAssert.class);

    /** The source URI. */
    private URI source;

    /** Message lock. */
    protected final Object lock;

    /** The last received MMI event. */
    private MMIEvent event;

    public AbstractAssert() {
        lock = new Object();
    }

    /**
     * Retrieves the id of this test case.
     * @return
     */
    public abstract int getId();

    /**
     * Sets the source URI.
     * @param uri the source URI.
     */
    public void setSource(final URI uri) {
        source = uri;
    }

    /**
     * Sends the given MMI event to JVoiceXML.
     * @param request the event to send.
     * @throws IOException
     *         error sending
     * @throws JAXBException
     *         error marshalling the event
     * @throws URISyntaxException
     *         error determining the source attribute 
     */
    public void send(final MMIEvent request) throws IOException, JAXBException,
            URISyntaxException {
        event = null;

        final Socket client = new Socket("localhost", 4343);
        final CommonAttributeAdapter adapter =
                new CommonAttributeAdapter(request);
        adapter.setSource(source.toString());
        final URI target = TcpUriFactory.createUri(
                (InetSocketAddress) client.getRemoteSocketAddress());
        adapter.setTarget(target.toString());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return;
        }
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
        final OutputStream out = client.getOutputStream();
        marshaller.marshal(request, out);
        LOGGER.info("sent '" + request + "'");
        client.close();
    }

    /**
     * Creates a new request id.
     * @return a new request id
     */
    public String createRequestId() {
        final UUID id = UUID.randomUUID();
        return id.toString();
    }

    protected MMIEvent waitForResponse() throws InterruptedException,
            TestFailedException {
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
        synchronized (lock) {
            event = evt;
            lock.notifyAll();
        }
    }

    /**
     * Executes the test case.
     * @exception Exception
     *            test failed
     */
    public abstract void test() throws Exception;
}