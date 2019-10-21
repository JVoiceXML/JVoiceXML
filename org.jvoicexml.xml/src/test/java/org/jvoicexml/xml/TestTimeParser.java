/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link TimeParser}.
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */

public final class TestTimeParser {
    /**
     * Test case for {@link TimeParser#parse()}.
     */
    @Test
    public void testParse() {
        final TimeParser parser1 = new TimeParser("10s");
        Assert.assertEquals(10000, parser1.parse());
        final TimeParser parser2 = new TimeParser("2.7s");
        Assert.assertEquals(2700, parser2.parse());
        final TimeParser parser3 = new TimeParser(".5s");
        Assert.assertEquals(500, parser3.parse());
        final TimeParser parser4 = new TimeParser("850ms");
        Assert.assertEquals(850, parser4.parse());
        final TimeParser parser5 = new TimeParser("+1.5s");
        Assert.assertEquals(1500, parser5.parse());
        final TimeParser parser6 = new TimeParser("20");
        Assert.assertEquals(-1, parser6.parse());
        final TimeParser parser7 = new TimeParser("abcs");
        Assert.assertEquals(-1, parser7.parse());
        final TimeParser parser8 = new TimeParser(null);
        Assert.assertEquals(-1, parser8.parse());
    }
}
