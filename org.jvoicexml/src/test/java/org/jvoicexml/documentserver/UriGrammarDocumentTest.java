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
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Test cases for {@link UriGrammarDocument}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class UriGrammarDocumentTest {

    /**
     * Test case for {@link UriGrammarDocument#getURI()}
     * @throws URISyntaxException test failed
     */
    @Test
    public void testGetURI() throws URISyntaxException {
        final URI uri = new URI("http://localhost:8080/testgrammar");
        final UriGrammarDocument doc = new UriGrammarDocument(uri,
                GrammarType.SRGS_XML, ModeType.VOICE);
        Assert.assertEquals(uri, doc.getURI());
    }

    @Test
    public void testGetModeType() throws URISyntaxException {
        final URI uri = new URI("http://localhost:8080/testgrammar");
        final UriGrammarDocument doc = new UriGrammarDocument(uri,
                GrammarType.SRGS_XML, ModeType.VOICE);
        Assert.assertEquals(ModeType.VOICE, doc.getModeType());
    }

}
