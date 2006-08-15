/*
 * File:    $RCSfile: GotoNextDocumentEvent.java,v $
 * Version: $Revision: 1.4 $
 * Date:    $Date: 2005/12/15 10:39:54 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.plain.PlainEvent;
import java.net.URI;

/**
 * The FIA processed a <code>&lt;goto&gt;</code> event.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class GotoNextDocumentEvent
        extends PlainEvent {
    /** The serial version UID. */
    static final long serialVersionUID = -8719847663038865208L;

    /** The detail message. */
    public static final String EVENT_TYPE =
            GotoNextDocumentEvent.class.getName();

    /** URI of the next document. */
    private final URI document;

    /**
     * Constructs a new object.
     * @param uri
     *        URI of the next document.
     */
    public GotoNextDocumentEvent(final URI uri) {
        document = uri;
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
}
