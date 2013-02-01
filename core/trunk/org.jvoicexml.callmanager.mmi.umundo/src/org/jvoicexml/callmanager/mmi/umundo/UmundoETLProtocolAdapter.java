/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.umundo;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.mmi.ETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.callmanager.mmi.socket.SocketETLProtocolAdapter;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.CancelResponse;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.LifeCycleResponse;
import org.jvoicexml.mmi.events.NewContextResponse;
import org.jvoicexml.mmi.events.PauseResponse;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.mmi.events.ResumeResponse;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.mmi.events.StatusResponse;
import org.jvoicexml.mmi.events.StatusType;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.umundo.core.Node;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

import com.google.protobuf.ExtensionRegistry;

/**
 * An {@link ETLProtocolAdapter} for umundo.
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 */
public final class UmundoETLProtocolAdapter implements ETLProtocolAdapter {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(SocketETLProtocolAdapter.class);

    /** The umundo not to receive messages. */
    private Node node;
    /** The subscriber to MMI events. */
    private TypedSubscriber subscriber;
    /** The receiver that effectively receives the event objects. */
    private final MmiReceiver receiver;
    /** The publisher to send MMI events. */
    private TypedPublisher publisher;
    /** The registry for protobuf extensions. */
    private ExtensionRegistry registry;
    /** The name of the umundo channel to use. */
    private String channel;
    /** The source URL of this modality component. */
    private String sourceUrl;

    /**
     * Constructs a new object.
     */
    public UmundoETLProtocolAdapter() {
        channel = "mmi:jvoicexml";
        sourceUrl = "umundo://mmi/jvoicexml";
        receiver = new MmiReceiver(sourceUrl);
    }

    /**
     * Sets the name of the channel to send and receive messages.
     * @param name name of the channel
     */
    public void setChannel(final String name) {
        channel = name;
    }

    /**
     * Sets the source URL of this modality component.
     * @param name source URL
     */
    public void setSourceUrl(final String name) {
        sourceUrl = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        node = new Node();
        subscriber = new TypedSubscriber(channel, receiver);
        subscriber.registerType(LifeCycleEvents.LifeCycleEvent.class);

        registry = ExtensionRegistry.newInstance();
        LifeCycleEvents.registerAllExtensions(registry);
        node.addSubscriber(subscriber);

        publisher = new TypedPublisher(channel);
        node.addPublisher(publisher);
        LOGGER.info("receiving MMI events via channel '" + channel
                + "' to '" + sourceUrl + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return receiver != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMMIEventListener(final MMIEventListener listener) {
        receiver.addMMIEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMMIEventListener(final MMIEventListener listener) {
        receiver.removeMMIEventListener(listener);
    }

    /**
     * Create a protobuf response object from the given event.
     * @param evt the event to transform into a protobuf representation
     * @return the transformed event
     */
    private LifeCycleEvents.LifeCycleResponse createResponse(
            final LifeCycleEvent evt) {
        final LifeCycleEvents.LifeCycleResponse.Builder builder;
        if (evt instanceof LifeCycleResponse) {
            final LifeCycleResponse response =
                    (LifeCycleResponse) evt;
            final LifeCycleEvents.LifeCycleResponse.StatusType type;
            if (response.getStatus() == StatusType.SUCCESS) {
                type = LifeCycleEvents.LifeCycleResponse.StatusType.SUCCESS;
            } else {
                type = LifeCycleEvents.LifeCycleResponse.StatusType.FAILURE;
            }
            final AnyComplexType any = response.getStatusInfo();
            final List<Object> content = any.getContent();
            final StringBuilder str = new StringBuilder();
            for (Object o : content) {
                str.append(o);
            }
            builder = LifeCycleEvents.LifeCycleResponse.newBuilder()
                    .setContext(response.getContext())
                    .setStatus(type)
                    .setStatusInfo(str.toString());
        } else {
            return null;
        }
        if (evt instanceof PrepareResponse) {
            LifeCycleEvents.PrepareResponse prepareResponse =
                    LifeCycleEvents.PrepareResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.PrepareResponse.response,
                    prepareResponse);
        } else if (evt instanceof NewContextResponse) {
            LifeCycleEvents.NewContextResponse newContextResponse =
                    LifeCycleEvents.NewContextResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.NewContextResponse.response,
                    newContextResponse);
        } else if (evt instanceof StartResponse) {
            LifeCycleEvents.StartResponse startResponse =
                    LifeCycleEvents.StartResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.StartResponse.response,
                    startResponse);
        } else if (evt instanceof DoneNotification) {
            LifeCycleEvents.DoneNotification notification =
                    LifeCycleEvents.DoneNotification.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.DoneNotification.notification,
                    notification);
        } else if (evt instanceof CancelResponse) {
            LifeCycleEvents.CancelResponse cancelResponse =
                    LifeCycleEvents.CancelResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.CancelResponse.response,
                    cancelResponse);
        } else if (evt instanceof PauseResponse) {
            LifeCycleEvents.PauseResponse pauseResponse =
                    LifeCycleEvents.PauseResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.PauseResponse.response,
                    pauseResponse);
        } else if (evt instanceof ResumeResponse) {
            LifeCycleEvents.ResumeResponse resumeResponse =
                    LifeCycleEvents.ResumeResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.ResumeResponse.response,
                    resumeResponse);
        } else if (evt instanceof ClearContextResponse) {
            LifeCycleEvents.ClearContextResponse clearContextResponse =
                    LifeCycleEvents.ClearContextResponse.newBuilder()
                    .build();
            builder.setExtension(LifeCycleEvents.ClearContextResponse.response,
                    clearContextResponse);
        } else if (evt instanceof StatusResponse) {
//            LifeCycleEvents.StatusResponse statusResponse =
//                    LifeCycleEvents.StatusResponse.newBuilder()
//                    .build();
//            builder.setExtension(LifeCycleEvents.StatusResponse.response,
//                    clearContextResponse);
            LOGGER.warn("status is currently unsupported");
        } else {
            LOGGER.warn("unable to map '" + evt + "' to protobuf");
            return null;
        }
        return builder.build();
    }

    private LifeCycleEvents.LifeCycleEvent.LifeCycleEventType getEventType(
            final LifeCycleEvent evt) {
        if (evt instanceof PrepareResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.PREPARE_RESPONSE;
        } else if (evt instanceof NewContextResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.NEW_CONTEXT_RESPONSE;
        } else if (evt instanceof StartResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.START_RESPONSE;
        } else if (evt instanceof DoneNotification) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.DONE_NOTIFICATION;
        } else if (evt instanceof CancelResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.CANCEL_RESPONSE;
        } else if (evt instanceof PauseResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.PAUSE_RESPONSE;
        } else if (evt instanceof ResumeResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.RESUME_RESPONSE;
        } else if (evt instanceof ClearContextResponse) {
            return LifeCycleEvents.LifeCycleEvent.LifeCycleEventType.CLEAR_CONTEXT_RESPONSE;
        } else if (evt instanceof StatusResponse) {
//            LifeCycleEvents.StatusResponse statusResponse =
//                    LifeCycleEvents.StatusResponse.newBuilder()
//                    .build();
//            builder.setExtension(LifeCycleEvents.StatusResponse.response,
//                    clearContextResponse);
            LOGGER.warn("status is currently unsupported");
        } else {
            LOGGER.warn("unable to map '" + evt + "' to protobuf");
        }
        return null;
   }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMMIEvent(final Object ch, final LifeCycleEvent evt)
            throws IOException {
        LifeCycleEvents.LifeCycleResponse response =
                createResponse(evt);
        final LifeCycleEvents.LifeCycleEvent.LifeCycleEventType type
            = getEventType(evt);
        final LifeCycleEvents.LifeCycleEvent event =
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .setType(type)
                .setRequestID(evt.getRequestId())
                .setSource(sourceUrl)
                .setTarget(evt.getTarget())
                .setExtension(LifeCycleEvents.LifeCycleResponse.response,
                        response)
                .build();
        publisher.sendObject("LifeCycleEvent", event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        node.suspend();
    }
}
