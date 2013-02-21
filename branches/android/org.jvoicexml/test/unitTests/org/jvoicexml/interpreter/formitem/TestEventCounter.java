/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/formitem/TestEventCounter.java $
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
package org.jvoicexml.interpreter.formitem;

import junit.framework.TestCase;

import org.jvoicexml.event.GenericVoiceXmlEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventCountable;

/**
 * This class provides a test case for the {@link ClearStrategy}.
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
public final class TestEventCounter
        extends TestCase {

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.EventCounter#incrementEventCounter(org.jvoicexml.event.JVoiceXMLEvent)}.
     */
    public void testIncrement() {
        final EventCountable counter = new EventCounter();
        assertEquals(0, counter.getEventCount(""));
        assertEquals(0, counter.getEventCount("org"));
        assertEquals(0, counter.getEventCount("org.jvoicexml"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test.counter"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test2.counter"));

        final JVoiceXMLEvent event =
            new GenericVoiceXmlEvent("org.jvoicexml.test");
        counter.incrementEventCounter(event);

        assertEquals(0, counter.getEventCount(""));
        assertEquals(1, counter.getEventCount("org"));
        assertEquals(1, counter.getEventCount("org.jvoicexml"));
        assertEquals(1, counter.getEventCount("org.jvoicexml.test"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test.counter"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test2.counter"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.EventCounter#resetEventCounter()}.
     */
    public void testReset() {
        final EventCountable counter = new EventCounter();
        assertEquals(0, counter.getEventCount(""));
        assertEquals(0, counter.getEventCount("org"));
        assertEquals(0, counter.getEventCount("org.jvoicexml"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test.counter"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test2.counter"));

        final JVoiceXMLEvent event =
            new GenericVoiceXmlEvent("org.jvoicexml.test");
        counter.incrementEventCounter(event);

        assertEquals(0, counter.getEventCount(""));
        assertEquals(1, counter.getEventCount("org"));
        assertEquals(1, counter.getEventCount("org.jvoicexml"));
        assertEquals(1, counter.getEventCount("org.jvoicexml.test"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test.counter"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test2.counter"));

        counter.resetEventCounter();
        assertEquals(0, counter.getEventCount(""));
        assertEquals(0, counter.getEventCount("org"));
        assertEquals(0, counter.getEventCount("org.jvoicexml"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test.counter"));
        assertEquals(0, counter.getEventCount("org.jvoicexml.test2.counter"));
    }
}
