/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.jetty;

import java.net.URI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.ExternalGrammarDocument;

/**
 * Test methods for {@link DocumentStorage}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class DocumentStorageTest {
    /** The document storage to test. */
    private DocumentStorage storage;

    /**
     * Set up the test environement
     * 
     * @exception set
     *                up failed
     */
    @Before
    public void setUp() throws Exception {
        storage = new DocumentStorage();
        storage.setStoragePort(9494);
        storage.start();
    }

    /**
     * Tear down the test environment
     * 
     * @throws Exception
     *             tear down failed
     */
    @After
    public void tearDown() throws Exception {
        storage.stop();
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.jetty.DocumentStorage#addGrammarDocument(java.lang.String, org.jvoicexml.GrammarDocument)}
     * .
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testAddGrammarDocument() throws Exception {
        final GrammarDocument document = new ExternalGrammarDocument(null,
                null, null, true);
        storage.addGrammarDocument("12345", document);
        final URI uri = document.getURI();
        Assert.assertNotNull(uri);
        Assert.assertEquals(document, storage.getDocument(uri));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.jetty.DocumentStorage#clear(java.lang.String)}
     * .
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testClear() throws Exception {
        final GrammarDocument document = new ExternalGrammarDocument(null,
                null, null, true);
        storage.addGrammarDocument("12345", document);
        final URI uri = document.getURI();
        Assert.assertNotNull(uri);
        Assert.assertEquals(document, storage.getDocument(uri));
        storage.clear("12345");
        Assert.assertNull(storage.getDocument(uri));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.jetty.DocumentStorage#clear(java.lang.String)}
     * .
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void testClearOtherSession() throws Exception {
        final GrammarDocument document = new ExternalGrammarDocument(null,
                null, null, true);
        storage.addGrammarDocument("12345", document);
        final URI uri = document.getURI();
        Assert.assertNotNull(uri);
        Assert.assertEquals(document, storage.getDocument(uri));
        storage.clear("54321");
        Assert.assertEquals(document, storage.getDocument(uri));
    }

}
