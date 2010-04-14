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

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link BooleanGrammarCreator}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public final class TestBooleanGrammarCreator {

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.builtin.BooleanGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammar() throws Exception, BadFetchError {
        final GrammarCreator creator = new BooleanGrammarCreator();

        final URI dtmfUri = new URI("builtin://dtmf/boolean");
        final SrgsXmlDocument dtmfDocument = creator.createGrammar(dtmfUri);
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());

        final URI voiceUri = new URI("builtin://voice/boolean");
        final SrgsXmlDocument voiceDocument = creator.createGrammar(voiceUri);
        final Grammar voiceGrammar = voiceDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.builtin.BooleanGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammarParameters() throws Exception, BadFetchError {
        final GrammarCreator creator = new BooleanGrammarCreator();

        final URI uri1 = new URI("builtin://dtmf/boolean?y=7;n=9");
        final SrgsXmlDocument document1 = creator.createGrammar(uri1);
        final Grammar grammar1 = document1.getGrammar();
        Assert.assertEquals(ModeType.DTMF, grammar1.getMode());

        final URI uri2 = new URI("builtin://dtmf/boolean?y=7");
        final SrgsXmlDocument document2 = creator.createGrammar(uri2);
        final Grammar grammar2 = document2.getGrammar();
        Assert.assertEquals(ModeType.DTMF, grammar2.getMode());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.builtin.BooleanGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            expected exception
     */
    @Test(expected = BadFetchError.class)
    public void testCreateGrammarInvalidParameters()
        throws Exception, BadFetchError {
        final GrammarCreator creator = new BooleanGrammarCreator();

        final URI uri = new URI("builtin://dtmf/boolean?y=");
        creator.createGrammar(uri);
    }
}
