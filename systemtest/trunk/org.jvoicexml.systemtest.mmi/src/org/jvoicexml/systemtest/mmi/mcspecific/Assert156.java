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

import java.io.File;
import java.net.URI;

import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.PrepareRequestBuilder;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartRequestBuilder;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 156: Modality Components that return a PrepareResponse event with
 * Status of 'Success' SHOULD be ready to run with close to 0 delay upon receipt
 * of the StartRequest.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Assert156 extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert156() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 156;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        final PrepareRequestBuilder builder = new PrepareRequestBuilder();
        final String contextId = getContextId();
        builder.setContextId(contextId);
        final String requestId = createRequestId();
        builder.setRequestId(requestId);
        final File file = new File("vxml/helloworld.vxml");
        final URI uri = file.toURI();
        builder.setHref(uri);
        final PrepareRequest request = builder.toPrepareRequest();
        send(request);
        final MMIEvent prepareReponse = waitForResponse("PrepareResponse");
        if (!(prepareReponse instanceof PrepareResponse)) {
            throw new TestFailedException("expected a PrepareReponse but got a "
                    + prepareReponse.getClass());
        }
        checkIds(prepareReponse, contextId, requestId);
        final StartRequestBuilder startBuilder = new StartRequestBuilder();
        startBuilder.setContextId(contextId);
        final String startRequestId = createRequestId();
        startBuilder.setRequestId(startRequestId);
        final StartRequest startRequest = startBuilder.toStartRequest();
        final long startTime = System.currentTimeMillis();
        send(startRequest);
        final MMIEvent startReponse = waitForResponse("StartResponse");
        final long endTime = System.currentTimeMillis();
        if (!(startReponse instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse.getClass());
        }
        ensureSuccess(startReponse);
        setNotes("started after " + (endTime - startTime) + " msec");
        checkIds(startReponse, contextId, startRequestId);
        final MMIEvent doneNotification = waitForResponse("DoneNotification");
        if (!(doneNotification instanceof DoneNotification)) {
            throw new TestFailedException(
                    "expected a DoneNotification but got a "
                    + startReponse.getClass());
        }
        checkIds(doneNotification, contextId, startRequestId);
    }
}
