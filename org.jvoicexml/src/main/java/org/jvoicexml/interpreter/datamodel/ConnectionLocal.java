/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.datamodel;

import java.net.URI;

/**
 * A variable container to hold the local connection information.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class ConnectionLocal  {
    /** The local interpreter context device. */
    private final String uri;

    /**
     * Constructs a new object.
     * @param localUri local interpreter context device
     */
    public ConnectionLocal(final URI localUri) {
        if (localUri == null) {
            uri = null;
        } else {
            uri = localUri.toString();
        }
    }

    /**
     * Retrieves the URI.
     * @return the URI.
     */
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ConnectionLocal [uri=" + uri + "]";
    }
}
