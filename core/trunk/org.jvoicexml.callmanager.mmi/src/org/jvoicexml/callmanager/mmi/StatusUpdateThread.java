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
package org.jvoicexml.callmanager.mmi;

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.mmi.events.xml.StatusResponse;
import org.jvoicexml.mmi.events.xml.StatusResponseBuilder;

/**
 * A thread to send status update messages to the sender.
 * <p>
 * the W3C spec does not mention how to stop automated status updates, so this
 * implementation simply keeps sending status messages until sending fails.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
final class StatusUpdateThread extends Thread {
    /** The voice modality component. */
    private final VoiceModalityComponent mc;

    /** The channel to use to send messages. */
    private final Object channel;

    /** The target where to send status update messages. */
    private final String target;

    /** A given context id. */
    private final URI contextId;

    /** The request id that was provided in the status request message. */
    private final String requestId;

    /** <code>true</code> if periodic sending of updates is requested. */
    private final boolean automaticUpdate;

    /**
     * Constructs a new object.
     * @param vmc the voice modality component
     * @param ch the channel
     * @param trgt the target where to send messages
     * @param context the context id, maybe <code>null</code>
     * @param reqId the request id of the message that caused the status update
     * @param automatic <code>true</code> if periodic sending of updates is
     *          requested
     */
    public StatusUpdateThread(final VoiceModalityComponent vmc, final Object ch,
            final String trgt, final URI context, final String reqId,
            final boolean automatic) {
        mc = vmc;
        channel = ch;
        target = trgt;
        contextId = context;
        requestId = reqId;
        automaticUpdate = automatic;
        setDaemon(true);
    }

    /**
     * Send the update messages.
     * {@inheritDoc}
     */
    @Override
    public void run() {
        boolean running = automaticUpdate;
        do {
            final StatusResponseBuilder builder = new StatusResponseBuilder();
            builder.setTarget(target);
            builder.setAutomaticUpdate(automaticUpdate);
            builder.setRequestId(requestId);
            if (contextId == null) {
                if (mc.isAcceptingLifecycleEvents()) {
                    builder.setStatusAlive();
                } else {
                    builder.setStatusDead();
                }
            } else {
                builder.setContextId(contextId.toString());
                final MMIContext context = mc.getContext(contextId);
                if (context == null) {
                    builder.setStatusDead();
                } else {
                    builder.setStatusAlive();
                }
            }
            final StatusResponse response = builder.toStatusResponse();
            try {
                mc.sendResponse(channel, response);
            } catch (IOException e) {
                running = false;
            }
            if (running) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        } while (running);
    }
}
