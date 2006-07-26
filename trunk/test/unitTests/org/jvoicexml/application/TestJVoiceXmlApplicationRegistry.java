/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java $
 * Version: $LastChangedRevision: 23 $
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.application;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;

/**
 * Test case for org.jvoicexml.application.JVoiceXmlApplicationRegistry.
 *
 * @see org.jvoicexml.application.JVoiceXmlApplicationRegistry
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision: 23 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestJVoiceXmlApplicationRegistry
        extends TestCase {

    /** The application registry to use. */
    private ApplicationRegistry registry;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        registry = new JVoiceXmlApplicationRegistry();

        super.setUp();
    }

    /**
     * Convenient method to create an URI without need to catch the exception.
     *
     * @param scheme
     *        Scheme name.
     * @param ssp
     *        Specific part.
     * @param fragment
     *        Fragment.
     * @return Create URI, <code>null</code> if we trapped into an exception.
     */
    private URI createUri(final String scheme, final String ssp,
                          final String fragment) {
        try {
            return new URI(scheme, ssp, fragment);
        } catch (URISyntaxException use) {
            fail("could not create URI " + use.toString());
        }

        return null;
    }

    /**
     * Test method for
     * 'JVoiceXmlApplicationRegistry.creatApplication(id, uri)
     */
    public void testCreateApplication() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme1", "ssp1", "fragment1");

        final Application application1 =
                registry.createApplication(testApplication1, testUri1);

        assertNotNull(application1);
        assertEquals(testApplication1, application1.getId());
        assertEquals(testUri1, application1.getUri());
        assertNull(registry.getApplication(testApplication1));

        final String testApplication2 = null;
        final URI testUri2 = createUri("scheme1", "ssp1", "fragment1");

        final Application application2 =
                registry.createApplication(testApplication2, testUri2);

        assertNotNull(application2);
        assertNull(application2.getId());
        assertEquals(testUri2, application2.getUri());
        assertNull(registry.getApplication(testApplication2));

        final String testApplication3 = "test1";
        final URI testUri3 = null;

        final Application application3 =
                registry.createApplication(testApplication3, testUri3);

        assertNotNull(application3);
        assertEquals(testApplication3, application3.getId());
        assertNull(application3.getUri());
        assertNull(registry.getApplication(testApplication3));

        final String testApplication4 = null;
        final URI testUri4 = null;

        final Application application4=
                registry.createApplication(testApplication4, testUri4);

        assertNotNull(application4);
        assertNull(application4.getId());
        assertNull(application4.getUri());
        assertNull(registry.getApplication(testApplication4));
    }

    /**
     * Test method for
     * 'org.jvoicexml.application.JVoiceXmlApplicationRegistry.register(Application)'.
     */
    public void testRegister() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme1", "ssp1", "fragment1");

        final Application application1 =
                registry.createApplication(testApplication1, testUri1);

        final String testApplication2 = "test2";
        final URI testUri2 = createUri("scheme2", "ssp2", "fragment2");

        final Application application2 =
                registry.createApplication(testApplication2, testUri2);

        final String testApplication3 = testApplication1;
        final URI testUri3 = testUri1;

        final Application application3 =
                registry.createApplication(testApplication3, testUri3);

        registry.register(application1);
        registry.register(application2);
        registry.register(application3);
        registry.register(application1);
        registry.register(null);

    }

    /**
     * Test method for
     * 'org.jvoicexml.application.ApplicationRegistry.getApplication(String)'.
     */
    public void testGetApplication() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme1", "ssp1", "fragment1");

        final Application application1 =
                registry.createApplication(testApplication1, testUri1);

        final String testApplication2 = "test2";
        final URI testUri2 = createUri("scheme2", "ssp2", "fragment2");

        final Application application2 =
                registry.createApplication(testApplication2, testUri2);

        final String testApplication3 = testApplication1;
        final URI testUri3 = testUri1;

        final Application application3 =
                registry.createApplication(testApplication3, testUri3);

        registry.register(application1);
        assertEquals(application1, registry.getApplication(testApplication1));

        registry.register(application2);
        assertEquals(application1, registry.getApplication(testApplication1));
        assertEquals(application2, registry.getApplication(testApplication2));

        registry.register(application3);
        assertEquals(application3, registry.getApplication(testApplication1));
        assertEquals(application2, registry.getApplication(testApplication2));
        assertEquals(application3, registry.getApplication(testApplication3));

        registry.register(application1);
        assertEquals(application1, registry.getApplication(testApplication1));
        assertEquals(application2, registry.getApplication(testApplication2));
        assertEquals(application1, registry.getApplication(testApplication3));

        registry.register(null);
        assertEquals(application1, registry.getApplication(testApplication1));
        assertEquals(application2, registry.getApplication(testApplication2));
        assertEquals(application1, registry.getApplication(testApplication3));
        assertEquals(application1, registry.getApplication(testApplication1));
        assertEquals(application2, registry.getApplication(testApplication2));
        assertNull(registry.getApplication(null));
    }

}
