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
 * Test cases for {@link ParamValueType}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class ParamValueTypeTest {

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@link ParamValueType#DATA}.
     */
    @Test
    public void testValueOfAttributeData() {
        final ParamValueType type1 = ParamValueType.valueOfAttribute("DATA");
        Assert.assertEquals(ParamValueType.DATA, type1);
        final ParamValueType type2 = ParamValueType.valueOfAttribute("data");
        Assert.assertEquals(ParamValueType.DATA, type2);
        final ParamValueType type3 = ParamValueType.valueOfAttribute("DaTa");
        Assert.assertEquals(ParamValueType.DATA, type3);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@link ParamValueType#REF}.
     */
    @Test
    public void testValueOfAttributeRef() {
        final ParamValueType type1 = ParamValueType.valueOfAttribute("REF");
        Assert.assertEquals(ParamValueType.REF, type1);
        final ParamValueType type2 = ParamValueType.valueOfAttribute("ref");
        Assert.assertEquals(ParamValueType.REF, type2);
        final ParamValueType type3 = ParamValueType.valueOfAttribute("Ref");
        Assert.assertEquals(ParamValueType.REF, type3);
    }
    
    /**
     * Test method for {@link org.jvoicexml.xml.vxml.AcceptType#valueOfAttribute(java.lang.String)}
     * for type {@code null}.
     */
    @Test
    public void testValueOfAttributeNull() {
        final ParamValueType type1 = ParamValueType.valueOfAttribute(null);
        Assert.assertNull(type1);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.BargeInType#valueOfAttribute(java.lang.String)}
     * for an invalid attribute.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfAttributeInvalid() {
        ParamValueType.valueOfAttribute("dataref");
    }
}
