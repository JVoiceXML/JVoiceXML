/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.interpreter.formitem;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.GenericVoiceXmlEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventCountable;

/**
 * This class provides a test case for the {@link ClearStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestEventCounter {

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.EventCounter#incrementEventCounter(org.jvoicexml.event.JVoiceXMLEvent)}.
     */
    @Test
    public void testIncrement() {
        final EventCountable counter = new EventCounter();
        Assert.assertEquals(0, counter.getEventCount(""));
        Assert.assertEquals(0, counter.getEventCount("org"));
        Assert.assertEquals(0, counter.getEventCount("org.jvoicexml"));
        Assert.assertEquals(0, counter.getEventCount("org.jvoicexml.test"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test.counter"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test2.counter"));

        final JVoiceXMLEvent event =
            new GenericVoiceXmlEvent("org.jvoicexml.test");
        counter.incrementEventCounter(event);

        Assert.assertEquals(0, counter.getEventCount(""));
        Assert.assertEquals(1, counter.getEventCount("org"));
        Assert.assertEquals(1, counter.getEventCount("org.jvoicexml"));
        Assert.assertEquals(1, counter.getEventCount("org.jvoicexml.test"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test.counter"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test2.counter"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.EventCounter#resetEventCounter()}.
     */
    @Test
    public void testReset() {
        final EventCountable counter = new EventCounter();
        Assert.assertEquals(0, counter.getEventCount(""));
        Assert.assertEquals(0, counter.getEventCount("org"));
        Assert.assertEquals(0, counter.getEventCount("org.jvoicexml"));
        Assert.assertEquals(0, counter.getEventCount("org.jvoicexml.test"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test.counter"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test2.counter"));

        final JVoiceXMLEvent event =
            new GenericVoiceXmlEvent("org.jvoicexml.test");
        counter.incrementEventCounter(event);

        Assert.assertEquals(0, counter.getEventCount(""));
        Assert.assertEquals(1, counter.getEventCount("org"));
        Assert.assertEquals(1, counter.getEventCount("org.jvoicexml"));
        Assert.assertEquals(1, counter.getEventCount("org.jvoicexml.test"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test.counter"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test2.counter"));

        counter.resetEventCounter();
        Assert.assertEquals(0, counter.getEventCount(""));
        Assert.assertEquals(0, counter.getEventCount("org"));
        Assert.assertEquals(0, counter.getEventCount("org.jvoicexml"));
        Assert.assertEquals(0, counter.getEventCount("org.jvoicexml.test"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test.counter"));
        Assert.assertEquals(0, counter.getEventCount(
                "org.jvoicexml.test2.counter"));
    }
}
