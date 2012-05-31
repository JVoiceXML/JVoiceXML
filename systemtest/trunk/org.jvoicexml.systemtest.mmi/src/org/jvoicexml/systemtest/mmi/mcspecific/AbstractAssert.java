package org.jvoicexml.systemtest.mmi.mcspecific;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ClearContextRequest;
import org.jvoicexml.mmi.events.ClearContextRequestBuilder;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.CommonAttributeAdapter;
import org.jvoicexml.mmi.events.CommonResponseAttributeAdapter;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.StatusType;
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

    public void clearContext() throws IOException, JAXBException,
        URISyntaxException, InterruptedException, TestFailedException {
        final String contextId = getContextId();
        LOGGER.info("clearing context '" + contextId + "'...");
        final ClearContextRequestBuilder builder =
                new ClearContextRequestBuilder();
        builder.setContextId(contextId);
        final String requestId = createRequestId();
        builder.setRequestId(requestId);
        ClearContextRequest request = builder.toClearContextRequest();
        send(request);
        final MMIEvent clearResponse = waitForResponse("ClearContextResponse");
        if (!(clearResponse instanceof ClearContextResponse)) {
            throw new TestFailedException(
                    "expected a ClearContextResponse but got a "
                    + clearResponse.getClass());
        }
        checkIds(clearResponse, contextId, requestId);
        ensureSuccess(clearResponse);
        LOGGER.info("...context '" + contextId + "' cleared");
    }

    /**
     * Retrieves a unique context id for this test case.
     * @return the conext id.
     */
    public String getContextId() {
        return "http://mmisystemtest/" + getId();
    }

    /**
     * Creates a new request id.
     * @return a new request id
     */
    public String createRequestId() {
        final UUID id = UUID.randomUUID();
        return id.toString();
    }

    /**
     * Checks if the given request and context ids match the attributes of the
     * given event.
     * @param event the event to check
     * @param contextId the context id
     * @param requestId the request id
     * @throws TestFailedException
     *        if the ids do not match
     */
    public void checkIds(final MMIEvent event, final String contextId,
            final String requestId)
        throws TestFailedException {
        final CommonAttributeAdapter adapter =
                new CommonAttributeAdapter(event);
        final String eventContextId = adapter.getContext();
        if (!contextId.equals(eventContextId)) {
            throw new TestFailedException("Expected context id '" + contextId
                    + "' but have '" + eventContextId + "'");
        }
        final String eventRequestId = adapter.getRequestID();
        if (!requestId.equals(eventRequestId)) {
            throw new TestFailedException("Expected context id '" + contextId
                    + "' but have '" + requestId + "'");
        }
    }

    /**
     * Checks if the received response was successful.
     * @param event the received event
     * @throws TestFailedException
     *         if the response was not successful. The status info is set as the
     *         detailed error message.
     */
    public void ensureSuccess(final MMIEvent event) throws TestFailedException {
        final CommonResponseAttributeAdapter adapter =
                new CommonResponseAttributeAdapter(event);
        if (adapter.getStatus() != StatusType.SUCCESS) {
            final AnyComplexType any = adapter.getStatusInfo();
            final List<Object> content = any.getContent();
            final StringBuilder str = new StringBuilder();
            for (Object o : content) {
                str.append(o);
                str.append(System.getProperty("line.separator"));
            }
            throw new TestFailedException(str.toString());
        }
    }

    /**
     * Waits for a response from JVoiceXML.
     * @param expected short description of the expected response
     * @return the received event
     * @throws InterruptedException
     *         if the waiting was interrupted
     * @throws TestFailedException
     *         if the response did not arrive
     */
    protected MMIEvent waitForResponse(final String expected)
            throws InterruptedException, TestFailedException {
        synchronized (lock) {
            if (event != null) {
                return event;
            }
            lock.wait(20000);
        }
        if (event == null) {
            throw new TestFailedException("Timeout waiting for '" + expected
                    + "'");
        }

        // Clear the old event so that it is not detected any more
        final MMIEvent copy = event;
        event = null;
        return copy;
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