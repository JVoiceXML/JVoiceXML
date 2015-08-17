package org.jvoicexml.documentserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Jetty handler to server generated documents from the {@link DocumentStorage}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
class DocumentHandler extends AbstractHandler {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(DocumentHandler.class);

    /** Reference to the document storage. */
    private final DocumentStorage storage;

    /**
     * Constructs a new object.
     * 
     * @param documentStorage
     *            the document storage
     */
    public DocumentHandler(final DocumentStorage documentStorage) {
        storage = documentStorage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final String target, final Request baseRequest,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        LOGGER.info("request from " + request.getRemoteAddr());
        final String requestUri = request.getRequestURI();
        try {
            final URI uri = new URI(requestUri);
            final GrammarDocument document = storage.getDocument(uri);
            final GrammarType type = document.getMediaType();
            final String contentType = getContentType(type);
            if (contentType != null) {
                response.setContentType(contentType);
            }
            final byte[] buffer = document.getBuffer();
            if (buffer == null) {
                LOGGER.warn("no document found at '" + uri + "'");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            final OutputStream out = response.getOutputStream();
            out.write(buffer);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (URISyntaxException e) {
            LOGGER.warn("unabale to create request uri '" + requestUri + "'", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private String getContentType(final GrammarType type) {
        if (type == GrammarType.SRGS_XML) {
            return "text/xml";
        } else if (type == GrammarType.SRGS_ABNF) {
            return "text/plain";
        } else if (type == GrammarType.GSL) {
            return "text/plain";
        } else {
            return null;
        }
    }
}
