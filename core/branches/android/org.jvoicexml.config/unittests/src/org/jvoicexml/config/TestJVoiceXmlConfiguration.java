/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.config/unittests/src/org/jvoicexml/config/TestJVoiceXmlConfiguration.java $
 * Version: $LastChangedRevision: 2605 $
 * Date:    $Date: 2011-02-20 04:38:38 -0600 (dom, 20 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.FilenameFilter;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jvxml.DummyTelephonySupportFactory;

/**
 * Test cases for {@link JVoiceXmlConfiguration}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2605 $
 * @since 0.7
 */
public final class TestJVoiceXmlConfiguration {
    /**
     * Initializes this test case.
     * @exception Exception
     *            test failed
     * @since 0.7.4
     */
    @BeforeClass
    public static void init() throws Exception {
        final File file = new File("unittests/config");
        final String path = file.getCanonicalPath();
        System.setProperty("jvoicexml.config", path);
    }

    /**
     * Test method for {@link org.jvoicexml.config.JVoiceXmlConfiguration#getConfigurationFiles(java.lang.String)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetConfigurationFiles() throws Exception {
        final JVoiceXmlConfiguration config =
            new JVoiceXmlConfiguration();
        final Collection<File> files =
            config.getConfigurationFiles("implementation");
        final File dir = new File("unittests/config");
        final FilenameFilter filter = new FilenameFilter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("-implementation.xml");
            }
        };
        final String[] impls = dir.list(filter);
        Assert.assertEquals(impls.length, files.size());
    }

    /**
     * Test case for {@link JVoiceXmlConfiguration#loadObjects(Class, String)}.
     * @throws Exception
     *            test failed
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void testLoadObjects() throws Exception {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration();
        final Collection<ResourceFactory> factories =
            config.loadObjects(ResourceFactory.class, "implementation");
        Assert.assertEquals(1, factories.size());
        final ResourceFactory factory = factories.iterator().next();
        Assert.assertEquals(DummyTelephonySupportFactory.class,
                factory.getClass());
    }
}
