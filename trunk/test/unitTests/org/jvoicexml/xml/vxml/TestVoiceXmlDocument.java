/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.xml.vxml;

import org.w3c.dom.DocumentType;

import junit.framework.TestCase;

/**
 * Test cases for {@link VoiceXmlDocument}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestVoiceXmlDocument extends TestCase {

    /**
     * Test method for {@link org.jvoicexml.xml.vxml.VoiceXmlDocument#VoiceXmlDocument()}.
     * @exception Exception
     *            Test failed.
     */
    public void testVoiceXmlDocument() throws Exception {
        final VoiceXmlDocument doc1 = new VoiceXmlDocument();
        assertNotNull(doc1.getVxml());
        assertNull(doc1.getDoctype());

        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.0");
        final VoiceXmlDocument doc2 = new VoiceXmlDocument();
        assertNotNull(doc2.getVxml());
        final DocumentType type2 = new VoiceXml20DocumentType();
        assertEquals(type2.toString(), doc2.getDoctype().toString());

        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.1");
        final VoiceXmlDocument doc3 = new VoiceXmlDocument();
        assertNotNull(doc3.getVxml());
        final DocumentType type3 = new VoiceXml21DocumentType();
        assertEquals(type3.toString(), doc3.getDoctype().toString());

        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.2");
        IllegalArgumentException error = null;
        try {
            new VoiceXmlDocument();
        } catch (IllegalArgumentException e) {
            error = e;
        }

        assertNull("2.2 is an unsupported version type", error);
    }

    /**
     * Test method for {@link VoiceXmlDocument#toXml().
     * @throws Excpetion
     *         Test failed.
     */
    public void testToXml() throws Exception {
        System.setProperty(VoiceXmlDocument.VXML_VERSION, "2.1");
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final String xml1 = doc.toXml();
        assertTrue("missing xml prefix", xml1.startsWith("<?xml"));
        System.setProperty("jvoicexml.xml.encoding", "ISO-8859-1");
        // TODO check why this fails.
        final String xml2 = doc.toXml();
        assertTrue("missing xml prefix", xml2.startsWith("<?xml"));
    }
}
