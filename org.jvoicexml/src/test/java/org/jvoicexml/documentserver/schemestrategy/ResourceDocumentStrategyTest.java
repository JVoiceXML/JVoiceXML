/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.BadFetchError;



/**
 * Test cases for {@link ResourceDocumentStrategy}.
 * @author Dirk SchnelleWalka
 * @since 0.7.9
 */
public class ResourceDocumentStrategyTest {

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.ResourceDocumentStrategy#getInputStream(org.jvoicexml.SessionIdentifier, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Collection)}.
     * @throws URISyntaxException test failed
     * @throws BadFetchError test failed
     */
    @Test
    public void testGetInputStream() throws URISyntaxException, BadFetchError {
        final URI uri = new URI("res://irp_srgs10/conformance-1.grxml");
        final ResourceDocumentStrategy strategy = new ResourceDocumentStrategy();
        final InputStream in = strategy.getInputStream(null, uri, null, 0, null);
        Assert.assertNotNull(in);
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.ResourceDocumentStrategy#getInputStream(org.jvoicexml.SessionIdentifier, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Collection)}.
     * @throws URISyntaxException test failed
     * @throws BadFetchError test failed
     */
    @Test
    public void testGetInputStreamFragment() throws URISyntaxException, BadFetchError {
        final URI uri = new URI("res://irp_srgs10/conformance-1.grxml#main");
        final ResourceDocumentStrategy strategy = new ResourceDocumentStrategy();
        final InputStream in = strategy.getInputStream(null, uri, null, 0, null);
        Assert.assertNotNull(in);
    }

}
