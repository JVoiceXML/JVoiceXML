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
 * Assertion 169: If the IM includes a value in the ContentURL or Content field
 * of the StartRequest event, the Modality Component MUST use this value.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class Assert169  extends AbstractAssert {
    /**
     * Constructs a new object.
     */
    public Assert169() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return 169;
    }

    /**
     * Executes the test case.
     * @exception Exception
     *            test failed
     */
    @Override
    public void test() throws Exception {
        final StartRequest request = new StartRequest();
        final String contextId = getContextId();
        request.setContext(contextId);
        final String requestId = createRequestId();
        request.setRequestId(requestId);
        final File file = new File("vxml/helloworld.vxml");
        final URI uri = file.toURI();
        request.setContentURL(uri);
        send(request);
        final LifeCycleEvent startReponse = waitForResponse("StartResponse");
        if (!(startReponse instanceof StartResponse)) {
            throw new TestFailedException("expected a StartReponse but got a "
                    + startReponse.getClass());
        }
        checkIds(startReponse, contextId, requestId);
        ensureSuccess(startReponse);
        final LifeCycleEvent doneNotification =
                waitForResponse("DoneNotification");
        if (!(doneNotification instanceof DoneNotification)) {
            throw new TestFailedException(
                    "expected a DoneNotification but got a "
                    + startReponse.getClass());
        }
        checkIds(doneNotification, contextId, requestId);
    }
}
