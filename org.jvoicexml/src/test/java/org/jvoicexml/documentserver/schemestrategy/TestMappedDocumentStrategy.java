/*
MappedDocumentStrategy.java * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.xml.sax.InputSource;

/**
 * Test case for {@link MappedDocumentStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestMappedDocumentStrategy {
    /** The document repository. */
    private DocumentMap map;

    /**
     * Set up the test environment
     */
    @Before
    public void setUp() throws Exception {
        map = DocumentMap.getInstance();
        final VoiceXmlDocument doc1 = new VoiceXmlDocument();
        final Vxml vxml = doc1.getVxml();
        vxml.appendChild(Form.class);
        URI uri1 = map.getUri("/doc");
        map.addDocument(uri1, doc1);
        System.setProperty("jvoicexml.xml.encoding", "ISO-8859-1");
        final URI uri2 = map.getUri("/doc2");
        map.addDocument(uri2, doc1);
        System.setProperty("jvoicexml.xml.encoding", "UTF-8");
        final String test = "test";
        URI uri3 = map.getUri("/test");
        map.addDocument(uri3, test);
    }

    /**
     * Test method for {@link MappedDocumentStrategy#getInputStream(URI)}.
     * @throws Exception
     *         Test failed.
     * @throws BadFetchError
     *         Test failed.
     */
    @Test
    public void testGetInputStream() throws Exception, BadFetchError {
        final MappedDocumentStrategy strategy = new MappedDocumentStrategy();
        JVoiceXMLEvent error = null;
        try {
            strategy.getInputStream(null, null, null, 0, null);
        } catch (BadFetchError e) {
            error = e;
        }
        Assert.assertNotNull("BadFetchError expected", error);

        URI uri1 = map.getUri("/doc");
        final InputStream stream1 = strategy.getInputStream(null, uri1, null,
                0, null);
        Assert.assertNotNull(stream1);
        final InputSource inputSource = new InputSource(stream1);
        final VoiceXmlDocument doc1 = new VoiceXmlDocument(inputSource);
        final Vxml vxml1 = doc1.getVxml();
        Assert.assertTrue(vxml1.hasChildNodes());

        URI uri2 =  map.getUri("/test");
        final InputStream stream2 = strategy.getInputStream(null, uri2, null,
                0, null);
        Assert.assertNotNull(stream2);
        final String test = readString(stream2);
        Assert.assertEquals("test", test);
    }

    /**
     * Reads a {@link String} from the given {@link InputStream}.
     * @param input the input stream to use.
     * @return read string.
     * @throws BadFetchError
     *         Error reading.
     */
    private String readString(final InputStream input) throws BadFetchError {
        // Read from the input
        final byte[] buffer = new byte[1024];
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int num;
        try {
            do {
                num = input.read(buffer);
                if (num >= 0) {
                    out.write(buffer, 0, num);
                }
            } while(num >= 0);
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        final byte[] readBytes = out.toByteArray();
        return new String(readBytes);
    }
}
