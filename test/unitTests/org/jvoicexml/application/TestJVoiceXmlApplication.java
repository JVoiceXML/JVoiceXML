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

package org.jvoicexml.application;

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.Application;
import org.jvoicexml.application.JVoiceXmlApplication;

import junit.framework.TestCase;

/**
 * Test case for org.jvoicexml.application.JVoiceXmlApplication.
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
     * @param ssp
     *        Specific part.
     * @param fragment
     *        Fragment.
     * @return Create URI, <code>null</code> if we trapped inot an exception.
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
     * 'org.jvoicexml.application.JVoiceXmlApplication.getId()'.
     *
     * @see JVoiceXmlApplication#getId()
     */
    public void testGetId() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme", "ssp", "fragment");

        final Application application1 =
                new JVoiceXmlApplication(testApplication1, testUri1);

        assertEquals(testApplication1, application1.getId());

        final String testApplication2 = null;
        final URI testUri2 = createUri("scheme", "ssp", "fragment");

        final Application application2 =
                new JVoiceXmlApplication(testApplication2, testUri2);

        assertNull(application2.getId());

        final String testApplication3 = null;
        final URI testUri3 = null;

        final Application application3 =
                new JVoiceXmlApplication(testApplication3, testUri3);

        assertNull(application3.getId());

        final String testApplication4 = "test4";
        final URI testUri4 = null;

        final Application application4 =
                new JVoiceXmlApplication(testApplication4, testUri4);

        assertEquals(testApplication4, application4.getId());
    }

    /**
     * Test method for
     * 'org.jvoicexml.application.JVoiceXmlApplication.getUri()'.
     *
     * @see JVoiceXmlApplication#getUri()
     */
    public void testGetUri() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme", "ssp", "fragment");

        final Application application1 =
                new JVoiceXmlApplication(testApplication1, testUri1);

        assertEquals(testUri1, application1.getUri());

        final String testApplication2 = null;
        final URI testUri2 = createUri("scheme", "ssp", "fragment");

        final Application application2 =
                new JVoiceXmlApplication(testApplication2, testUri2);

        assertNotNull(application2.getUri());

        final String testApplication3 = null;
        final URI testUri3 = null;

        final Application application3 =
                new JVoiceXmlApplication(testApplication3, testUri3);

        assertNull(application3.getUri());

        final String testApplication4 = "test4";
        final URI testUri4 = null;

        final Application application4 =
                new JVoiceXmlApplication(testApplication4, testUri4);

        assertNull(application4.getUri());
    }
}
