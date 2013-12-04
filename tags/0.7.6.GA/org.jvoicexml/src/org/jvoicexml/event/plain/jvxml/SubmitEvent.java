/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
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

import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.PlainEvent;

/**
 * The FIA processed a <code>&lt;submit&gt;</code> event.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.7
 */
public final class SubmitEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = -8687408528165723480L;

    /** The detail message. */
    public static final String EVENT_TYPE =
            SubmitEvent.class.getName();

    /** Descriptor of the next document. */
    private final DocumentDescriptor descriptor;

    /**
     * Constructs a new object.
     * @param documentDescriptor
     *        descriptor of the next document.
     */
    public SubmitEvent(final DocumentDescriptor documentDescriptor) {
        descriptor = documentDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the descriptor of the next document.
     * @return descriptor of the next document.
     */
    public DocumentDescriptor getDocumentDescriptor() {
        return descriptor;
    }
}
