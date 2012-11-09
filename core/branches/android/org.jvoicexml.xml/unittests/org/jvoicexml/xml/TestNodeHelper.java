/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/unittests/org/jvoicexml/xml/TestNodeHelper.java $
 * Version: $LastChangedRevision: 2632 $
 * Date:    $Date: 2011-05-04 16:52:27 -0500 (mié, 04 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2011-05-04 16:52:27 -0500 (mié, 04 may 2011) $, Dirk Schnelle-Walka, project lead
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
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Test case for {@link NodeHelper}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2632 $
 * @since 0.7.3
 */
public final class TestNodeHelper {

    /**
     * Test method for {@link org.jvoicexml.xml.NodeHelper#addText(org.jvoicexml.xml.TextContainer, java.lang.String)}.
     * @exception Exception test failed
     */
    @Test
    public void testAddText() throws Exception {
        final SsmlDocument document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.addText("1");
        speak.addText("  2 3  \t ");
        speak.addText("\r\n4 ");
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                + "<speak "
                + "xmlns=\"http://www.w3.org/2001/10/synthesis\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "version=\"1.0\" xml:lang=\""
                + LanguageIdentifierConverter.toLanguageIdentifier(
                        Locale.getDefault()) + "\" "
                + "xsi:schemaLocation=\"http://www.w3.org/2001/10/synthesis "
                + "http://www.w3.org/TR/speech-synthesis/synthesis.xsd\">"
                + "1 2 3 4</speak>", document.toString());
    }

}
