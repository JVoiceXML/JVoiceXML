package org.jvoicexml.callmanager.mmi.http;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jvoicexml.callmanager.mmi.CallMetadata;
import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.Mmi;

public class MmiHandler extends AbstractHandler {
    /** Registered listeners for MMI events. */
    private final Collection<MMIEventListener> listeners;
    /** Logger instance. */
    private static final Logger LOGGER = Logger.getLogger(MmiHandler.class);

    /**
     * Constructs a new object.
     */
    public MmiHandler() {
        listeners = new java.util.ArrayList<MMIEventListener>();
    }

    public void addMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about a received MMI Event.
     * 
     * @param protocol the used protocol
     * @param event
     *            the event to notify
     */
    void notifyMMIEvent(final String protocol, final DecoratedMMIEvent event) {
        final CallMetadata data = new CallMetadata();
        final String[] str = protocol.split("/"); 
        data.setProtocolName(str[0]);
        data.setProtocolVersion(str[1]);
        synchronized (listeners) {
            for (MMIEventListener listener : listeners) {
                listener.receivedEvent(event, data);
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void handle(final String target, final Request baseRequest,
            final HttpServletRequest request,
            final HttpServletResponse response)
                    throws IOException, ServletException {
        LOGGER.info("request from " + request.getRemoteAddr());
        response.setContentType("text/html;charset=utf-8");
        final Reader reader = request.getReader();
        try {
            JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            final Object o = unmarshaller.unmarshal(reader);
            if (o instanceof Mmi) {
                final Mmi mmi = (Mmi) o;
                LOGGER.info("received MMI event: " + mmi);
                final String protocol = request.getProtocol();
                final DecoratedMMIEvent event =
                        new DecoratedMMIEvent(this, mmi);
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        notifyMMIEvent(protocol, event);
                    }
                };
                final Thread thread = new Thread(runnable);
                thread.start();
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                LOGGER.warn("received unknown MMI object: " + o);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (JAXBException e) {
            LOGGER.error("unable to read input", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        baseRequest.setHandled(true);
    }
}
