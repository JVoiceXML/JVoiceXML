/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/event/plain/jvxml/TestObjectTagResultEvent.java $
 * Version: $LastChangedRevision: 2153 $
 * Date:    $Date: 2010-04-14 02:25:59 -0500 (mié, 14 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

/**
 * Test case for {@link org.jvoicexml.event.plain.jvxml.RecognitionEvent}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2153 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestObjectTagResultEvent
        extends TestCase {
    /** Test value. */
    private static final Object TEST_VALUE = new Long(42);

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent#getEventType()}.
     */
    public void testGetEventType() {
        final ObjectTagResultEvent event =
            new ObjectTagResultEvent(TEST_VALUE);
        assertEquals(ObjectTagResultEvent.EVENT_TYPE, event.getEventType());
        assertEquals(ObjectTagResultEvent.class.getName(),
                event.getEventType());
    }

    /**
     * Test method for {@link org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent#getInputResult()}.
     */
    public void testGetInputResult() {
        final ObjectTagResultEvent event1 =
            new ObjectTagResultEvent(TEST_VALUE);
        assertEquals(TEST_VALUE, event1.getInputResult());

        final ObjectTagResultEvent event2 =
            new ObjectTagResultEvent(null);
        assertNull(event2.getInputResult());
    }

}
