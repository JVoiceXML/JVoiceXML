/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mmi.events;

import java.io.Serializable;

/**
 * An MMI done notification.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class DoneNotification extends LifeCycleResponse
    implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = -6780336880993618024L;

    /**
     * Constructs a new object.
     */
    public DoneNotification() {
    }

    /**
     * Constructs a new object with the provided values.
     * @param requestId the request id
     * @param source the source
     * @param target the target
     * @param context the context
     * @since 0.7.9
     */
    public DoneNotification(final String requestId, final String source,
            final String target, final String context) {
        super(requestId, source, target, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof DoneNotification)) {
            return false;
        }
        return true;
    }
}
