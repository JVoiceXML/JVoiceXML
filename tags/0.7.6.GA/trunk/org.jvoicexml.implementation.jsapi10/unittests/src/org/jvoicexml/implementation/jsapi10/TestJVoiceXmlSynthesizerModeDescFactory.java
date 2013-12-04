/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.synthesis.SynthesizerModeDesc;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link JVoiceXmlSynthesizerModeDescFactory}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestJVoiceXmlSynthesizerModeDescFactory {
    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.JVoiceXmlSynthesizerModeDescFactory#setEngineName(java.lang.String)}.
     */
    @Test
    public void testSetEngineName() {
        final JVoiceXmlSynthesizerModeDescFactory desc1 =
            new JVoiceXmlSynthesizerModeDescFactory();

        final String engineName = "Horst";
        desc1.setEngineName(engineName);

        final SynthesizerModeDesc refDescriptor1 =
            new SynthesizerModeDesc(engineName, null, null, null, null);

        Assert.assertEquals(refDescriptor1, desc1.createDescriptor());

        final SynthesizerModeDescFactory desc2 =
            new JVoiceXmlSynthesizerModeDescFactory();

        final SynthesizerModeDesc refDescriptor2 =
            new SynthesizerModeDesc(null, null, null, null, null);

        Assert.assertEquals(refDescriptor2, desc2.createDescriptor());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.JVoiceXmlSynthesizerModeDescFactory#setLocale(java.util.Locale)}.
     */
    @Test
    public void testSetLocale() {
        final JVoiceXmlSynthesizerModeDescFactory desc1 =
            new JVoiceXmlSynthesizerModeDescFactory();

        final Locale locale = Locale.US;
        desc1.setLocale(locale);

        final SynthesizerModeDesc refDescriptor1 =
            new SynthesizerModeDesc(null, null, locale, null, null);

        Assert.assertEquals(refDescriptor1, desc1.createDescriptor());

        final SynthesizerModeDescFactory desc2 =
            new JVoiceXmlSynthesizerModeDescFactory();

        final SynthesizerModeDesc refDescriptor2 =
            new SynthesizerModeDesc(null, null, null, null, null);

        Assert.assertEquals(refDescriptor2, desc2.createDescriptor());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.JVoiceXmlSynthesizerModeDescFactory#setModeName(java.lang.String)}.
     */
    @Test
    public void testSetModeName() {
        final JVoiceXmlSynthesizerModeDescFactory desc1 =
            new JVoiceXmlSynthesizerModeDescFactory();

        final String modeName = "Horst";
        desc1.setModeName(modeName);

        final SynthesizerModeDesc refDescriptor1 =
            new SynthesizerModeDesc(null, modeName, null, null, null);

        Assert.assertEquals(refDescriptor1, desc1.createDescriptor());

        final SynthesizerModeDescFactory desc2 =
            new JVoiceXmlSynthesizerModeDescFactory();

        final SynthesizerModeDesc refDescriptor2 =
            new SynthesizerModeDesc(null, null, null, null, null);

        Assert.assertEquals(refDescriptor2, desc2.createDescriptor());
    }

}
