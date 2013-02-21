/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/documentserver/schemestrategy/builtin/TestBuiltinSchemeStrategy.java $
 * Version: $LastChangedRevision: 2627 $
 * Date:    $Date: 2011-03-14 02:32:26 -0600 (lun, 14 mar 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.UnsupportedBuiltinError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;


/**
 * Test cases for {@link BuiltinSchemeStrategy}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2627 $
 * @since 0.7.1
 */
public final class TestBuiltinSchemeStrategy {
    /** The test object. */
    private BuiltinSchemeStrategy strategy;

    /**
     * Set up the test environment.
     * 
     * @since 0.7.5
     */
    @Before
    public void setUo() {
        strategy = new BuiltinSchemeStrategy();
        strategy.addGrammarCreator(new BooleanGrammarCreator());
        strategy.addGrammarCreator(new DigitsGrammarCreator());
    }

    /**
     * Test case for {@link BuiltinSchemeStrategy#getInputStream(org.jvoicexml.Session, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Map)}.
     * @exception Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     * @since 0.7.1
     */
    @Test
    public void testGetInputStream() throws Exception, JVoiceXMLEvent {
        final URI dtmfUri = new URI("builtin:dtmf/boolean");
        final InputStream input = strategy.getInputStream(null, dtmfUri, null,
                0, null);
        final InputSource source = new InputSource(input);
        final SrgsXmlDocument dtmfDocument = new SrgsXmlDocument(source);
        input.close();
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        final URI voiceUri = new URI("builtin:voice/boolean");
        final InputStream voiceInput = strategy.getInputStream(null, voiceUri,
                null, 0, null);
        final InputSource voiceSource = new InputSource(voiceInput);
        final SrgsXmlDocument voiceDocument = new SrgsXmlDocument(voiceSource);
        input.close();
        final Grammar voiceGrammar = voiceDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());

        final URI grammarUri = new URI("builtin:grammar/boolean");
        final InputStream grammarInput = strategy.getInputStream(null,
                grammarUri, null, 0, null);
        final InputSource grammarSource = new InputSource(grammarInput);
        final SrgsXmlDocument gramamrDocument =
            new SrgsXmlDocument(grammarSource);
        input.close();
        final Grammar grammarGrammar = gramamrDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, grammarGrammar.getMode());
    }

    /**
     * Test case for {@link BuiltinSchemeStrategy#getInputStream(org.jvoicexml.Session, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Map)}.
     * @exception Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     * @since 0.7.1
     */
    @Test
    public void testGetInputStreamParameters()
        throws Exception, JVoiceXMLEvent {
        final URI dtmfUri = new URI("builtin:dtmf/boolean?y=7;n=9");
        final InputStream input = strategy.getInputStream(null, dtmfUri, null,
                0, null);
        final InputSource source = new InputSource(input);
        final SrgsXmlDocument dtmfDocument = new SrgsXmlDocument(source);
        input.close();
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
    }

    /**
     * Test method for {@link BuiltinSchemeStrategy#getInputStream(org.jvoicexml.Session, URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Map)}.
     * @exception Exception
     *         test failed
     * @throws JVoiceXMLEvent
     *         test failed
     * @since 0.7.5
     */
    @Test(expected = UnsupportedBuiltinError.class)
    public void testGetInputStreamUnknowBuiltin()
        throws Exception, JVoiceXMLEvent {
        final URI uri = new URI("builtin:builtin/cheese?y=7;n=9");
        strategy.getInputStream(null, uri, null, 0, null);
    }
}
