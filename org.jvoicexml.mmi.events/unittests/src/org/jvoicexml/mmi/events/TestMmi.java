package org.jvoicexml.mmi.events;


import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Mmi}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 *
 */
public class TestMmi {

    /**
     * Test for {@link Mmi#getLifeCycleEvent()}.
     */
    @Test
    public void testGetLifeCycleEventCancelRequest() {
        final Mmi mmi = new Mmi();
        final CancelRequest request = new CancelRequest();
        request.setRequestId("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        mmi.setCancelRequest(request);
        Assert.assertEquals(request, mmi.getLifeCycleEvent());
    }

    /**
     * Test for {@link Mmi#getLifeCycleEvent()}.
     */
    @Test
    public void testGetLifeCycleEventNewContextRequest() {
        final Mmi mmi = new Mmi();
        final NewContextRequest request = new NewContextRequest();
        request.setRequestId("request1");
        request.setSource("source1");
        request.setTarget("target1");
        request.setContext("context1");
        mmi.setNewContextRequest(request);
        Assert.assertEquals(request, mmi.getLifeCycleEvent());
    }
}
