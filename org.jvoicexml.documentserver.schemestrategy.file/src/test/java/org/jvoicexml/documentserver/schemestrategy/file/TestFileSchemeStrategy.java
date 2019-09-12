/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy.file;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Test case for {@link FileSchemeStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.1
 */
public final class TestFileSchemeStrategy {
    /** the scheme strategy to test. */
    private SchemeStrategy strategy;

    /**
     * Set up the test environment.
     * @throws Exception
     *         error setting up the test environment.
     */
    @Before
    public void setUp() throws Exception {
        strategy = new FileSchemeStrategy();
    }

    /**
     * Test for {@link FileSchemeStrategy#getInputStream(String, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Collection)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetInputStream() throws Exception, JVoiceXMLEvent {
       final URL file = this.getClass().getResource("/hello.vxml");
       final URI uri = file.toURI();
       InputStream in = strategy.getInputStream(null, uri, null, 5000, null);
       Assert.assertNotNull(in);
       in.close();
    }

    /**
     * Test for {@link FileSchemeStrategy#getInputStream(String, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Collection)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetInputStreamFragment() throws Exception, JVoiceXMLEvent {
       final URL file = this.getClass().getResource("/hello.vxml");
       final URI uri = file.toURI();
       final URI fragmentUri = new URI(uri.toString() + "#fragment");
       InputStream in = strategy.getInputStream(null, fragmentUri, null, 5000,
               null);
       Assert.assertNotNull(in);
       in.close();
    }
}
