/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/unittests/org/jvoicexml/xml/TestLanguageIdentifierConverter.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
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
package org.jvoicexml.xml;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link LanguageIdentifierConverter}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.7.1
 */
public class TestLanguageIdentifierConverter {

    /**
     * Test method for {@link org.jvoicexml.xml.LanguageIdentifierConverter#toLanguageIdentifier(java.util.Locale)}.
     */
    @Test
    public void testToLanguageIdentifier() {
        final String id1 =
            LanguageIdentifierConverter.toLanguageIdentifier(Locale.GERMAN);
        Assert.assertEquals("de", id1);
        final String id2 =
            LanguageIdentifierConverter.toLanguageIdentifier(Locale.US);
        Assert.assertEquals("en-US", id2);
        final String id3 =
            LanguageIdentifierConverter.toLanguageIdentifier(null);
        Assert.assertNull(id3);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.LanguageIdentifierConverter#toLocale(java.lang.String)}.
     */
    @Test
    public void testToLocale() {
        final Locale locale1 =
            LanguageIdentifierConverter.toLocale("de");
        Assert.assertEquals(Locale.GERMAN, locale1);
        final Locale locale2 =
            LanguageIdentifierConverter.toLocale("en-US");
        Assert.assertEquals(Locale.US, locale2);
        final Locale locale3 =
            LanguageIdentifierConverter.toLocale(null);
        Assert.assertNull(locale3);
    }

}
