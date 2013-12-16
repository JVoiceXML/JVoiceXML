/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.voicexmlunit/src/org/jvoicexml/voicexmlunit/Call.java $
 * Version: $LastChangedRevision: 4058 $
 * Date:    $Date: 2013-12-07 06:32:30 +0100 (Sat, 07 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Assertion, that is able to handle xpath expressions.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4058 $
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
     * <code>document</code> node is equal
     * to <code>expected</code>. 
     * @param document the current XML document
     * @param expression the XPath expression to evaluate
     * @param expected the expected result
     * @exception XPathExpressionException
     *            error evaluating the XPath expression
     */
    public static void assertEquals(final Document document,
            final String expression, final Node expected)
                    throws XPathExpressionException {
        final XPathFactory xpathFactory = XPathFactory.newInstance();
        final XPath xpath = xpathFactory.newXPath();
        final Node node = (Node) xpath.evaluate(expression,
                document, XPathConstants.NODE);
        Assert.assertEquals(expected, node);
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
        final Node node = (Node) xpath.evaluate(expression,
                document, XPathConstants.NODE);
        final String actual = node.getNodeValue();
        Assert.assertEquals(expected, actual);
    }
}