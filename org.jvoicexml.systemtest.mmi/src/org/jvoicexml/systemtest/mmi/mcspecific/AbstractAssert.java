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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jvoicexml.client.TcpUriFactory;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ClearContextRequest;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.LifeCycleRequest;
import org.jvoicexml.mmi.events.LifeCycleResponse;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StatusType;
import org.jvoicexml.systemtest.mmi.MMIEventListener;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Base class for MMI test assertions.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public abstract class AbstractAssert implements MMIEventListener {
    /** The logger instance. */
    private static final Logger LOGGER = Logger.getLogger(AbstractAssert.class);

    /** The source URI. */
    private URI source;

    /** Message lock. */
    private final Object lock;

    /** The last received MMI event. */
    private LifeCycleEvent event;

    /** Possible additional notes. */
    private String notes;

    /** <code>true</code> if the context is cleared. */
    private boolean clearedContext;

    /** An atomic counter for the request ids. */
    private static AtomicLong currentId;

    static {
        currentId = new AtomicLong();
    }

    /**
     * Constructs a new Object.
     */
    public AbstractAssert() {
        lock = new Object();
        clearedContext = true;
    }

    /**
     * Retrieves the id of this test case.
     * @return the id of this test case
     */
    public abstract int getId();

    /**
     * Retrieves possible additional notes.
     * @return notes, maybe <code>null</code> if there are no notes.
     */
    public final String getNotes() {
        return notes;
    }

    /**
     * Sets additional notes.
     * @param value the notes.
     */
    public final void setNotes(final String value) {
        notes = value;
    }

    /**
     * Sets the source URI.
     * @param uri the source URI.
     */
    public final void setSource(final URI uri) {
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
    public final void send(final LifeCycleEvent request)
            throws IOException, JAXBException,
            URISyntaxException {
        event = null;

        final Socket client = new Socket("localhost", 4343);
        request.setSource(source.toString());
        final URI target = TcpUriFactory.createUri(
                (InetSocketAddress) client.getRemoteSocketAddress());
        request.setTarget(target.toString());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            client.close();
            return;
        }
        final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
        final Marshaller marshaller = ctx.createMarshaller();
        final OutputStream out = client.getOutputStream();
        marshaller.marshal(request, out);
        LOGGER.info("sent '" + request + "'");
        client.close();
        if ((request instanceof StartRequest)
                || (request instanceof PrepareRequest)) {
            clearedContext = false;
        }
    }

    /**
     * Notification that a clear context request must be sent.
     */
    protected final void needToClearContext() {
        clearedContext = false;
    }

    /**
     * Clears the context after the test has run by sending a clear context
     * message.
     * @throws IOException
     *         error sending the message
     * @throws JAXBException
     *         error marshalling the message
     * @throws URISyntaxException
     *         if the target could not be resolved
     * @throws InterruptedException
     *         in case of a timeout waiting for a response
     * @throws TestFailedException
     *         if sending fails in general
     */
    public final void clearContext() throws IOException, JAXBException,
        URISyntaxException, InterruptedException, TestFailedException {
        if (clearedContext) {
            return;
        }
        final String contextId = getContextId();
        LOGGER.info("clearing context '" + contextId + "'...");
        final ClearContextRequest request =
                new ClearContextRequest();
        request.setContext(contextId);
        final String requestId = createRequestId();
        request.setRequestId(requestId);
        send(request);
        final LifeCycleEvent clearResponse = waitForResponse(
                "ClearContextResponse");
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
     * @return the context id.
     */
    public final String getContextId() {
        return "http://mmisystemtest/" + getId();
    }

    /**
     * Creates a new request id.
     * @return a new request id
     */
    public final String createRequestId() {
        long requestId = currentId.addAndGet(1);
        return Long.toString(requestId);
    }

    /**
     * Checks if the given request and context ids match the attributes of the
     * given event.
     * @param evt the event to check
     * @param contextId the context id
     * @param requestId the request id
     * @throws TestFailedException
     *        if the ids do not match
     */
    public final void checkIds(final LifeCycleEvent evt, final String contextId,
            final String requestId)
        throws TestFailedException {
        final String eventContextId;
        if (evt instanceof LifeCycleRequest) {
            final LifeCycleRequest request = (LifeCycleRequest) evt;
            eventContextId = request.getContext();
        } else if (evt instanceof LifeCycleResponse) {
            final LifeCycleResponse response = (LifeCycleResponse) evt;
            eventContextId = response.getContext();
        } else {
            throw new TestFailedException(
                    "event does not contain a context id (" + evt.getClass()
                    + ")");
        }
        if (!contextId.equals(eventContextId)) {
            final String message = "Expected context id '" + contextId
                    + "' but have '" + eventContextId + "' in "
                    + evt.getClass().getCanonicalName();
            LOGGER.warn(message);
            throw new TestFailedException(message);
        }
        final String eventRequestId = evt.getRequestId();
        if (!requestId.equals(eventRequestId)) {
            final String message = "Expected request id '" + requestId
                    + "' but have '" + eventRequestId + "' in "
                    + evt.getClass().getCanonicalName();
            LOGGER.warn(message);
            throw new TestFailedException(message);
        }
    }

    /**
     * Checks if the received response was successful.
     * @param evt the received event
     * @throws TestFailedException
     *         if the response was not successful. The status info is set as the
     *         detailed error message.
     */
    public final void ensureSuccess(final LifeCycleEvent evt)
            throws TestFailedException {
        if (!(evt instanceof LifeCycleResponse)) {
            throw new TestFailedException(
            "received event was not a LifeCycleResponse");
        }
        final LifeCycleResponse response = (LifeCycleResponse) evt;
        if (response.getStatus() != StatusType.SUCCESS) {
            final AnyComplexType any = response.getStatusInfo();
            final List<Object> content = any.getContent();
            final StringBuilder str = new StringBuilder();
            for (Object o : content) {
                str.append(o);
                str.append(System.getProperty("line.separator"));
            }
            final String message = str.toString();
            LOGGER.warn("call was not successful: '" + message + "'");
            throw new TestFailedException(message);
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
    protected final LifeCycleEvent waitForResponse(final String expected)
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
        final LifeCycleEvent copy = event;
        event = null;
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void receivedEvent(final LifeCycleEvent evt) {
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
