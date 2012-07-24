/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Container to store all request identifier for MMI events in a single place.
 * <p>
 * It may be extended by custom implementations to also add other information
 * associated with this ids.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class MMIRequestIdentifier {
    /**
     * A unique identifier for a Request/Response pair. Most life-cycle events
     * come in Request/Response pairs that share a common RequestID. For any
     * such pair, the RequestID in the Response event MUST match the RequestID
     * in the request event. The RequestID for such a pair MUST be unique within
     * the given context.
     */
    private String requestId;

    /**
     * A URI that MUST be unique for the lifetime of the system. It is used to
     * identify this interaction. All events relating to a given interaction
     * MUST use the same context URI. Events containing a different context URI
     * MUST be interpreted as part of other, unrelated, interactions.
     */
    private final URI contextId;

    /**
     * Constructs a new object.
     * @param ctxId the context id
     * @exception URISyntaxException
     *            the given context id does not denote a valid URI
     */
    public MMIRequestIdentifier(final String ctxId) throws URISyntaxException {
        contextId = new URI(ctxId);
    }

    /**
     * Constructs a new object.
     * @param reqId the request id
     * @param ctxId the context id
     * @exception URISyntaxException
     *            the given context id does not denote a valid URI
     */
    public MMIRequestIdentifier(final String reqId, final String ctxId)
            throws URISyntaxException {
        this(ctxId);
        requestId = reqId;
    }

    /**
     * Sets the request id.
     * @param reqId the new request id.
     */
    public final void setRequestId(final String reqId) {
        requestId = reqId;
    }

    /**
     * Retrieves the request id.
     * @return the request id
     */
    public final String getRequestId() {
        return requestId;
    }

    /**
     * Retrieves the context id.
     * @return the context id.
     */
    public final String getContextId() {
        return contextId.toString();
    }

    /**
     * Retrieves the context id.
     * @return the context id.
     */
    public final URI getContextIdUri() {
        return contextId;
    }
}
