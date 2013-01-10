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

package org.jvoicexml.mmi.events.xml;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for the {@link CommonAttributeAdapter}.
 * @author Dirk Schnelle-walka
 * @since 0.7.6
 */
public final class TestCommonAttributeAdapter {

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
