/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/config/TestBeansFilter.java $
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Test cases for {@link BeansFilter}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2153 $
 * @since 0.7
 */
public final class TestBeansFilter {
    /**
     * Test method for the filter.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testFilter() throws Exception {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final TransformerHandler th = ((SAXTransformerFactory) tf)
                .newTransformerHandler();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        th.setResult(result);
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        spf.setNamespaceAware(true);
        spf.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        final SAXParser parser = spf.newSAXParser();
        final XMLFilterImpl filter = new BeansFilter(parser.getXMLReader());
        filter.setContentHandler(th);
        final InputStream in =
            new FileInputStream("test/config/test-implementation.xml");
        final InputSource input = new InputSource(in);
        filter.parse(input);
        final String str = out.toString();
        Assert.assertTrue("classpath should be removed",
                str.indexOf("classpath") < 0);
        Assert.assertTrue("repository should be removed",
                str.indexOf("repository") < 0);
    }

}
