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

package org.jvoicexml.documentserver;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.event.error.BadFetchError;
import org.mockito.Mockito;

/**
 * Test cases for {@link LazyLoadingGrammarDocument}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class LazyLoadingGrammarDocumentTest {

    /**
     * Test case for {@link LazyLoadingGrammarDocument#getDocument().}
     * 
     * @throws URISyntaxException
     *             test failed.
     * @throws BadFetchError
     *             test failed
     */
    @Test
    public void testGetDocument() throws URISyntaxException, BadFetchError {
        final URI uri = new URI("http://localhost:8080/testgrammar");
        final FetchAttributes attributes = new FetchAttributes();
        attributes.setFetchHint(FetchAttributes.HINT_SAFE);
        final SessionIdentifier sessionId = new UuidSessionIdentifier();
        final DocumentServer server = Mockito.mock(DocumentServer.class);
        final GrammarDocument doc = new ExternalGrammarDocument(uri,
                "test".getBytes(), "UTF-8", true);
        Mockito.when(server.getGrammarDocument(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(doc);
        final LazyLoadingGrammarDocument document = new LazyLoadingGrammarDocument(
                sessionId, server, null, uri, attributes);
        Assert.assertEquals(doc.getBuffer(), document.getBuffer());
    }
}
