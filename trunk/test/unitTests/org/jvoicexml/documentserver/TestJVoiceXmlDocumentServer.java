/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.documentserver;

import java.net.URI;

import junit.framework.TestCase;

import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.documentserver.schemestrategy.MappedDocumentStrategy;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Test case for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestJVoiceXmlDocumentServer
        extends TestCase {
    /** Mapped document repository. */
    private DocumentMap map;
    
    /** The server object to test. */
    private JVoiceXmlDocumentServer server;
    
    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        map = DocumentMap.getInstance();

        server = new JVoiceXmlDocumentServer();
        server.addSchemeStrategy(new MappedDocumentStrategy());
    }

    /**
     * Test method for {@link org.jvoicexml.documentserver.JVoiceXmlDocumentServer#getObject(java.net.URI, java.lang.String)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    public void testGetObject() throws JVoiceXMLEvent {
        String test = "Pinocchio";
        final URI uri = map.getUri("/test");
        map.addDocument(uri, test);
        
        Object object = server.getObject(uri, "text/plain");
        assertEquals(test, object);
        
    }
}
