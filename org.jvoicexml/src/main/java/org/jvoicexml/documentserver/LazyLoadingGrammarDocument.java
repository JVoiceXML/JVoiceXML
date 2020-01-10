/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2016 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver;

import java.net.URI;
import java.util.Objects;

import javax.activation.MimeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * A grammar document that does not prefetch the grammar document but just keeps
 * its URI and loads it once it is requested.
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class LazyLoadingGrammarDocument implements GrammarDocument {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(LazyLoadingGrammarDocument.class);

    /** The URI of the grammar document. */
    private URI uri;

    /** The MIME type of the grammar. */
    private final MimeType mimeType;
    
    /** The grammar type. */
    private GrammarType type;

    /** The mode type. */
    private ModeType mode;

    /** The session identifier. */
    private final SessionIdentifier sessionIdentifier;
    
    /** The document server to actually load the grammar. */
    private final DocumentServer server;
        
    /** The fetch attributes. */
    private final FetchAttributes attributes;
    
    /** The loaded document. */
    private GrammarDocument document;
   
    /**
     * Constructs a new object.
     * @param sessionId the session identifier
     * @param documentServer the document server to actually load the grammar
     * @param requestedMimeType the requested MIME type.
     * @param source the source URI
     * @param attrs the fetch attributes
     */
    public LazyLoadingGrammarDocument(final SessionIdentifier sessionId,
            final DocumentServer documentServer, 
            final MimeType requestedMimeType,
            final URI source, final FetchAttributes attrs) {
        sessionIdentifier = sessionId;
        server = documentServer;
        mimeType = requestedMimeType;
        attributes = attrs;
        // Clear a prefetch safe hint to enable loading later on.
        if (attributes.isFetchintSafe()) {
            attributes.setFetchHint(null);
        }
        uri = source;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCacheable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setURI(final URI source) {
        uri = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMediaType(final GrammarType gramamrType) {
        type = gramamrType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getMediaType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModeType(final ModeType modeType) {
        mode = modeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getModeType() {
        return mode;
    }

    /**
     * Lazy retrieval of the document.
     * @return the reireved document
     * @throws BadFetchError
     *          error obtaining the document
     */
    private GrammarDocument retrieveDocument() throws BadFetchError {
        if (document != null) {
            return document;
        }
        if (server == null) {
            LOGGER.warn("no server known. unable to load the document");
            return null;
        }
        document = server.getGrammarDocument(sessionIdentifier, uri,
                mimeType, attributes);
        return document;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAscii() {
        try {
            final GrammarDocument doc = retrieveDocument();
            return doc.isAscii();
        } catch (BadFetchError e) {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocument() {
        try {
            final GrammarDocument doc = retrieveDocument();
            return doc.getDocument();
        } catch (BadFetchError e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTextContent() {
        try {
            final GrammarDocument doc = retrieveDocument();
            return doc.getTextContent();
        } catch (BadFetchError e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBuffer() {
        try {
            final GrammarDocument doc = retrieveDocument();
            return doc.getBuffer();
        } catch (BadFetchError e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(attributes, document, mode, server,
                sessionIdentifier, type, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LazyLoadingGrammarDocument)) {
            return false;
        }
        LazyLoadingGrammarDocument other = (LazyLoadingGrammarDocument) obj;
        return equals(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(GrammarDocument obj) {
        if (!(obj instanceof LazyLoadingGrammarDocument)) {
            return false;
        }
        LazyLoadingGrammarDocument other = (LazyLoadingGrammarDocument) obj;
        return Objects.equals(attributes, other.attributes)
                && Objects.equals(document, other.document)
                && mode == other.mode && Objects.equals(server, other.server)
                && Objects.equals(sessionIdentifier, other.sessionIdentifier)
                && Objects.equals(type, other.type)
                && Objects.equals(uri, other.uri);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getCanonicalName());
        str.append('[');
        str.append(uri);
        str.append(',');
        str.append(mode);
        str.append(',');
        str.append(type);
        str.append(']');
        return str.toString();
    }
}
