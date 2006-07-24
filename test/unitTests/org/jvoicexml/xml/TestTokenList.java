/*
 * File:    $RCSfile: TestTokenList.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2005/07/28 07:28:10 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

/**
 * Test case for org.jvoicexml.xml.TokenList.
 * 
 * @see org.jvoicexml.xml.TokenList
 * 
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 * 
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestTokenList
        extends TestCase {
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
     * Test method for 'org.jvoicexml.xml.TokenList.size()'.
     * 
     * @see TokenList#size()
     */
    public void testSize() {
        final TokenList list1 = new TokenList();
        assertEquals(0, list1.size());

        list1.add(TOKEN1);
        assertEquals(1, list1.size());

        list1.add(TOKEN2);
        assertEquals(2, list1.size());

        final TokenList list2 = new TokenList(TOKEN_LIST);
        assertEquals(TOKEN_LIST_SIZE, list2.size());

        final TokenList list3 = new TokenList("");
        assertEquals(0, list3.size());

        final TokenList list4 = new TokenList(null);
        assertEquals(0, list4.size());
    }

    /**
     * Test method for 'org.jvoicexml.xml.TokenList.get(int)'.
     * 
     * @see TokenList#get(int)
     */
    public void testGetInt() {
        final TokenList list1 = new TokenList();

        try {
            final String str = list1.get(0);
            fail("retrieved str: '" + str + "'");
        } catch (IndexOutOfBoundsException ioobe) {
        }

        list1.add(TOKEN1);
        assertEquals(TOKEN1, list1.get(0));
        try {
            final String str = list1.get(1);
            fail("retrieved str: '" + str + "'");
        } catch (IndexOutOfBoundsException ioobe) {
        }

        list1.add(TOKEN2);
        assertEquals(TOKEN1, list1.get(0));
        assertEquals(TOKEN2, list1.get(1));
        try {
            final String str = list1.get(2);
            fail("retrieved str: '" + str + "'");
        } catch (IndexOutOfBoundsException ioobe) {
        }

        final TokenList list2 = new TokenList(TOKEN_LIST);
        assertEquals(TOKEN1, list2.get(0));
        assertEquals(TOKEN2, list2.get(1));
        assertEquals(TOKEN3, list2.get(2));
    }

    /**
     * Test method for 'org.jvoicexml.xml.TokenList.add(String)'.
     * 
     * @see TokenList#add(String)
     */
    public void testAddString() {
        final TokenList list = new TokenList();

        assertEquals(true, list.add(TOKEN1));
        assertEquals(TOKEN1, list.get(list.size() - 1));
        assertEquals(true, list.add(TOKEN2));
        assertEquals(TOKEN2, list.get(list.size() - 1));
        assertEquals(true, list.add(TOKEN3));
        assertEquals(TOKEN3, list.get(list.size() - 1));

        try {
            list.add(null);
            fail("added null");
        } catch (NullPointerException npe) {

        }

        assertEquals(false, list.add(""));
        assertEquals(false, list.add("   "));
    }

    /**
     * Test method for 'org.jvoicexml.xml.TokenList.toString'.
     * 
     * @see TokenList#toString()
     */
    public void testToString() {
        final TokenList list1 = new TokenList();
        list1.add(TOKEN1);
        list1.add(TOKEN2);
        list1.add(TOKEN3);

        assertEquals(TOKEN_LIST, list1.toString());

        final TokenList list2 = new TokenList(TOKEN_LIST);
        assertEquals(TOKEN_LIST, list2.toString());

    }
}
