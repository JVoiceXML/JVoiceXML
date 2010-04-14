/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver.schemestrategy.builtin;

import java.io.InputStream;
import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;


/**
 * Test cases for {@link BuiltinSchemeStrategy}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public final class TestBuiltinSchemeStrategy {
    /**
     * Test case for {@link BuiltinSchemeStrategy#getInputStream(org.jvoicexml.Session, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Map)}.
     * @exception Exception
     *         test failed
     * @throws BadFetchError
     *         test failed
     * @since 0.7.1
     */
    @Test
    public void testGetInputStream() throws Exception, BadFetchError {
        final BuiltinSchemeStrategy strategy = new BuiltinSchemeStrategy();
        final URI dtmfUri = new URI("builtin://dtmf/boolean");
        final InputStream input = strategy.getInputStream(null, dtmfUri, null,
                0, null);
        final InputSource source = new InputSource(input);
        final SrgsXmlDocument dtmfDocument = new SrgsXmlDocument(source);
        input.close();
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        final URI voiceUri = new URI("builtin://voice/boolean");
        final InputStream voiceInput = strategy.getInputStream(null, voiceUri,
                null, 0, null);
        final InputSource voiceSource = new InputSource(voiceInput);
        final SrgsXmlDocument voiceDocument = new SrgsXmlDocument(voiceSource);
        input.close();
        final Grammar voiceGrammar = voiceDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
    }

    /**
     * Test case for {@link BuiltinSchemeStrategy#getInputStream(org.jvoicexml.Session, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Map)}.
     * @exception Exception
     *         test failed
     * @throws BadFetchError
     *         test failed
     * @since 0.7.1
     */
    @Test
    public void testGetInputStreamParameters() throws Exception, BadFetchError {
        final BuiltinSchemeStrategy strategy = new BuiltinSchemeStrategy();
        final URI dtmfUri = new URI("builtin://dtmf/boolean?y=7;n=9");
        final InputStream input = strategy.getInputStream(null, dtmfUri, null,
                0, null);
        final InputSource source = new InputSource(input);
        final SrgsXmlDocument dtmfDocument = new SrgsXmlDocument(source);
        input.close();
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
    }
}
