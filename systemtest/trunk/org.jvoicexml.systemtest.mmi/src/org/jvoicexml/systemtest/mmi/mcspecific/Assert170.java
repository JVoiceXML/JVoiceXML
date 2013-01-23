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
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 170: If a Modality Component receives a new StartRequest while it
 * is executing a previous one, it MUST either cease execution of the previous
 * StartRequest and begin executing the content specified in the most recent
 * StartRequest, or reject the new StartRequest, returning a StartResponse with
 * status equal to 'failure'.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Assert170 extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert170() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 170;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        final StartRequest request1 = new StartRequest();
        final String contextId = getContextId();
        request1.setContext(contextId);
        final String requestId1 = createRequestId();
        request1.setRequestId(requestId1);
        final File file = new File("vxml/helloworld.vxml");
        final URI uri = file.toURI();
        request1.setContentURL(uri);
        send(request1);
        final LifeCycleEvent startReponse1 = waitForResponse("StartResponse");
        if (!(startReponse1 instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse1.getClass());
        }
        checkIds(startReponse1, contextId, requestId1);
        ensureSuccess(startReponse1);
        final StartRequest request2 = new StartRequest();
        request2.setContext(contextId);
        final String requestId2 = createRequestId();
        request2.setRequestId(requestId2);
        request2.setContentURL(uri);
        send(request2);
        final LifeCycleEvent startReponse2 = waitForResponse("StartResponse");
        if (!(startReponse2 instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse1.getClass());
        }
        checkIds(startReponse2, contextId, requestId2);
        ensureSuccess(startReponse1);
        final LifeCycleEvent doneNotification =
                waitForResponse("DoneNotification");
        if (!(doneNotification instanceof DoneNotification)) {
            throw new TestFailedException(
                    "expected a DoneNotification but got a "
                    + startReponse1.getClass());
        }
        checkIds(doneNotification, contextId, requestId2);
    }
}
