package org.jvoicexml.demo.mmi.simpledemo;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.Mmi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Jetty HTT handler for MMI clients.
 * 
 * @author Dirk Schnelle-Walka
 * 
 */
public class MmiHandler extends AbstractHandler {
    /** The EMMA namespace. */
    private static final String EMMA_NAMESPACE =
            "http://www.w3.org/2003/04/emma";

    /** The JVoiceXML extension notification data namespace. */
    public static final String JVXML_MMI_NAMESPACE =
            "http://www.nowhere.org/jvxmlmmi";

    /** Logger instance. */
    private static final Logger LOGGER = LogManager.getLogger(MmiHandler.class);

    /** The demo. */
    private final SimpleMmiDemo demo;

    /**
     * Constructs a new object.
     * @param simpleMmiDemo reference to the demo
     */
    public MmiHandler(final SimpleMmiDemo simpleMmiDemo) {
        demo = simpleMmiDemo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final String target, final Request baseRequest,
            final HttpServletRequest request,
            final  HttpServletResponse response)
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
                        if (inputStarted(mmi)) {
                            final ExtensionNotification ext = mmi
                                    .getExtensionNotification();
                            final String contextId = ext.getContext();
                            final String source = ext.getSource();
                            final String target = ext.getTarget();
                            try {
                                sendYes(contextId, target, source);
                            } catch (Exception e) {
                                LOGGER.warn(e.getMessage(), e);
                            }
                        } else if (sessionTerminated(mmi)) {
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

    /**
     * Checks if the received MMI event is the done notification.
     * 
     * @param mmi
     *            received MMI event
     * @return {@code true} if the MMI event is the
     *         {@link org.jvoicexml.mmi.events.DoneNotification}
     */
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

    private boolean inputStarted(final Mmi mmi) {
        final ExtensionNotification extension = mmi.getExtensionNotification();
        if (extension == null) {
            return false;
        }
        final String name = extension.getName();
        return name.equals("vxml.input.start");
    }

    private void sendYes(final String contextId, final String source,
            final String target) throws ParserConfigurationException,
            URISyntaxException, JAXBException, IOException {
        final Mmi mmi = new Mmi();
        final ExtensionNotification notification = new ExtensionNotification();
        mmi.setExtensionNotification(notification);
        notification.setContext(contextId);
        notification.setRequestId("4343");
        notification.setSource(source);
        notification.setTarget(target);
        final AnyComplexType any = new AnyComplexType();
        notification.setData(any);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.newDocument();
        final Element emma = document.createElementNS(EMMA_NAMESPACE,
                "emma:emma");
        emma.setAttribute("version", "1.0");
        document.appendChild(emma);
        final Element interpretation = document.createElementNS(EMMA_NAMESPACE,
                "emma:interpretation");
        interpretation.setAttribute("id", "demoinput");
        interpretation
                .setAttributeNS(EMMA_NAMESPACE, "emma:medium", "acoustic");
        any.addContent(emma);
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:mode", "mmi");
        final float confidence = 0.9f;
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:confidence",
                Float.toString(confidence));
        final String tokens = "yes";
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens", tokens);
        emma.appendChild(interpretation);
        final URI targetUri = new URI(target);
        demo.send(mmi, targetUri);
    }
}
