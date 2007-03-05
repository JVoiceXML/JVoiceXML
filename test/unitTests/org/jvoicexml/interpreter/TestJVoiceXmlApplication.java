/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.jvoicexml.Application;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for org.jvoicexml.interpreter.JVoiceXmlApplication.
 *
 * @see org.jvoicexml.application.JVoiceXmlApplication
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestJVoiceXmlApplication
        extends TestCase {

    /**
     * Convenient method to create an URI without need to catch the exception.
     *
     * @param scheme
     *        Scheme name.
     * @param host
     *        Host
     * @param path
     *        Path
     * @param fragment
     *        Fragment.
     * @return Create URI, <code>null</code> if we trapped into an exception.
     */
    private URI createUri(final String scheme, final String host,
                final String path, final String fragment) {
        try {
            return new URI(scheme, host, path, fragment);
        } catch (URISyntaxException use) {
            fail("could not create URI " + use.toString());
        }

        return null;
    }

    /**
     * Test method for
     * 'JVoiceXmlApplication.addDocument()'.
     *
     * @see JVoiceXmlApplication#addDocument(org.jvoicexml.xml.vxml.VoiceXmlDocument)
     */
    public void testAddDocument() {
        final Application application = new JVoiceXmlApplication();

        VoiceXmlDocument doc1 = null;
        try {
            doc1 = new VoiceXmlDocument();
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        }
        final Vxml vxml1 = doc1.getVxml();
        final URI testUri1 = createUri("scheme", "host", "/path", "fragment");

        vxml1.setXmlBase(testUri1);

        try {
            application.addDocument(doc1);
        } catch (BadFetchError e) {
            fail(e.getMessage());
        }

        assertEquals(testUri1, application.getApplication());

        VoiceXmlDocument doc2 = null;
        try {
            doc2 = new VoiceXmlDocument();
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        }
        final Vxml vxml2 = doc2.getVxml();
        final URI testUri2 = createUri("scheme", "host", "/path", "fragment");
        vxml2.setXmlBase(testUri2);

        try {
            application.addDocument(doc2);
        } catch (BadFetchError e) {
            fail(e.getMessage());
        }

        assertEquals(testUri1, application.getApplication());

        VoiceXmlDocument doc3 = null;
        try {
            doc3 = new VoiceXmlDocument();
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        }
        final Vxml vxml3 = doc3.getVxml();
        final URI testUri3 =
            createUri("scheme3", "host3", "/path3", "fragment3");
        vxml3.setXmlBase(testUri3);

        try {
            application.addDocument(doc3);
        } catch (BadFetchError e) {
            fail(e.getMessage());
        }

        assertEquals(testUri3, application.getApplication());
    }

    /**
     * Test method for
     * 'JVoiceXmlApplication.resolve()'.
     *
     * @see JVoiceXmlApplication#resolve(URI)
     */
    public void testResolve() {
        final Application application = new JVoiceXmlApplication();
        VoiceXmlDocument doc1 = null;
        try {
            doc1 = new VoiceXmlDocument();
        } catch (ParserConfigurationException e) {
            fail(e.getMessage());
        }
        final Vxml vxml1 = doc1.getVxml();
        final URI testUri1 =
            createUri("scheme", "host", "/path/subpath1", "fragment");

        vxml1.setXmlBase(testUri1);

        try {
            application.addDocument(doc1);
        } catch (BadFetchError e) {
            fail(e.getMessage());
        }

        final URI testUri2 =
            createUri("scheme", "host", "/path/subpath2", "fragment");
        assertEquals(testUri2, application.resolve(testUri2));

        final URI testUri3 =
            createUri("scheme", "host", "/path/subpath3", null);
        URI testUri3Relative = null;
        try {
            testUri3Relative = new URI("subpath3");
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
        assertEquals(testUri3, application.resolve(testUri3Relative));

        final URI testUri4 =
            createUri("scheme", "host", "/path/subpath3/extendedsubpath", null);
        URI testUri4Relative = null;
        try {
            testUri4Relative = new URI("subpath3/extendedsubpath");
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
        assertEquals(testUri4, application.resolve(testUri4Relative));

        final URI testUri5 =
            createUri("scheme2", "host", "/path/subpath3", null);
        assertEquals(testUri5, application.resolve(testUri5));

        assertNull(application.resolve(null));
    }
}
