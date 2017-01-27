/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.implementation.jsapi10;

import java.util.Locale;

import javax.speech.recognition.RecognizerModeDesc;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link JVoiceXmlRecognizerModeDescFactory}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestJVoiceXmlRecognizerModeDescFactory {
    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.JVoiceXmlRecognizerModeDescFactory#setEngineName(java.lang.String)}.
     */
    @Test
    public void testSetEngineName() {
        final JVoiceXmlRecognizerModeDescFactory desc1 =
            new JVoiceXmlRecognizerModeDescFactory();

        final String engineName = "Horst";
        desc1.setEngineName(engineName);

        final RecognizerModeDesc refDescriptor1 =
            new RecognizerModeDesc(engineName, null, null, null, null, null);

        Assert.assertEquals(refDescriptor1, desc1.createDescriptor());

        final JVoiceXmlRecognizerModeDescFactory desc2 =
            new JVoiceXmlRecognizerModeDescFactory();

        final RecognizerModeDesc refDescriptor2 =
            new RecognizerModeDesc(null, null, null, null, null, null);

        Assert.assertEquals(refDescriptor2, desc2.createDescriptor());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.JVoiceXmlRecognizerModeDescFactory#setLocale(java.util.Locale)}.
     */
    @Test
    public void testSetLocale() {
        final JVoiceXmlRecognizerModeDescFactory desc1 =
            new JVoiceXmlRecognizerModeDescFactory();

        final Locale locale = Locale.US;
        desc1.setLocale(locale);

        final RecognizerModeDesc refDescriptor1 =
            new RecognizerModeDesc(null, null, locale, null, null, null);

        Assert.assertEquals(refDescriptor1, desc1.createDescriptor());

        final JVoiceXmlRecognizerModeDescFactory desc2 =
            new JVoiceXmlRecognizerModeDescFactory();

        final RecognizerModeDesc refDescriptor2 =
            new RecognizerModeDesc(null, null, null, null, null, null);

        Assert.assertEquals(refDescriptor2, desc2.createDescriptor());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.JVoiceXmlRecognizerModeDescFactory#setModeName(java.lang.String)}.
     */
    @Test
    public void testSetModeName() {
        final JVoiceXmlRecognizerModeDescFactory desc1 =
            new JVoiceXmlRecognizerModeDescFactory();

        final String modeName = "Horst";
        desc1.setModeName(modeName);

        final RecognizerModeDesc refDescriptor1 =
            new RecognizerModeDesc(null, modeName, null, null, null, null);

        Assert.assertEquals(refDescriptor1, desc1.createDescriptor());

        final JVoiceXmlRecognizerModeDescFactory desc2 =
            new JVoiceXmlRecognizerModeDescFactory();

        final RecognizerModeDesc refDescriptor2 =
            new RecognizerModeDesc(null, null, null, null, null, null);

        Assert.assertEquals(refDescriptor2, desc2.createDescriptor());
    }

}
