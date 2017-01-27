/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.text;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link TextApplication}.
 * @author Dirk Schnelle-Walka
 *
 */
public final class TestTextApplication {

    /**
     * Test method for {@link org.jvoicexml.callmanager.text.TextApplication#getUriObject()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetUriObject() throws Exception {
        final TextApplication application1 = new TextApplication();
        application1.setUri("http:localhost:8080");
        final URI uri1 = new URI("http:localhost:8080");
        Assert.assertEquals(uri1, application1.getUriObject());

        final TextApplication application2 = new TextApplication();
        Assert.assertNull(application2.getUriObject());
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.text.TextApplication#getPort()}.
     */
    @Test
    public void testGetPort() {
        final TextApplication application1 = new TextApplication();
        application1.setPort(4242);
        Assert.assertEquals(4242, application1.getPort());

        final TextApplication application2 = new TextApplication();
        Assert.assertEquals(0, application2.getPort());
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.text.TextApplication#getUri()}.
     */
    @Test
    public void testGetUri() {
        final TextApplication application1 = new TextApplication();
        application1.setUri("http:localhost:8080");
        Assert.assertEquals("http:localhost:8080", application1.getUri());

        final TextApplication application2 = new TextApplication();
        Assert.assertNull(application2.getUri());
    }

    public void testSetUri() {
        final TextApplication application = new TextApplication();
        application.setUri("foo");
        Assert.assertEquals("foo", application);
        application.setUri(null);
        Assert.assertNull(application.getUri());
    }

    /**
     * Test method for {@link org.jvoicexml.callmanager.text.TextApplication#setUri(String)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUriInvalid() {
        final TextApplication application = new TextApplication();
        application.setUri("#foo#");
    }
}
