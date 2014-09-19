package org.jvoicexml.callmanager.mmi;

import java.util.UUID;

import org.junit.Test;
import org.jvoicexml.callmanager.mmi.mock.MockETLProtocolAdapter;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;

public class MmiDetailedSessionListenerTest {

    @Test
    public void testSessionEventQueueEMpty() throws Exception {
        final MMIContext context = new MMIContext(UUID.randomUUID().toString());
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final MockETLProtocolAdapter adapter = new MockETLProtocolAdapter();
        final MmiDetailedSessionListener listener = new MmiDetailedSessionListener(
                adapter, context, converter);
        final QueueEmptyEvent empty = new QueueEmptyEvent(null, null);
        listener.sessionEvent(null, empty);
        System.out.println(adapter.getMmiAsString());
    }

}
