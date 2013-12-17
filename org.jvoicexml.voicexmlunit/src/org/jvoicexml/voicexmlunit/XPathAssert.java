/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.jvoicexml.xml.XmlDocument;
import org.w3c.dom.Document;


/**
 * Assertion, that is able to handle xpath expressions.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public final class XPathAssert {
    /**
     * Do not make instances.
     */
    private XPathAssert() {
    }

    /**
     * Checks if the evaluation of <code>expression</code> on
     * <code>document</code> node's node value
     * is equal to <code>expected</code>. 
     * @param document the current XML document
     * @param expression the XPath expression to evaluate
     * @param expected the expected result
     * @exception XPathExpressionException
     *            error evaluating the XPath expression
     */
    public static void assertEquals(final Document document,
            final String expression, final String expected)
                    throws XPathExpressionException {
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();
        final Document doc;
        if (document instanceof XmlDocument) {
            final XmlDocument xmldocument = (XmlDocument) document;
            doc = xmldocument.getDocument();
        } else {
            doc = document;
        }
        final String actual = (String) xpath.evaluate(expression,
                doc, XPathConstants.STRING);
        Assert.assertEquals(expected, actual);
    }
}
