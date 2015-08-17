/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test case for {@link SessionStorage}.
 * 
 * @author Dirk Schnelle
 * @version $Revision$
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
        @SuppressWarnings("unchecked")
        final SessionIdentifierFactory<String> factory = Mockito
                .mock(SessionIdentifierFactory.class);
        final Answer<String> answer = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0].toString() + "-G";
            }
        };
        Mockito.when(factory.createSessionIdentifier(Mockito.anyString()))
                .then(answer);
        sessionId = factory.createSessionIdentifier("dummy");
        storage = new SessionStorage<String>(factory);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.schemestrategy.SessionStorage#getSessionIdentifier(org.jvoicexml.Session)}
     * .
     */
    @Test
    public void testGetSessionIdentifier() {
        final String id1 = storage.getSessionIdentifier(sessionId);
        Assert.assertNotNull(id1);
        final String id2 = storage.getSessionIdentifier(sessionId);
        Assert.assertEquals(id1, id2);
        final String sessionId3 = UUID.randomUUID().toString();
        final String id3 = storage.getSessionIdentifier(sessionId3);
        Assert.assertNotSame(id1, id3);
        final String id4 = storage.getSessionIdentifier(null);
        Assert.assertNull("expected tosessionId retrieve a null identifer", id4);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.schemestrategy.SessionStorage#releaseSession(org.jvoicexml.Session)}
     * .
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
