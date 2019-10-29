/*
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Application;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for org.jvoicexml.interpreter.JVoiceXmlApplication.
 *
 * @see org.jvoicexml.application.JVoiceXmlApplication
 *
 * @author Dirk Schnelle-Walka
 */
public final class TestJVoiceXmlApplication  {
    /** The scope observer. */
    private ScopeObserver observer;

    /**
     * Test setup.
     */
    @Before
    public void setUp() {
        observer = new ScopeObserver();
    }

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
            Assert.fail("could not create URI " + use.toString());
        }

        return null;
    }

    /**
     * Test method for
     * 'JVoiceXmlApplication.addDocument()'.
     *
     * @see JVoiceXmlApplication#addDocument(org.jvoicexml.xml.vxml.VoiceXmlDocument)
     * @exception JVoiceXMLEvent
     *            test failed.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testAddDocument() throws JVoiceXMLEvent, Exception {
        final Application application = new JVoiceXmlApplication(observer);

        VoiceXmlDocument doc1 = new VoiceXmlDocument();
        final Vxml vxml1 = doc1.getVxml();
        final URI testUri1 = createUri("scheme", "host", "/path", "fragment");

        vxml1.setXmlBase(testUri1);

        Assert.assertFalse(application.isLoaded(testUri1));
        application.addDocument(testUri1, doc1);
        Assert.assertTrue(application.isLoaded(testUri1));
        Assert.assertEquals(testUri1, application.getXmlBase());

        VoiceXmlDocument doc2 = new VoiceXmlDocument();
        final Vxml vxml2 = doc2.getVxml();
        final URI testUri2 = createUri("scheme", "host", "/path", "fragment");
        vxml2.setXmlBase(testUri2);

        application.addDocument(testUri2, doc2);
        Assert.assertTrue(application.isLoaded(testUri2));

        Assert.assertEquals(testUri1, application.getXmlBase());

        VoiceXmlDocument doc3 = new VoiceXmlDocument();
        final Vxml vxml3 = doc3.getVxml();
        final URI testUri3 =
            createUri("scheme3", "host3", "/path3", "fragment3");
        vxml3.setXmlBase(testUri3);
        application.addDocument(testUri3, doc3);
        Assert.assertEquals(testUri3, application.getXmlBase());
        final URI testUri31 =
            createUri("scheme3", "host3", "/path3", null);
        Assert.assertTrue("fragment less document is also loaded",
                application.isLoaded(testUri31));
    }

    /**
     * Test method for
     * 'JVoiceXmlApplication.resolve()'.
     *
     * @see JVoiceXmlApplication#resolve(URI)
     * @exception JVoiceXMLEvent
     *            test failed
     * @exception Exception
     *            test failed
     */
    @Test
    public void testResolve() throws JVoiceXMLEvent, Exception {
        final Application application = new JVoiceXmlApplication(observer);
        VoiceXmlDocument doc1 = new VoiceXmlDocument();
        final Vxml vxml1 = doc1.getVxml();
        final URI testUri1 =
            createUri("scheme", "host", "/path/subpath1", "fragment");

        vxml1.setXmlBase(testUri1);

        application.addDocument(testUri1, doc1);

        final URI testUri2 =
            createUri("scheme", "host", "/path/subpath2", "fragment");
        Assert.assertEquals(testUri2, application.resolve(testUri2));

        final URI testUri3 =
            createUri("scheme", "host", "/path/subpath3", null);
        URI testUri3Relative = new URI("subpath3");
        Assert.assertEquals(testUri3, application.resolve(testUri3Relative));

        final URI testUri4 =
            createUri("scheme", "host", "/path/subpath3/extendedsubpath", null);
        URI testUri4Relative = new URI("subpath3/extendedsubpath");
        Assert.assertEquals(testUri4, application.resolve(testUri4Relative));

        final URI testUri5 =
            createUri("scheme2", "host", "/path/subpath3", null);
        Assert.assertEquals(testUri5, application.resolve(testUri5));

        Assert.assertNull(application.resolve(null));

        application.addDocument(testUri5, null);
    }

    /**
     * Test method for {@link JVoiceXmlApplication#resolve(URI).}
     * @throws Exception test failed
     * @throws JVoiceXMLEvent test failed
     * @since 0.7.9
     */
    @Test
    public void testResolveNullHostUri() throws Exception, JVoiceXMLEvent {
        final Application application = new JVoiceXmlApplication(observer);
        final URI uri = new URI("res://root.vxml");
        VoiceXmlDocument doc1 = new VoiceXmlDocument();
        final Vxml vxml1 = doc1.getVxml();
        final URI base = new URI("res:///");
        vxml1.setXmlBase(base);
        application.addDocument(uri, doc1);
        final URI relativeUri = new URI("relative.vxml");
        final URI testUri = new URI("res://relative.vxml");
        Assert.assertEquals(testUri, application.resolve(relativeUri));
    }
}
