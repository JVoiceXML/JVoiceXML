/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

/**
 * Test cases for {@link SynthesizerModeDescFactory}.
 *
 * @author Dirk SChnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestSynthesizerModeDescFactory
        extends TestCase {
    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.SynthesizerModeDescFactory#setEngineName(java.lang.String)}.
     */
    public void testSetEngineName() {
        final SynthesizerModeDescFactory desc1 =
            new SynthesizerModeDescFactory();

        final String engineName = "Horst";
        desc1.setEngineName(engineName);

        final SynthesizerModeDesc refDescriptor1 =
            new SynthesizerModeDesc(engineName, null, null, null, null);

        assertEquals(refDescriptor1, desc1.getDescriptor());

        final SynthesizerModeDescFactory desc2 =
            new SynthesizerModeDescFactory();

        final SynthesizerModeDesc refDescriptor2 =
            new SynthesizerModeDesc(null, null, null, null, null);

        assertEquals(refDescriptor2, desc2.getDescriptor());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.SynthesizerModeDescFactory#setLocale(java.util.Locale)}.
     */
    public void testSetLocale() {
        final SynthesizerModeDescFactory desc1 =
            new SynthesizerModeDescFactory();

        final Locale locale = Locale.US;
        desc1.setLocale(locale);

        final SynthesizerModeDesc refDescriptor1 =
            new SynthesizerModeDesc(null, null, locale, null, null);

        assertEquals(refDescriptor1, desc1.getDescriptor());

        final SynthesizerModeDescFactory desc2 =
            new SynthesizerModeDescFactory();

        final SynthesizerModeDesc refDescriptor2 =
            new SynthesizerModeDesc(null, null, null, null, null);

        assertEquals(refDescriptor2, desc2.getDescriptor());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.jsapi10.SynthesizerModeDescFactory#setModeName(java.lang.String)}.
     */
    public void testSetModeName() {
        final SynthesizerModeDescFactory desc1 =
            new SynthesizerModeDescFactory();

        final String modeName = "Horst";
        desc1.setModeName(modeName);

        final SynthesizerModeDesc refDescriptor1 =
            new SynthesizerModeDesc(null, modeName, null, null, null);

        assertEquals(refDescriptor1, desc1.getDescriptor());

        final SynthesizerModeDescFactory desc2 =
            new SynthesizerModeDescFactory();

        final SynthesizerModeDesc refDescriptor2 =
            new SynthesizerModeDesc(null, null, null, null, null);

        assertEquals(refDescriptor2, desc2.getDescriptor());
    }

}
