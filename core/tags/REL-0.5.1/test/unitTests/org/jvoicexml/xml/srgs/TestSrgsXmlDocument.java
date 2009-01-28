/*
 * File:    $RCSfile: TestSrgsXmlDocument.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;

/**
 * The <code>TestSrgsXmlDocument</code> tests the integrety of the
 * specified class
 * 
 * @author Christoph Buente
 * 
 * @version $Revision$
 * 
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestSrgsXmlDocument
        extends TestCase {
    
    /* Base String for grammar documents */
    private static final String BASE = "test/config/irp_vxml21/";

    private Logger LOGGER = Logger.getLogger(TestSrgsXmlDocument.class);
    
    /**
     * Test constructor for 'org.jvoicexml.xml.srgs.SrgsXmlDocument'.
     * 
     * @see SrgsXmlDocument()
     */
    public void testEmptyConstructor() {
        try {
            SrgsXmlDocument document = new SrgsXmlDocument();
            assertNotNull(document);
        } catch (Exception e) {
            fail();
        }

    }
    
    /**
     * Test constructor for 'org.jvoicexml.xml.srgs.SrgsXmlDocument'.
     * 
     * @see SrgsXmlDocument(InputSource)
     */
    public void testInputConstructor() {
        try {
            final StringBuffer buffer = new StringBuffer();
            final File testFile = new File(BASE + "2/2_grammar_b.grxml");
            final FileReader reader = new FileReader(testFile);
            final InputSource source = new InputSource(reader);
            final SrgsXmlDocument document = new SrgsXmlDocument(source);
            assertNotNull(document);
            LOGGER.info(document);
            
        } catch (Exception e) {
            fail();
        }

    }

}
