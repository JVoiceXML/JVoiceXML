/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2023 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml.vxml;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link BargeInType}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class BargeInTypeTest {

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.BargeInType#valueOfAttribute(java.lang.String)}
     * for type {@link BargeInType#SPEECH}.
     */
    @Test
    public void testValueOfAttributeSpeech() {
        final BargeInType type1 = BargeInType.valueOfAttribute("SPEECH");
        Assert.assertEquals(BargeInType.SPEECH, type1);
        final BargeInType type2 = BargeInType.valueOfAttribute("speech");
        Assert.assertEquals(BargeInType.SPEECH, type2);
        final BargeInType type3 = BargeInType.valueOfAttribute("SpEeCh");
        Assert.assertEquals(BargeInType.SPEECH, type3);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.BargeInType#valueOfAttribute(java.lang.String)}
     * for type {@link BargeInType#HOTWORD}.
     */
    @Test
    public void testValueOfAttributeHotword() {
        final BargeInType type1 = BargeInType.valueOfAttribute("HOTWORD");
        Assert.assertEquals(BargeInType.HOTWORD, type1);
        final BargeInType type2 = BargeInType.valueOfAttribute("hotword");
        Assert.assertEquals(BargeInType.HOTWORD, type2);
        final BargeInType type3 = BargeInType.valueOfAttribute("HoTwOrD");
        Assert.assertEquals(BargeInType.HOTWORD, type3);
    }
    
    /**
     * Test method for {@link org.jvoicexml.xml.vxml.BargeInType#valueOfAttribute(java.lang.String)}
     * for type {@code null}.
     */
    @Test
    public void testValueOfAttributeNull() {
        final BargeInType type1 = BargeInType.valueOfAttribute(null);
        Assert.assertNull(type1);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.BargeInType#valueOfAttribute(java.lang.String)}
     * for an invalid attribute.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfAttributeInvalid() {
        BargeInType.valueOfAttribute("quirx");
    }
}
