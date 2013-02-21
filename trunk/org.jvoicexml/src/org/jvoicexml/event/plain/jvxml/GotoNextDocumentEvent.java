/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain.jvxml;

import java.net.URI;

import org.jvoicexml.event.PlainEvent;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * The FIA processed a <code>&lt;goto&gt;</code> event.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.3
 */
public final class GotoNextDocumentEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = -8607895631087859465L;

    /** The detail message. */
    public static final String EVENT_TYPE =
            GotoNextDocumentEvent.class.getName();

    /** The next document. */
    private final VoiceXmlDocument document;

    /** URI of the next document. */
    private final URI uri;

    /**
     * Constructs a new object.
     * @param docUri
     *        URI of the next document.
     * @param doc the next document.
     */
    public GotoNextDocumentEvent(final URI docUri,
            final VoiceXmlDocument doc) {
        uri = docUri;
        document = doc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the URI of the next document.
     * @return URI of the next document.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Retrieves the next document.
     * @return the next document.
     */
    public VoiceXmlDocument getDocument() {
        return document;
    }
}
