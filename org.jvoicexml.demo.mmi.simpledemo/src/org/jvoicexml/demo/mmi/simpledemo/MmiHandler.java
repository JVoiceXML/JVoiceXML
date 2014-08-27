package org.jvoicexml.demo.mmi.simpledemo;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.Mmi;

/**
 * The Jetty HTT handler for MMI clients.
 * @author Dirk Schnelle-Walka
 *
 */
public class MmiHandler extends AbstractHandler {
    /** Logger instance. */
    private static final Logger LOGGER = Logger.getLogger(MmiHandler.class);

    /** The demo. */
    private final SimpleMmiDemo demo;

    /**
     * Constructs a new object.
     */
    public MmiHandler(final SimpleMmiDemo simpleMmiDemo) {
        demo = simpleMmiDemo;
    }

    @Override
    public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        LOGGER.info("request from " + request.getRemoteAddr());
        response.setContentType("text/html;charset=utf-8");
        final Reader reader = request.getReader();
        try {
            JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            final Object o = unmarshaller.unmarshal(reader);
            if (o instanceof Mmi) {
                LOGGER.info("received MMI event: " + o);
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        final Mmi mmi = (Mmi) o;
                        if(sessionTerminated(mmi)) {
                            demo.notifySessionEnd();
                        }
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
    
    private boolean sessionTerminated(final Mmi mmi) {
        final LifeCycleEvent evt = mmi.getLifeCycleEvent();
        if (!(evt instanceof DoneNotification)) {
            return false;
        }
        LOGGER.info("session terminated");
        final DoneNotification done = (DoneNotification) evt;
        final AnyComplexType any = done.getStatusInfo();
        if (any == null) {
            LOGGER.info("no status info");
            return true;
        }
        final List<Object> info = any.getContent();
        if (info.isEmpty()) {
            LOGGER.info("no status info");
            return true;
        }
        final Object content = info.get(0);
        LOGGER.info("input result: " + content);
        return true;
    }

}
