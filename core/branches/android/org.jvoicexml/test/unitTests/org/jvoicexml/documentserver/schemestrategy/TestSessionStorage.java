/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/documentserver/schemestrategy/TestSessionStorage.java $
 * Version: $LastChangedRevision: 2830 $
 * Date:    $Date: 2011-09-23 06:04:56 -0500 (vie, 23 sep 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.documentserver.schemestrategy.DummySessionIdentifierFactory;

/**
 * Test case for {@link SessionStorage}.
 * @author Dirk Schnelle
 * @version $Revision: 2830 $
 * @since 0.7
 */
public final class TestSessionStorage {
    /** The default session to use. */
    private String sessionId;

    /** The storage to test. */
    private SessionStorage<String> storage;

    /**
     * Test set up.
     */
    @Before
    public void setUp() {
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final Session session = new JVoiceXmlSession(null, jvxml, null);
        sessionId = session.getSessionID();
        final SessionIdentifierFactory<String> factory =
            new DummySessionIdentifierFactory();
        storage = new SessionStorage<String>(factory);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.SessionStorage#getSessionIdentifier(org.jvoicexml.Session)}.
     */
    @Test
    public void testGetSessionIdentifier() {
        final String id1 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotNull(id1);
        final String id2 = storage.getSessionIdentifier(sessionId);
        Assert.assertEquals(id1, id2);
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final Session session2 = new JVoiceXmlSession(null, jvxml, null);
        final String id3 = storage.getSessionIdentifier(
                session2.getSessionID());
        Assert.assertNotSame(id1, id3);
        final String id4 = storage.getSessionIdentifier(null);
        Assert.assertNull("expected to retrieve a null identifer", id4);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.SessionStorage#releaseSession(org.jvoicexml.Session)}.
     */
    @Test
    public void testReleaseSession() {
        final String id1 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotNull(id1);
        storage.releaseSession(sessionId);
        final String id2 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotSame(id1, id2);
    }

}
