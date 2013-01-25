/**
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
 * @author dirk
 * 
 */
public class UmundoETLProtocolAdapter implements ETLProtocolAdapter {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(SocketETLProtocolAdapter.class);

    private Node node;
    private TypedSubscriber subscriber;
    private MmiReceiver receiver;
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
        receiver = new MmiReceiver(sourceUrl);
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
     * (non-Javadoc)
     * 
     * @see
     * org.jvoicexml.callmanager.mmi.ETLProtocolAdapter#sendMMIEvent(java.lang
     * .Object, org.jvoicexml.mmi.events.xml.MMIEvent)
     */
    @Override
    public void sendMMIEvent(Object channel, LifeCycleEvent event)
            throws IOException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvoicexml.callmanager.mmi.ETLProtocolAdapter#stop()
     */
    @Override
    public void stop() {

    }

}
