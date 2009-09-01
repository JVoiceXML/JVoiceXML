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
package org.jvoicexml.xml.ssml;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link PercentageParser}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public final class TestPercentageParser {

    /**
     * Test method for {@link org.jvoicexml.xml.ssml.PercentageParser#parse(java.lang.String)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testParse() throws Exception {
        final float rate1 = 97.34f;
        Assert.assertEquals(rate1, PercentageParser.parse(rate1 + "%"));
        final float rate2 = 13f;
        Assert.assertEquals(-rate2, PercentageParser.parse("-" + rate2 + "%"));
        final float rate3 = 27.2f;
        Assert.assertEquals(rate3, PercentageParser.parse("+" + rate3 + "%"));
    }

    /**
     * Test method for {@link org.jvoicexml.xml.ssml.PercentageParser#parse(java.lang.String)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIsRelative() throws Exception {
        final float rate1 = 97.34f;
        Assert.assertFalse(PercentageParser.isRelative(rate1 + "%"));
        final float rate2 = 13f;
        Assert.assertTrue(PercentageParser.isRelative("-" + rate2 + "%"));
        final float rate3 = 27.2f;
        Assert.assertTrue(PercentageParser.isRelative("+ " + rate3 + "%"));
    }
}
