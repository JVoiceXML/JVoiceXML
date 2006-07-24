/*
 * File:    $RCSfile: TestApplicationRegistry.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/03/21 17:07:56 $
 * Author:  $Author: buente $
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

package org.jvoicexml.application;

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.application.JVoiceXmlApplication;
import org.jvoicexml.application.JVoiceXmlApplicationRegistry;

import junit.framework.TestCase;

/**
 * Test case for org.jvoicexml.application.ApplicationRegistry.
 * 
 * @see org.jvoicexml.application.ApplicationRegistry
 * 
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 * 
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestApplicationRegistry
        extends TestCase {

    /** The application registry to use. */
    private ApplicationRegistry registry;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
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
     * @return Create URI, <code>null</code> if we trapped inot an exception.
     */
    private URI createUri(final String scheme, final String ssp,
            final String fragment) {
        try {
            return new URI("scheme", "ssp", "fragment");
        } catch (URISyntaxException use) {
            fail("could not create URI " + use.toString());
        }

        return null;
    }

    /**
     * Test method for
     * 'org.jvoicexml.application.ApplicationRegistry.register(Application)'.
     */
    public void testRegister() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme1", "ssp1", "fragment1");

        final Application application1 = new JVoiceXmlApplication(testApplication1,
                testUri1);

        final String testApplication2 = "test2";
        final URI testUri2 = createUri("scheme2", "ssp2", "fragment2");

        final Application application2 = new JVoiceXmlApplication(testApplication2,
                testUri2);

        final String testApplication3 = testApplication1;
        final URI testUri3 = testUri1;

        final Application application3 = new JVoiceXmlApplication(testApplication3,
                testUri3);

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

        final Application application1 = new JVoiceXmlApplication(testApplication1,
                testUri1);

        final String testApplication2 = "test2";
        final URI testUri2 = createUri("scheme2", "ssp2", "fragment2");

        final Application application2 = new JVoiceXmlApplication(testApplication2,
                testUri2);

        final String testApplication3 = testApplication1;
        final URI testUri3 = testUri1;

        final Application application3 = new JVoiceXmlApplication(testApplication3,
                testUri3);

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
