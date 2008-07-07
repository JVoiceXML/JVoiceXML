/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

package org.jvoicexml;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Test case for {@link DocumentDescriptor}.
 * @author Dirk Schnell
 * @version $Revision: $
 * @since 0.7
 */
public final class TestDocumentDescriptor {

    /**
     * Test method for {@link org.jvoicexml.DocumentDescriptor#DocumentDescriptor(java.net.URI)}.
     * @exception Exception
     *            Test failed
     */
    @Test
    public void testDocumentDescriptorURI() throws Exception {
        final URI uri = new URI("http://jvoicexml.org");
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
        Assert.assertEquals(uri, descriptor.getUri());
        Assert.assertEquals(RequestMethod.GET, descriptor.getMethod());
        Assert.assertNull("excepted to find no attributes",
                descriptor.getAttributes());
    }

    /**
     * Test method for {@link org.jvoicexml.DocumentDescriptor#DocumentDescriptor(java.net.URI, org.jvoicexml.xml.vxml.RequestMethod)}.
     * @exception Exception
     *            Test failed
     */
    @Test
    public void testDocumentDescriptorURIRequestMethod() throws Exception {
        final URI uri = new URI("http://jvoicexml.org");
        final RequestMethod method = RequestMethod.POST;
        final DocumentDescriptor descriptor =
            new DocumentDescriptor(uri, method);
        Assert.assertEquals(uri, descriptor.getUri());
        Assert.assertEquals(method, descriptor.getMethod());
        Assert.assertNull("excepted to find no attributes",
                descriptor.getAttributes());
    }

    /**
     * Test method for {@link org.jvoicexml.DocumentDescriptor#setURI(java.net.URI)}.
     * @exception Exception
     *            Test failed
     */
    @Test
    public void testSetURI() throws Exception {
        final URI uri1 = new URI("http://jvoicexml.org");
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri1);
        Assert.assertEquals(uri1, descriptor.getUri());
        final URI uri2 = new URI("http://jvoicexml.sourceforge.net");
        descriptor.setURI(uri2);
        Assert.assertEquals(uri2, descriptor.getUri());
    }

    /**
     * Test method for {@link org.jvoicexml.DocumentDescriptor#setAttributes(org.jvoicexml.FetchAttributes)}.
     * @exception Exception
     *            Test failed
     */
    @Test
    public void testSetAttributes() throws Exception {
        final URI uri = new URI("http://jvoicexml.org");
        final DocumentDescriptor descriptor =
            new DocumentDescriptor(uri);
        final FetchAttributes attributes = new FetchAttributes();
        descriptor.setAttributes(attributes);
        Assert.assertEquals(attributes, descriptor.getAttributes());
    }

}
