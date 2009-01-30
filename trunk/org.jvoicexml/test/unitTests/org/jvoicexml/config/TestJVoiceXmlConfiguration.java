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

package org.jvoicexml.config;

import java.io.File;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.implementation.PlatformFactory;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;

/**
 * Test cases for {@link JVoiceXmlConfiguration}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestJVoiceXmlConfiguration {

    /**
     * Test method for {@link org.jvoicexml.config.JVoiceXmlConfiguration#getConfigurationFiles(java.lang.String)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetConfigurationFiles() throws Exception {
        JVoiceXmlConfiguration config = JVoiceXmlConfiguration.getInstance();
        Collection<File> files = config.getConfigurationFiles("implementation");
        Assert.assertEquals(1, files.size());
    }

    /**
     * Test case for {@link JVoiceXmlConfiguration#loadObjects(Class, String)}.
     * @throws Exception
     *            test failed
     */
    @Test
    public void testLoadObjects() throws Exception {
        JVoiceXmlConfiguration config = JVoiceXmlConfiguration.getInstance();
        final Collection<PlatformFactory> factories =
            config.loadObjects(PlatformFactory.class, "implementation");
        Assert.assertEquals(1, factories.size());
        final PlatformFactory factory = factories.iterator().next();
        Assert.assertEquals(
                "org.jvoicexml.implementation.text.TextPlatformFactory",
                factory.getClass().getCanonicalName());
    }
}
