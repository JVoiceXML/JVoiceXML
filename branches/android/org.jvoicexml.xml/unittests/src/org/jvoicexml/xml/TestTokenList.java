/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/unittests/src/org/jvoicexml/xml/TestTokenList.java $
 * Version: $LastChangedRevision: 3829 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for org.jvoicexml.xml.TokenList.
 *
 * @see org.jvoicexml.xml.TokenList
 *
 * @author Dirk Schnelle
 * @version $Revision: 3829 $
 */
public final class TestTokenList {
    /** Test token 1. */
    private static final String TOKEN1 = "token1";

    /** Test token 2. */
    private static final String TOKEN2 = "token2";

    /** Test token 3. */
    private static final String TOKEN3 = "token3";

    /** A list of the test tokens. */
    private static final String TOKEN_LIST = TOKEN1 + " " + TOKEN2 + " "
            + TOKEN3;

    /** Number of tokens in the <code>TOKEN_LIST</code>. */
    private static final int TOKEN_LIST_SIZE = 3;

    /**
     * Test method for 'TokenList.size()'.
     *
     * @see TokenList#size()
     */
    @Test
    public void testSize() {
        final TokenList list1 = new TokenList();
        Assert.assertEquals(0, list1.size());

        list1.add(TOKEN1);
        Assert.assertEquals(1, list1.size());

        list1.add(TOKEN2);
        Assert.assertEquals(2, list1.size());

        final TokenList list2 = new TokenList(TOKEN_LIST);
        Assert.assertEquals(TOKEN_LIST_SIZE, list2.size());

        final TokenList list3 = new TokenList("");
        Assert.assertEquals(0, list3.size());

        final TokenList list4 = new TokenList(null);
        Assert.assertEquals(0, list4.size());
    }

    /**
     * Test method for 'TokenList.get(int)'.
     *
     * @see TokenList#get(int)
     */
    @Test
    public void testGetInt() {
        final TokenList list1 = new TokenList();

        try {
            final String str = list1.get(0);
            Assert.fail("retrieved str: '" + str + "'");
        } catch (IndexOutOfBoundsException ioobe) {
        }

        list1.add(TOKEN1);
        Assert.assertEquals(TOKEN1, list1.get(0));
        try {
            final String str = list1.get(1);
            Assert.fail("retrieved str: '" + str + "'");
        } catch (IndexOutOfBoundsException ioobe) {
        }

        list1.add(TOKEN2);
        Assert.assertEquals(TOKEN1, list1.get(0));
        Assert.assertEquals(TOKEN2, list1.get(1));
        try {
            final String str = list1.get(2);
            Assert.fail("retrieved str: '" + str + "'");
        } catch (IndexOutOfBoundsException ioobe) {
        }

        final TokenList list2 = new TokenList(TOKEN_LIST);
        Assert.assertEquals(TOKEN1, list2.get(0));
        Assert.assertEquals(TOKEN2, list2.get(1));
        Assert.assertEquals(TOKEN3, list2.get(2));
    }

    /**
     * Test method for 'TokenList.add(String)'.
     *
     * @see TokenList#add(String)
     */
    @Test
    public void testAddString() {
        final TokenList list = new TokenList();

        Assert.assertEquals(true, list.add(TOKEN1));
        Assert.assertEquals(TOKEN1, list.get(list.size() - 1));
        Assert.assertEquals(true, list.add(TOKEN2));
        Assert.assertEquals(TOKEN2, list.get(list.size() - 1));
        Assert.assertEquals(true, list.add(TOKEN3));
        Assert.assertEquals(TOKEN3, list.get(list.size() - 1));

        try {
            list.add(null);
            Assert.fail("added null");
        } catch (NullPointerException npe) {
        }

        Assert.assertEquals(false, list.add(""));
        Assert.assertEquals(false, list.add("   "));
    }

    /**
     * Test method for 'TokenList.toString'.
     *
     * @see TokenList#toString()
     */
    @Test
    public void testToString() {
        final TokenList list1 = new TokenList();
        list1.add(TOKEN1);
        list1.add(TOKEN2);
        list1.add(TOKEN3);

        Assert.assertEquals(TOKEN_LIST, list1.toString());

        final TokenList list2 = new TokenList(TOKEN_LIST);
        Assert.assertEquals(TOKEN_LIST, list2.toString());
    }
}
