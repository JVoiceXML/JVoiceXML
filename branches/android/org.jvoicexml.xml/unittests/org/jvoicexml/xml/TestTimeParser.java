/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/unittests/org/jvoicexml/xml/TestTimeParser.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.TimeParser;
import junit.framework.TestCase;

/**
 * Test for {@link TimeParser}.
 * @author Dirk Schnelle
 * @version $Revision: 2325 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class TestTimeParser extends TestCase {
    /**
     * Test case for {@link TimeParser#parse()}.
     */
    public void testParse() {
        final TimeParser parser1 = new TimeParser("10s");
        assertEquals(10000, parser1.parse());
        final TimeParser parser2 = new TimeParser("2.7s");
        assertEquals(2700, parser2.parse());
        final TimeParser parser3 = new TimeParser(".5s");
        assertEquals(500, parser3.parse());
        final TimeParser parser4 = new TimeParser("850ms");
        assertEquals(850, parser4.parse());
        final TimeParser parser5 = new TimeParser("+1.5s");
        assertEquals(1500, parser5.parse());
        final TimeParser parser6 = new TimeParser("20");
        assertEquals(-1, parser6.parse());
        final TimeParser parser7 = new TimeParser("abcs");
        assertEquals(-1, parser7.parse());
        final TimeParser parser8 = new TimeParser(null);
        assertEquals(0, parser8.parse());
    }
}

