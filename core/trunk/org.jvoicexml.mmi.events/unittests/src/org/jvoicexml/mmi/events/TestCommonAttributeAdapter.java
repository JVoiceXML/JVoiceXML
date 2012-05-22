/**
 * 
 */
package org.jvoicexml.mmi.events;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for the {@link CommonAttributeAdapter}.
 * @author Dirk Schnelle-walka
 * @since 0.7.6
 */
public class TestCommonAttributeAdapter {

    /**
     * Test case for {@link CommonAttributeAdapter#getSource()}.
     */
    @Test
    public void testGetSource() {
        final DoneNotification e1 = new DoneNotification();
        e1.setSource(UUID.randomUUID().toString());
        final CommonAttributeAdapter a1 = new CommonAttributeAdapter(e1);
        Assert.assertEquals(e1.getSource(), a1.getSource());
        final StartRequest e2 = new StartRequest();
        e2.setSource(UUID.randomUUID().toString());
        final CommonAttributeAdapter a2 = new CommonAttributeAdapter(e2);
        Assert.assertEquals(e2.getSource(), a2.getSource());
    }

    /**
     * Test case for {@link CommonAttributeAdapter#setSource()}.
     */
    @Test
    public void testSetSource() {
        final DoneNotification e1 = new DoneNotification();
        final CommonAttributeAdapter a1 = new CommonAttributeAdapter(e1);
        a1.setSource(UUID.randomUUID().toString());
        Assert.assertEquals(e1.getSource(), a1.getSource());
        final StartRequest e2 = new StartRequest();
        final CommonAttributeAdapter a2 = new CommonAttributeAdapter(e2);
        a2.setSource(UUID.randomUUID().toString());
        Assert.assertEquals(e2.getSource(), a2.getSource());
    }

    /**
     * Test case for {@link CommonAttributeAdapter#getTarget()}.
     */
    @Test
    public void testGetTarget() {
        final DoneNotification e1 = new DoneNotification();
        e1.setTarget(UUID.randomUUID().toString());
        final CommonAttributeAdapter a1 = new CommonAttributeAdapter(e1);
        Assert.assertEquals(e1.getTarget(), a1.getTarget());
        final StartRequest e2 = new StartRequest();
        e2.setTarget(UUID.randomUUID().toString());
        final CommonAttributeAdapter a2 = new CommonAttributeAdapter(e2);
        Assert.assertEquals(e2.getTarget(), a2.getTarget());
    }

    /**
     * Test case for {@link CommonAttributeAdapter#setTarget()}.
     */
    @Test
    public void testSetTarget() {
        final DoneNotification e1 = new DoneNotification();
        final CommonAttributeAdapter a1 = new CommonAttributeAdapter(e1);
        a1.setTarget(UUID.randomUUID().toString());
        Assert.assertEquals(e1.getTarget(), a1.getTarget());
        final StartRequest e2 = new StartRequest();
        final CommonAttributeAdapter a2 = new CommonAttributeAdapter(e2);
        a2.setTarget(UUID.randomUUID().toString());
        Assert.assertEquals(e2.getTarget(), a2.getTarget());
    }
}
