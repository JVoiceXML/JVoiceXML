/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.documentserver.schemestrategy;

import java.net.URI;
import java.util.Map;

import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Test cases for {@link HttpSchemeStrategy}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
public final class TestHttpSchemeStrategy {
    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.HttpSchemeStrategy#getInputStream(org.jvoicexml.Session, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, java.util.Map)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            expected error
     */
    @Test(expected = BadFetchError.class)
    public void testGetInputStream() throws Exception, BadFetchError {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final Session session = new JVoiceXmlSession(platform, jvxml, null);
        final HttpSchemeStrategy strategy = new HttpSchemeStrategy();
        final URI uri = new URI("http://localhost:8080?session=id");
        final Map<String, Object> parameters =
            new java.util.HashMap<String, Object>();
        parameters.put("firstName", "Horst");
        parameters.put("lastName", "Buchholz");
        final String sessionId = session.getSessionID();
        strategy.getInputStream(sessionId, uri, RequestMethod.GET,
                0, parameters);
    }

}
