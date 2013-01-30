package org.jvoicexml.callmanager.mmi.umundo;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents.LifeCycleEvent.LifeCycleEventType;
import org.umundo.core.Node;
import org.umundo.s11n.TypedPublisher;
import org.umundo.s11n.TypedSubscriber;

import com.google.protobuf.ExtensionRegistry;

public class TestMmiReceiver {

    private Node receivingNode;
    private Node publishingNode;

    @Before
    public void setUp() throws Exception {
        receivingNode = new Node();
        publishingNode = new Node();
        final MmiReceiver receiver = new MmiReceiver("test");
        TypedSubscriber subscriber = new TypedSubscriber("test", receiver);
        receivingNode.addSubscriber(subscriber);
//        subscriber.registerType(LifeCycleEvents.LifeCycleEvent.class);
//        subscriber.registerType(LifeCycleEvents.LifeCycleRequest.class);
//        subscriber.registerType(LifeCycleEvents.LifeCycleResponse.class);
//        subscriber.registerType(LifeCycleEvents.NewContextRequest.class);
//        subscriber.registerType(LifeCycleEvents.NewContextResponse.class);
//        subscriber.registerType(LifeCycleEvents.PrepareRequest.class);
//        subscriber.registerType(LifeCycleEvents.PrepareResponse.class);
//        subscriber.registerType(LifeCycleEvents.StartRequest.class);
//        subscriber.registerType(LifeCycleEvents.StartResponse.class);
//        subscriber.registerType(LifeCycleEvents.DoneNotification.class);
//        subscriber.registerType(LifeCycleEvents.CancelRequest.class);
//        subscriber.registerType(LifeCycleEvents.CancelResponse.class);
//        subscriber.registerType(LifeCycleEvents.PauseRequest.class);
//        subscriber.registerType(LifeCycleEvents.PauseResponse.class);
//        subscriber.registerType(LifeCycleEvents.ResumeRequest.class);
//        subscriber.registerType(LifeCycleEvents.ResumeResponse.class);
//        subscriber.registerType(LifeCycleEvents.ExtensionNotification.class);
//        subscriber.registerType(LifeCycleEvents.ClearContextRequest.class);
//        subscriber.registerType(LifeCycleEvents.ClearContextResponse.class);
//        subscriber.registerType(LifeCycleEvents.StatusRequest.class);
//        subscriber.registerType(LifeCycleEvents.StatusResponse.class);
//
//        ExtensionRegistry registry = ExtensionRegistry.newInstance();
//        LifeCycleEvents.registerAllExtensions(registry);
    }

    @Test(timeout = 5000)
    public void testReceiveObject() throws Exception {
        final TypedPublisher publisher = new TypedPublisher("test");
        publisher.setGreeter(new MmiGreeter());
        publishingNode.addPublisher(publisher);
        System.out.println("waiting for subscribers");
        publisher.waitForSubscribers(1);
        System.out.println("have subscribers");
        final String requestId = "requestId1";
        final String source = "source1";
        final String target = "target1";
        final String context = "context1";
        final String content = "content1";
        final String contentUrl = "contentUrl1";
        final LifeCycleEvents.PrepareRequest prepareRequest =
                LifeCycleEvents.PrepareRequest.newBuilder()
                .setContent(content)
                .setContentURL(contentUrl)
                .build();
        final LifeCycleEvents.LifeCycleRequest lifeCycleRequest =
                LifeCycleEvents.LifeCycleRequest.newBuilder()
                .setContext(context)
                .setExtension(LifeCycleEvents.PrepareRequest.request, prepareRequest)
                .build();
        final LifeCycleEvents.LifeCycleEvent event1 = 
                LifeCycleEvents.LifeCycleEvent.newBuilder()
                .setType(LifeCycleEventType.PREPARE_REQUEST)
                .setRequestID(requestId)
                .setSource(source)
                .setTarget(target)
                .setExtension(LifeCycleEvents.LifeCycleRequest.request, lifeCycleRequest)
                .build();
        publisher.sendObject("PrepareRequest", event1);
        Thread.sleep(2000);
    }

}
