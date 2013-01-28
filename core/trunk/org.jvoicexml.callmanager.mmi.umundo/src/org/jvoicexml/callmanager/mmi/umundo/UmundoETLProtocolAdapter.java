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

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.mmi.ETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.callmanager.mmi.socket.SocketETLProtocolAdapter;
import org.jvoicexml.mmi.events.LifeCycleEvent;
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

    private Node node;
    private TypedSubscriber subscriber;
    private final MmiReceiver receiver;
    private TypedPublisher publisher;
    /** The registry for protobuf extensions. */
    private ExtensionRegistry registry;
    private String channel;
    private String sourceUrl;

    /**
     * Constructs a new object.
     */
    public UmundoETLProtocolAdapter() {
        channel = "mmi:jvoicexml";
        sourceUrl = "umundo://mmi/jvoicexml";
        receiver = new MmiReceiver(sourceUrl);
    }

    public void setChannel(final String name) {
        channel = name;
    }

    public void setSourceUrl(final String name) {
        sourceUrl = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.callmanager.mmi.ETLProtocolAdapter#start()
     */
    @Override
    public void start() throws IOException {
        node = new Node();
        subscriber = new TypedSubscriber(channel, receiver);
        subscriber.registerType(LifeCycleEvents.LifeCycleEvent.class);
        subscriber.registerType(LifeCycleEvents.LifeCycleRequest.class);
        subscriber.registerType(LifeCycleEvents.LifeCycleResponse.class);
        subscriber.registerType(LifeCycleEvents.NewContextRequest.class);
        subscriber.registerType(LifeCycleEvents.NewContextResponse.class);
        subscriber.registerType(LifeCycleEvents.PrepareRequest.class);
        subscriber.registerType(LifeCycleEvents.PrepareResponse.class);
        subscriber.registerType(LifeCycleEvents.StartRequest.class);
        subscriber.registerType(LifeCycleEvents.StartResponse.class);
        subscriber.registerType(LifeCycleEvents.DoneNotification.class);
        subscriber.registerType(LifeCycleEvents.CancelRequest.class);
        subscriber.registerType(LifeCycleEvents.CancelResponse.class);
        subscriber.registerType(LifeCycleEvents.PauseRequest.class);
        subscriber.registerType(LifeCycleEvents.PauseResponse.class);
        subscriber.registerType(LifeCycleEvents.ResumeRequest.class);
        subscriber.registerType(LifeCycleEvents.ResumeResponse.class);
        subscriber.registerType(LifeCycleEvents.ExtensionNotification.class);
        subscriber.registerType(LifeCycleEvents.ClearContextRequest.class);
        subscriber.registerType(LifeCycleEvents.ClearContextResponse.class);
        subscriber.registerType(LifeCycleEvents.StatusRequest.class);
        subscriber.registerType(LifeCycleEvents.StatusResponse.class);

        registry = ExtensionRegistry.newInstance();
        LifeCycleEvents.registerAllExtensions(registry);

        publisher = new TypedPublisher(channel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.callmanager.mmi.ETLProtocolAdapter#isStarted()
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

    /*
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMMIEvent(Object channel, LifeCycleEvent event)
            throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        node.suspend();
    }

}
