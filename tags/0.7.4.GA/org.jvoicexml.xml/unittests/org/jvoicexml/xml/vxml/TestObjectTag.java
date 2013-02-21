/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml.vxml;

import java.net.URI;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link ObjectTag}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class TestObjectTag {
    /** The test object. */
    private ObjectTag object;

    /**
     * Set up the test environment.
     * @throws Exception set up failed
     */
    @Before
    public void setUp() throws Exception {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        object = form.appendChild(ObjectTag.class);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.ObjectTag#getArchiveUris()}.
     * @exception Exception test failed
     */
    @Test
    public void testGetArchiveUris() throws Exception {
        final URI uri1 = new URI("http://localhost");
        final URI uri2 = new URI("ftp://localhost");
        final Collection<URI> uris = new java.util.ArrayList<URI>();
        uris.add(uri1);
        uris.add(uri2);
        object.setArchive(uris);
        Assert.assertEquals(uris, object.getArchiveUris());
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.ObjectTag#getArchiveUris()}.
     * @exception Exception test failed
     */
    @Test
    public void testGetArchiveUrisNull() throws Exception {
        Assert.assertNull(object.getArchiveUris());
    }
}
