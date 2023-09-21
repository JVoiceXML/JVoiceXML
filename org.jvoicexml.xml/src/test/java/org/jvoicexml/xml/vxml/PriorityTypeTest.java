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
public class PriorityTypeTest {

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.PriorityType#valueOfAttribute(java.lang.String)}
     * for type {@link PriorityType#APPEND}.
     */
    @Test
    public void testValueOfAttributeAppend() {
        final PriorityType type1 = PriorityType.valueOfAttribute("APPEND");
        Assert.assertEquals(PriorityType.APPEND, type1);
        final PriorityType type2 = PriorityType.valueOfAttribute("append");
        Assert.assertEquals(PriorityType.APPEND, type2);
        final PriorityType type3 = PriorityType.valueOfAttribute("ApPeNd");
        Assert.assertEquals(PriorityType.APPEND, type3);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@link PriorityType#PREPEND}.
     */
    @Test
    public void testValueOfAttributePrepend() {
        final PriorityType type1 = PriorityType.valueOfAttribute("PREPEND");
        Assert.assertEquals(PriorityType.PREPEND, type1);
        final PriorityType type2 = PriorityType.valueOfAttribute("prepend");
        Assert.assertEquals(PriorityType.PREPEND, type2);
        final PriorityType type3 = PriorityType.valueOfAttribute("PrEpEnD");
        Assert.assertEquals(PriorityType.PREPEND, type3);
    }
    
    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@link PriorityType#CLEAR}.
     */
    @Test
    public void testValueOfAttributeClear() {
        final PriorityType type1 = PriorityType.valueOfAttribute("CLEAR");
        Assert.assertEquals(PriorityType.CLEAR, type1);
        final PriorityType type2 = PriorityType.valueOfAttribute("clear");
        Assert.assertEquals(PriorityType.CLEAR, type2);
        final PriorityType type3 = PriorityType.valueOfAttribute("ClEaR");
        Assert.assertEquals(PriorityType.CLEAR, type3);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.PriorityType#valueOfAttribute(java.lang.String)}
     * for type {@code null}.
     */
    @Test
    public void testValueOfAttributeNull() {
        final PriorityType type1 = PriorityType.valueOfAttribute(null);
        Assert.assertNull(type1);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.PriorityType#valueOfAttribute(java.lang.String)}
     * for an invalid attribute.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfAttributeInvalid() {
        PriorityType.valueOfAttribute("aprox");
    }
}
