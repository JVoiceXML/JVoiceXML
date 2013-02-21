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

import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 207: If a MC receives a PrepareRequest containing a new context
 * (without a previous NewContextRequest/Response exchange), it MUST accept the
 * new context and return a PrepareResponse message.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Assert207 extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert207() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 207;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        final PrepareRequest request1 = new PrepareRequest();
        final String contextId = getContextId();
        request1.setContext(contextId);
        final String requestId1 = createRequestId();
        request1.setRequestId(requestId1);
        final File file1 = new File("vxml/helloworld1.vxml");
        final URI uri1 = file1.toURI();
        request1.setContentURL(uri1);
        send(request1);
        final LifeCycleEvent prepareReponse1 =
                waitForResponse("PrepareResponse");
        if (!(prepareReponse1 instanceof PrepareResponse)) {
            throw new TestFailedException("expected a PrepareReponse but got a "
                    + prepareReponse1.getClass());
        }
        checkIds(prepareReponse1, contextId, requestId1);
        final PrepareRequest request2 = new PrepareRequest();
        request2.setContext(contextId);
        final String requestId2 = createRequestId();
        request2.setRequestId(requestId2);
        final File file2 = new File("vxml/helloworld2.vxml");
        final URI uri2 = file2.toURI();
        request2.setContentURL(uri2);
        send(request2);
        final LifeCycleEvent prepareReponse2 =
                waitForResponse("PrepareResponse");
        if (!(prepareReponse2 instanceof PrepareResponse)) {
            throw new TestFailedException("expected a PrepareReponse but got a "
                    + prepareReponse2.getClass());
        }
        checkIds(prepareReponse2, contextId, requestId2);
        final PrepareRequest request3 = new PrepareRequest();
        request3.setContext(contextId);
        final String requestId3 = createRequestId();
        request3.setRequestId(requestId3);
        final File file3 = new File("vxml/helloworld.vxml");
        final URI uri3 = file3.toURI();
        request3.setContentURL(uri3);
        send(request3);
        final LifeCycleEvent prepareReponse3 =
                waitForResponse("PrepareResponse");
        if (!(prepareReponse3 instanceof PrepareResponse)) {
            throw new TestFailedException("expected a PrepareReponse but got a "
                    + prepareReponse3.getClass());
        }
        checkIds(prepareReponse3, contextId, requestId3);
    }
}
