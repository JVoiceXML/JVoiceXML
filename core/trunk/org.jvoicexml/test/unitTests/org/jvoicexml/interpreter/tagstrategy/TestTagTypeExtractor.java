/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.interpreter.tagstrategy;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link TagTypeExtractor}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public class TestTagTypeExtractor {

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.TagTypeExtractor#getTagType()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetTagType() throws Exception {
        final TransformerFactory transformerFactory =
            TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final File file = new File("test/config/test-tagsupport.xml");
        final Source source = new StreamSource(file);
        final TagTypeExtractor extractor = new TagTypeExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        Assert.assertEquals(TagType.EXECUTABLE, extractor.getTagType());
    }

}
