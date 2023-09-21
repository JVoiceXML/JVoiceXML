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
 * Test cases for {@link AcceptType}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class AcceptTypeTest {

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@link AcceptType#EXACT}.
     */
    @Test
    public void testValueOfAttributeExact() {
        final AcceptType type1 = AcceptType.valueOfAttribute("EXACT");
        Assert.assertEquals(AcceptType.EXACT, type1);
        final AcceptType type2 = AcceptType.valueOfAttribute("exact");
        Assert.assertEquals(AcceptType.EXACT, type2);
        final AcceptType type3 = AcceptType.valueOfAttribute("ExAcT");
        Assert.assertEquals(AcceptType.EXACT, type3);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@link AcceptType#APPROXIMATE}.
     */
    @Test
    public void testValueOfAttributeApproximate() {
        final AcceptType type1 = AcceptType.valueOfAttribute("APPROXIMATE");
        Assert.assertEquals(AcceptType.APPROXIMATE, type1);
        final AcceptType type2 = AcceptType.valueOfAttribute("approximate");
        Assert.assertEquals(AcceptType.APPROXIMATE, type2);
        final AcceptType type3 = AcceptType.valueOfAttribute("ApPrOxImAtE");
        Assert.assertEquals(AcceptType.APPROXIMATE, type3);
    }
    
    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@code null}.
     */
    @Test
    public void testValueOfAttributeNull() {
        final AcceptType type1 = AcceptType.valueOfAttribute(null);
        Assert.assertNull(type1);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.BargeInType#valueOfAttribute(java.lang.String)}
     * for an invalid attribute.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfAttributeInvalid() {
        AcceptType.valueOfAttribute("aprox");
    }
}
