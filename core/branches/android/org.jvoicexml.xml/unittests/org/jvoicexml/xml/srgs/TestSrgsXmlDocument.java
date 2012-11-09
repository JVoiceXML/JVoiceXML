/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/unittests/org/jvoicexml/xml/srgs/TestSrgsXmlDocument.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

import java.io.File;
import java.io.FileReader;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * The <code>TestSrgsXmlDocument</code> tests the integrity of the
 * specified class.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision: 2325 $
 */
public final class TestSrgsXmlDocument {

    /** Base String for grammar documents. */
    private static final String BASE =
        "../org.jvoicexml/test/config/irp_vxml21/";

    /**
     * Test constructor for 'SrgsXmlDocument'.
     *
     * @see SrgsXmlDocument()
     * @exception Exception
     *            test failed
     */
    @Test
    public void testEmptyConstructor() throws Exception {
        SrgsXmlDocument document = new SrgsXmlDocument();
        Assert.assertNotNull(document);
    }

    /**
     * Test constructor for 'SrgsXmlDocument'.
     *
     * @see SrgsXmlDocument(InputSource)
     * @exception Exception
     *            test failed
     */
    @Test
    public void testInputConstructor() throws Exception {
        final File testFile = new File(BASE + "2/2_grammar_b.grxml");
        final FileReader reader = new FileReader(testFile);
        final InputSource source = new InputSource(reader);
        final SrgsXmlDocument document = new SrgsXmlDocument(source);
        Assert.assertNotNull(document);
    }
}
