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
package org.jvoicexml.event.plain.jvxml;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent}.
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
public final class TestObjectTagResultEvent {
    /** Test value. */
    private static final Object TEST_VALUE = new Long(42);

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent#getEventType()}.
     */
    @Test
    public void testGetEventType() {
        final ObjectTagResultEvent event =
            new ObjectTagResultEvent(TEST_VALUE);
        Assert.assertEquals(ObjectTagResultEvent.EVENT_TYPE,
                event.getEventType());
        Assert.assertEquals(ObjectTagResultEvent.class.getName(),
                event.getEventType());
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent#getInputResult()}.
     */
    @Test
    public void testGetInputResult() {
        final ObjectTagResultEvent event1 =
            new ObjectTagResultEvent(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, event1.getInputResult());

        final ObjectTagResultEvent event2 =
            new ObjectTagResultEvent(null);
        Assert.assertNull(event2.getInputResult());
    }
}
