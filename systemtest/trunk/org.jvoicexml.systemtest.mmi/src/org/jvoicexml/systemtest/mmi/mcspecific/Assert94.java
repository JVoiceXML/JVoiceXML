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
package org.jvoicexml.systemtest.mmi.mcspecific;

import org.apache.log4j.Logger;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.StatusRequest;
import org.jvoicexml.mmi.events.StatusRequestBuilder;
import org.jvoicexml.mmi.events.StatusResponse;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 94: All Modality Components must support the basic life-cycle
 * events.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class Assert94 extends AbstractAssert {
    /** The logger instance. */
    private static final Logger LOGGER = Logger.getLogger(Assert94.class);

    /**
     * Constructs a new object.
     */
    public Assert94() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 94;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        final StatusRequestBuilder statusBuilder = new StatusRequestBuilder();
        String requestId = createRequestId();
        statusBuilder.setRequestId(requestId);
        final StatusRequest statusRequest = statusBuilder.toStatusRequest();
        send(statusRequest);
        final MMIEvent statusReponse = waitForResponse("StatusResponse");
        if (!(statusReponse instanceof StatusResponse)) {
            throw new TestFailedException("expected a StatusReponse but got a "
                    + statusReponse.getClass());
        }
        checkIds(statusReponse, null, requestId);
        final StatusResponse statusResponseObject =
                (StatusResponse) statusReponse;
        if (statusResponseObject.getRequestID() != null) {
            throw new TestFailedException("expected no context id but got '"
                    + statusResponseObject.getRequestID() + "'");
        }
        if (!requestId.equals(statusResponseObject.getRequestID())) {
            final String message = "Expected request id '" + requestId
                    + "' but have '" + statusResponseObject.getRequestID()
                    + "' in "
                    + statusResponseObject.getClass().getCanonicalName();
            LOGGER.warn(message);
            throw new TestFailedException(message);
        }
        throw new TestFailedException("need to add other lifecycle events");
    }
}
