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
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartRequestBuilder;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.systemtest.mmi.TestFailedException;

/**
 * Assertion 8: If a modality component finishes processing it MUST send a
 * DoneNotification
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public class Assert8 extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert8() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void test() throws Exception {
        final StartRequestBuilder builder = new StartRequestBuilder();
        final String contextId = getContextId();
        builder.setContextId(contextId);
        final String requestId = createRequestId();
        builder.setRequestId(requestId);
        final File file = new File("vxml/helloworld.vxml");
        final URI uri = file.toURI();
        builder.setHref(uri);
        final StartRequest request = builder.toStartRequest();
        send(request);
        final MMIEvent startReponse = waitForResponse("StartResponse");
        if (!(startReponse instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse.getClass());
        }
        checkIds(startReponse, contextId, requestId);
        ensureSuccess(startReponse);
        final MMIEvent doneNotification = waitForResponse("DoneNotification");
        if (!(doneNotification instanceof DoneNotification)) {
            throw new TestFailedException(
                    "expected a DoneNotification but got a "
                    + startReponse.getClass());
        }
        checkIds(startReponse, contextId, requestId);
        clearContext();
    }
}
