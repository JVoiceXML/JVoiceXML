/*
 * File:    $RCSfile: TokenList.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2005/12/13 08:28:24 $
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

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Many of the tags feature lists of strings separated by a space. This class
 * helps developers to create these lists easily and to iterate over them. This
 * means <em>real</em> list access.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 */
public final class TokenList
        extends AbstractList<String> {
    /** The encapsulated list. */
    private final List<String> list;

    /**
     * Construct a new object.
     */
    public TokenList() {
        list = new java.util.ArrayList<String>();
    }

    /**
     * Create a new <code>TokenList</code> with values from the given list of
     * strings separated by spaces.
     *
     * @param stringlist
     *        List of strings separated by spaces.
     */
    public TokenList(final String stringlist) {
        this();

        if (stringlist != null) {
            final StringTokenizer tokenizer = new StringTokenizer(stringlist,
                    " ");

            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();

                add(token);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(final int index) {
        return list.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();

        final Iterator<String> iterator = iterator();
        while (iterator.hasNext()) {
            final String token = iterator.next();

            str.append(token);

            if (iterator.hasNext()) {
                str.append(' ');
            }
        }

        return str.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Adding <code>null</code> is not allowed and throws a
     * <code>NullPointerException</code>.
     *
     * Addition of an empty string has no effect on the list.
     * </p>
     *
     * @return <code>true</code> if the string was appendend.
     */
    @Override
    public boolean add(final String o) {
        if (o == null) {
            throw new NullPointerException(
                    "null must not be added to a TokenList!");
        }

        if (o.trim().length() == 0) {
            return false;
        }

        return list.add(o);
    }

}
