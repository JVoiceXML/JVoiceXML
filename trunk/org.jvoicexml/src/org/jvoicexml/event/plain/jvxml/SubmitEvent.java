/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/SubmitStrategy.java $
 * Version: $LastChangedRevision: 558 $
 * Date:    $LastChangedDate: 2007-11-08 11:21:16 +0100 (Do, 08 Nov 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * The FIA processed a <code>&lt;submit&gt;</code> event.
 *
 * @author Dirk Schnelle
 * @version $Revision: 248 $
 *
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class SubmitEvent
        extends PlainEvent {
    /** The detail message. */
    public static final String EVENT_TYPE =
            SubmitEvent.class.getName();

    /** URI of the next document. */
    private final URI document;

    /** The request method. */
    private final RequestMethod method;

    /**
     * Constructs a new object.
     * @param uri
     *        URI of the next document.
     * @param requestMethod
     *        the request method.
     */
    public SubmitEvent(final URI uri, final RequestMethod requestMethod) {
        document = uri;
        method = requestMethod;
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
        return document;
    }

    /**
     * Retrieves the request method.
     * @return the request method.
     */
    public RequestMethod getRequestMethod() {
        return method;
    }
}
