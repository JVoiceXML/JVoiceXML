/*
 * File:    $RCSfile: Goto.java,v $
 * Version: $Revision: 1.7 $
 * Date:    $Date: 2006/05/16 07:26:22 $
 * Author:  $Author: schnelle $
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

import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * Go to another dialog in the same or different document.
 *
 * <p>
 * The <code>&lt;goto&gt;</code> element is used to:
 * </p>
 * <ul>
 * <li>
 * transition to another form item in the current form,
 * </li>
 * <li>
 * transition to another dialog in the current document, or
 * </li>
 * <li>
 * transition to another document.
 * </li>
 * </ul>
 *
 * <p>
 * To transition to another form item, use the nextitem attribute, or the
 * expritem attribute if the form item name is computed using an ECMAScript
 * expression:
 * </p>
 * <p>
 * <code>
 * &lt;goto nextitem="ssn_confirm"/&gt;<br>
 * &lt;goto expritem="(type==12)? 'ssn_confirm' : 'reject'"/&gt;
 * </code>
 * </p>
 * <p>
 * To go to another dialog in the same document, use next (or expr) with only a
 * URI fragment:
 * </p>
 * <p>
 * <code>
 * &lt;goto next="#another_dialog"/&gt;<br>
 * &lt;goto expr="'#' + 'another_dialog'"/&gt;
 * </code>
 * </p>
 * <p>
 * To transition to another document, use next (or expr) with a URI:
 * </p>
 * <p>
 * <code>
 * &lt;goto next="http://flight.example.com/reserve_seat"/&gt;<br>
 * &lt;goto next="./special_lunch#wants_vegan"/&gt;
 * </code>
 * </p>
 * <p>
 * The URI may be absolute or relative to the current document. You may specify
 * the starting dialog in the next document using a fragment that corresponds to
 * the value of the id attribute of a dialog. If no fragment is specified, the
 * first dialog in that document is chosen.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Steve Doyle
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Goto
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "goto";

    /**
     * The URI to which to transition.
     */
    public static final String ATTRIBUTE_NEXT = "next";

    /**
     * An ECMAScript expression that yields the URI.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * The name of the next form item to visit in the current form.
     */
    public static final String ATTRIBUTE_NEXTITEM = "nextitem";

    /**
     * An ECMAScript expression that yields the name of the next form item to
     * visit.
     */
    public static final String ATTRIBUTE_EXPRITEM = "expritem";

    /**
     * The URI of the audio clip to play while the fetch is being done. If not
     * specified, the fetchaudio property is used, and if that property is not
     * set, no audio is played during the fetch. The fetching of the audio clip
     * is governed by the audiofetchhint, audiomaxage, audiomaxstale, and
     * fetchtimeout properties in effect at the time of the fetch. The playing
     * of the audio clip is governed by the fetchaudiodelay, and
     * fetchaudiominimum properties in effect at the time of the fetch.
     */
    public static final String ATTRIBUTE_FETCHAUDIO = "fetchaudio";

    /**
     * The interval to wait for the content to be returned before throwing an
     * error.badfetch event. This defaults to the fetchtimeout property.
     */
    public static final String ATTRIBUTE_FETCHTIMEOUT = "fetchtimeout";

    /**
     * Defines when the interpreter context should retrieve content from the
     * server. prefetch indicates a file may be downloaded when the page is
     * loaded, whereas safe indicates a file that should only be downloaded when
     * actually needed. This defaults to the audiofetchhint property.
     */
    public static final String ATTRIBUTE_FETCHHINT = "fetchhint";

    /**
     * Indicates that the document is willing to use content whose age is no
     * greater than the specified time in seconds. The document is not willing
     * to use stale content, unless maxstale is also provided. This defaults to
     * the audiomaxage property.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * Indicates that the document is willing to use content that has exceeded
     * its expiration time. If maxstale is assigned a value, then the document
     * is willing to accept content that has exceeded its expiration time by no
     * more than the specified number of seconds. This defaults to the
     * audiomaxstale property.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPRITEM);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHAUDIO);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NEXT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NEXTITEM);
    }

    /**
     * Construct a new goto object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Goto() {
        super(null);
    }

    /**
     * Construct a new goto object.
     * @param node The encapsulated node.
     */
    Goto(final Node node) {
        super(node);
    }

    /**
     * Get the name of the tag for the derived node.
     *
     * @return name of the tag.
     */
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * Create a new instance for the given node.
     * @param n The node to encapsulate.
     * @return The new instance.
     */
    public VoiceXmlNode newInstance(final Node n) {
        return new Goto(n);
    }

    /**
     * Retrieve the next attribute.
     * @return Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     */
    public String getNext() {
        return getAttribute(ATTRIBUTE_NEXT);
    }

    /**
     * Set the next attribute.
     * @param next Value of the next attribute.
     * @see #ATTRIBUTE_NEXT
     */
    public void setNext(final String next) {
        setAttribute(ATTRIBUTE_NEXT, next);
    }

    /**
     * Retrieve the expr attribute.
     * @return Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public String getExpr() {
        return getAttribute(ATTRIBUTE_EXPR);
    }

    /**
     * Set the expr attribute.
     * @param expr Value of the expr attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public void setExpr(final String expr) {
        setAttribute(ATTRIBUTE_EXPR, expr);
    }

    /**
     * Retrieve the nextitem attribute.
     * @return Value of the nextitem attribute.
     * @see #ATTRIBUTE_NEXTITEM
     */
    public String getNextitem() {
        return getAttribute(ATTRIBUTE_NEXTITEM);
    }

    /**
     * Set the nextitem attribute.
     * @param nextitem Value of the nextitem attribute.
     * @see #ATTRIBUTE_NEXTITEM
     */
    public void setNextitem(final String nextitem) {
        setAttribute(ATTRIBUTE_NEXTITEM, nextitem);
    }

    /**
     * Retrieve the expritem attribute.
     * @return Value of the expritem attribute.
     * @see #ATTRIBUTE_EXPRITEM
     */
    public String getExpritem() {
        return getAttribute(ATTRIBUTE_EXPRITEM);
    }

    /**
     * Set the expritem attribute.
     * @param expritem Value of the expritem attribute.
     * @see #ATTRIBUTE_EXPRITEM
     */
    public void setExpritem(final String expritem) {
        setAttribute(ATTRIBUTE_EXPRITEM, expritem);
    }

    /**
     * Retrieve the fetchaudio attribute.
     * @return Value of the fetchaudio attribute.
     * @see #ATTRIBUTE_FETCHAUDIO
     */
    public String getFetchaudio() {
        return getAttribute(ATTRIBUTE_FETCHAUDIO);
    }

    /**
     * Set the fetchaudio attribute.
     * @param fetchaudio Value of the fetchaudio attribute.
     * @see #ATTRIBUTE_FETCHAUDIO
     */
    public void setFetchaudio(final String fetchaudio) {
        setAttribute(ATTRIBUTE_FETCHAUDIO, fetchaudio);
    }

    /**
     * Retrieve the fetchhint attribute.
     * @return Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public String getFetchhint() {
        return getAttribute(ATTRIBUTE_FETCHHINT);
    }

    /**
     * Set the fetchhint attribute.
     * @param fetchhint Value of the fetchhint attribute.
     * @see #ATTRIBUTE_FETCHHINT
     */
    public void setFetchhint(final String fetchhint) {
        setAttribute(ATTRIBUTE_FETCHHINT, fetchhint);
    }

    /**
     * Retrieve the fetchtimeout attribute.
     * @return Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public String getFetchtimeout() {
        return getAttribute(ATTRIBUTE_FETCHTIMEOUT);
    }

    /**
     * Set the fetchtimeout attribute.
     * @param fetchtimeout Value of the fetchtimeout attribute.
     * @see #ATTRIBUTE_FETCHTIMEOUT
     */
    public void setFetchtimeout(final String fetchtimeout) {
        setAttribute(ATTRIBUTE_FETCHTIMEOUT, fetchtimeout);
    }

    /**
     * Retrieve the maxage attribute.
     * @return Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public String getMaxage() {
        return getAttribute(ATTRIBUTE_MAXAGE);
    }

    /**
     * Set the maxage attribute.
     * @param maxage Value of the maxage attribute.
     * @see #ATTRIBUTE_MAXAGE
     */
    public void setMaxage(final String maxage) {
        setAttribute(ATTRIBUTE_MAXAGE, maxage);
    }

    /**
     * Retrieve the maxstale attribute.
     * @return Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public String getMaxstale() {
        return getAttribute(ATTRIBUTE_MAXSTALE);
    }

    /**
     * Set the maxstale attribute.
     * @param maxstale Value of the maxstale attribute.
     * @see #ATTRIBUTE_MAXSTALE
     */
    public void setMaxstale(final String maxstale) {
        setAttribute(ATTRIBUTE_MAXSTALE, maxstale);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return false;
    }

    /**
     * Returns a collection of permitted attribute names for the node.
     *
     * @return A collection of attribute names that are allowed for the node
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
