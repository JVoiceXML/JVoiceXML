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

import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartRequestBuilder;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.mmi.events.StatusType;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 167: The Modality Component MUST return a StartResponse event in
 * response to a StartRequest event.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Assert167 extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert167() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 167;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        final String contextId = getContextId();
        final StartRequestBuilder startBuilder = new StartRequestBuilder();
        startBuilder.setContextId(contextId);
        String requestId = createRequestId();
        startBuilder.setRequestId(requestId);
        final StartRequest startRequest = startBuilder.toStartRequest();
        send(startRequest);
        final MMIEvent startReponse = waitForResponse("StartResponse");
        if (!(startReponse instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse.getClass());
        }
        checkIds(startReponse, contextId, requestId);
        final StartResponse response = (StartResponse) startReponse;
        if (response.getStatus() != StatusType.FAILURE) {
            throw new TestFailedException(
                    "default behavious should be FAILURE");
        }
    }
}
