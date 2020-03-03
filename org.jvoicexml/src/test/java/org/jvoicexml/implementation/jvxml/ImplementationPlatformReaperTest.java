/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jvxml;

import org.junit.Test;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.mockito.Mockito;

/**
 * Test cases for {@link ImplementationPlatformReaper}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class ImplementationPlatformReaperTest {

    /**
     * Test method for {@link org.jvoicexml.implementation.jvxml.ImplementationPlatformReaper#run()}.
     * @throws InterruptedException test failed
     */
    @Test
    public void testRun() throws InterruptedException {
        final JVoiceXmlImplementationPlatform platform =
                new JVoiceXmlImplementationPlatform(null, null, null, null);
        final Session session = Mockito.mock(Session.class);
        final SessionIdentifier id = new UuidSessionIdentifier();
        Mockito.when(session.getSessionId()).thenReturn(id);
        platform.setSession(session);
        final ImplementationPlatformReaper reaper =
                new ImplementationPlatformReaper(platform, null, null);
        final long delay = 200;
        reaper.setReapingDelay(delay);
        reaper.start();
        Thread.sleep(delay + 100);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jvxml.ImplementationPlatformReaper#stopReaping()}.
     * @throws InterruptedException test failed
     */
    @Test
    public void testStopReaping() throws InterruptedException {
        final JVoiceXmlImplementationPlatform platform =
                new JVoiceXmlImplementationPlatform(null, null, null, null);
        final Session session = Mockito.mock(Session.class);
        final SessionIdentifier id = new UuidSessionIdentifier();
        Mockito.when(session.getSessionId()).thenReturn(id);
        platform.setSession(session);
        final ImplementationPlatformReaper reaper =
                new ImplementationPlatformReaper(platform, null, null);
        reaper.start();
        Thread.sleep(100);
        reaper.stopReaping();
    }

}
