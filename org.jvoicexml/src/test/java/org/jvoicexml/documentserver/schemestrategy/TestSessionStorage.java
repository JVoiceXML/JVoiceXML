/*
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
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifer;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test case for {@link SessionStorage}.
 * 
 * @author Dirk Schnelle
 * @since 0.7
 */
public final class TestSessionStorage {
    /** The default session to use. */
    private SessionIdentifier sessionId;

    /** The storage to test. */
    private SessionStorage<SessionIdentifier> storage;

    /**
     * Test set up.
     */
    @Before
    public void setUp() {
        @SuppressWarnings("unchecked")
        final SessionIdentifierFactory<SessionIdentifier> factory = Mockito
                .mock(SessionIdentifierFactory.class);
        final Answer<SessionIdentifier> answer = new Answer<SessionIdentifier>() {
            @Override
            public SessionIdentifier answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgumentAt(0, SessionIdentifier.class);
            }
        };
        Mockito.when(factory.createSessionIdentifier(Mockito.anyObject()))
                .then(answer);
        sessionId = factory.createSessionIdentifier(new SessionIdentifier() {
            private static final long serialVersionUID = 1907169201694843275L;

            @Override
            public String getId() {
                // TODO Auto-generated method stub
                return "dummy";
            }
        });
        storage = new SessionStorage<SessionIdentifier>(factory);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.schemestrategy.SessionStorage#getSessionIdentifier(org.jvoicexml.Session)}
     * .
     */
    @Test
    public void testGetSessionIdentifier() {
        final SessionIdentifier id1 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotNull(id1);
        final SessionIdentifier id2 = storage.getSessionIdentifier(sessionId);
        Assert.assertEquals(id1, id2);
        final SessionIdentifier sessionId3 = new UuidSessionIdentifer();
        final SessionIdentifier id3 = storage.getSessionIdentifier(sessionId3);
        Assert.assertNotSame(id1, id3);
        final SessionIdentifier id4 = storage.getSessionIdentifier(null);
        Assert.assertNull("expected tosessionId retrieve a null identifer", id4);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.schemestrategy.SessionStorage#releaseSession(org.jvoicexml.Session)}
     * .
     */
    @Test
    public void testReleaseSession() {
        final SessionIdentifier id1 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotNull(id1);
        storage.releaseSession(sessionId);
        final SessionIdentifier id2 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotSame(id1, id2);
    }

}
