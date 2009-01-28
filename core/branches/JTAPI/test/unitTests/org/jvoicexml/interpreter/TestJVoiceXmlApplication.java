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

import junit.framework.TestCase;

import org.jvoicexml.Application;

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
     * 'JVoiceXmlApplication.getUri()'.
     *
     * @see JVoiceXmlApplication#getCurrentUri()
     */
    public void testGetCurrentUri() {
        final String testApplication1 = "test1";
        final URI testUri1 = createUri("scheme", "ssp", "fragment");

        final Application application1 =
                new JVoiceXmlApplication(testUri1);

        assertEquals(testUri1, application1.getCurrentUri());

        final URI testUri2 = null;

        final Application application2 =
                new JVoiceXmlApplication(testUri2);

        assertNull(application2.getCurrentUri());
    }
}
