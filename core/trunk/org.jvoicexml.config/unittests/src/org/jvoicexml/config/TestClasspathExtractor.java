/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/config/TestClasspathExtractor.java $
 * Version: $LastChangedRevision: 2153 $
 * Date:    $Date: 2010-04-14 09:25:59 +0200 (Mi, 14 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
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

import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link ClasspathExtractor}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2153 $
 * @since 0.7
 */
public final class TestClasspathExtractor {

    /**
     * Test method for {@link org.jvoicexml.config.ClasspathExtractor#getClasspathEntries()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetClasspathEntries() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final Source source =
            new StreamSource("unittests/config/test-implementation.xml");
        final ClasspathExtractor extractor = new ClasspathExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        final URL[] urls = extractor.getClasspathEntries();
        Assert.assertEquals(1, urls.length);
        Assert.assertTrue(urls[0].toString().indexOf("test/classes") > 0);
    }

    /**
     * Test method for {@link org.jvoicexml.config.ClasspathExtractor#getLoaderRepostory()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetLoaderRepository() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer = factory.newTransformer();
        final Source source =
            new StreamSource("unittests/config/test-implementation.xml");
        final ClasspathExtractor extractor = new ClasspathExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        final String repository = extractor.getLoaderRepostory();
        Assert.assertEquals("deschd", repository);
    }

}
