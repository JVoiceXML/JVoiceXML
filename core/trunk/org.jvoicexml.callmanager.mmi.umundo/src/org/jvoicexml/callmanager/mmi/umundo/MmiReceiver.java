package org.jvoicexml.callmanager.mmi.umundo;

import java.util.Collection;

import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

public class MmiReceiver implements ITypedReceiver {
    /** Registered listeners for MMI events. */
    private final Collection<MMIEventListener> listeners;

    /** The used protocol adapter. */
    private final String sourceUrl;

    public MmiReceiver(final String source) {
        listeners = new java.util.ArrayList<MMIEventListener>();
        sourceUrl = source;
    }

    @Override
    public void receiveObject(Object object, Message msg) {
        LifeCycleEvents.LifeCycleEvent receivedEvent =
                (LifeCycleEvents.LifeCycleEvent) object;
        final LifeCycleEvent event = convertToLifeCycleEvent(receivedEvent);
        if (event == null) {
            return;
        }
        final DecoratedMMIEvent docatedEvent =
                new DecoratedMMIEvent(sourceUrl, event);
        synchronized (listeners) {
            for (MMIEventListener listener : listeners) {
                listener.receivedEvent(docatedEvent);
            }
        }
    }

    private LifeCycleEvent convertToLifeCycleEvent(
            final LifeCycleEvents.LifeCycleEvent receivedEvent) {
        final LifeCycleEvent event;
        switch (receivedEvent.getType()) {
        case PREPARE_REQUEST:
            event = new PrepareRequest();
            break;
        default:
            event = null;
            break;
        }
        final String target = receivedEvent.getTarget();
        if (!target.equals(sourceUrl)) {
            return null;
        }
        event.setTarget(receivedEvent.getTarget());
        event.setRequestId(receivedEvent.getRequestID());
        event.setSource(receivedEvent.getSource());
        return event;
    }
 
    public void addMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}

