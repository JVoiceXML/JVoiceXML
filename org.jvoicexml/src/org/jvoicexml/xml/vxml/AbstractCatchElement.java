/*
 * File:    $RCSfile: AbstractCatchElement.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * The <code>&lt;catch&gt;</code> element associates a catch with a document,
 * dialog, or form item (except for blocks). It contains executable content.
 *
 * <p>
 * Main purpose of this call is to unify the commonalities of all
 * <code>&lt;catch&gt;</code> tags.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.xml.vxml.VoiceXmlDocument
 *
 * @author Dirk Schnelle
 * @author Steve Doyle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public abstract class AbstractCatchElement
        extends AbstractVoiceXmlNode {
    /**
     * The occurrence of the event (default is 1). The count allows you to
     * handle different occurrences of the same event differently.
     */
    public static final String ATTRIBUTE_COUNT = "count";

    /**
     * An expression which must evaluate to true after conversion to boolean in
     * order for the event to be caught. Defaults to true.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * Construct a new object.
     *
     * @param node
     *        The encapsulated node.
     */
    AbstractCatchElement(final Node node) {
        super(node);
    }

    /**
     * Construct a new object.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    protected AbstractCatchElement(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Retrieve the count attribute.
     *
     * @return Value of the count attribute.
     * @see #ATTRIBUTE_COUNT
     */
    public final String getCount() {
        final String count = getAttribute(ATTRIBUTE_COUNT);
        if (count != null) {
            return count;
        }

        return Integer.toString(1);
    }

    /**
     * Set the count attribute.
     *
     * @param count
     *        Value of the count attribute.
     * @see #ATTRIBUTE_COUNT
     */
    public final void setCount(final String count) {
        setAttribute(ATTRIBUTE_COUNT, count);
    }

    /**
     * Retrieve the cond attribute.
     *
     * @return Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public final String getCond() {
        return getAttribute(ATTRIBUTE_COND);
    }

    /**
     * Set the cond attribute.
     *
     * @param cond
     *        Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public final void setCond(final String cond) {
        setAttribute(ATTRIBUTE_COND, cond);
    }

    /**
     * Retrieve a list with all events, caught by this catch element.
     *
     * @return List with all events, caught by this catch element.
     */
    public abstract TokenList getEventList();
}
