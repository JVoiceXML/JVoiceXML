/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link JlibRtpOutputMediaLocatorFactory}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 *
 */
public final class TestJlibRtpOutputMediaLocatorFactory {

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi20.JlibRtpOutputMediaLocatorFactory#getSourceMediaLocator(org.jvoicexml.implementation.SynthesizedOutput)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetSourceMediaLocator() throws Exception {
        final JlibRtpOutputMediaLocatorFactory factory =
            new JlibRtpOutputMediaLocatorFactory();
        final URI locator1 = factory.getSourceMediaLocator(null);
        Assert.assertEquals(new URI("rtp://localhost:30000/audio?"
                + "participant=localhost&keepAlive=false&signed=false"),
                locator1);
        final URI locator2 = factory.getSourceMediaLocator(null);
        Assert.assertEquals(new URI("rtp://localhost:30002/audio?"
                + "participant=localhost&keepAlive=false&signed=false"),
                locator2);
        factory.setParticipant("127.0.0.1");
        factory.setChannels(2);
        final URI locator3 = factory.getSourceMediaLocator(null);
        Assert.assertEquals(new URI("rtp://localhost:30004/audio?"
                + "participant=127.0.0.1&channels=2&keepAlive=false"
                + "&signed=false"),
                locator3);
    }

}
