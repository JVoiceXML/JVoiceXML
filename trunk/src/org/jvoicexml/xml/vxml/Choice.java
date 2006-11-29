/*
 * File:    $RCSfile: Choice.java,v $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.srgs.Grammar;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Define a menu item. The <code>&lt;choice&gt;</code> element serves
 * several purposes:
 * <ul>
 * <li>
 * It may specify a speech grammar, defined either using a
 * <code>&lt;grammar&gt;</code> element or automatically generated by the
 * process described in Section 2.2.5 of
 * <a href="http://www.w3.org/TR/2005/REC-voicexml20-20050316">
 * http://www.w3.org/TR/2005/REC-voicexml20-20050316</a>.
 * </li>
 * <li>
 * It may specify a DTMF grammar, as discussed in Section 2.2.3 of
 * <a href="http://www.w3.org/TR/2005/REC-voicexml20-20050316">
 * http://www.w3.org/TR/2005/REC-voicexml20-20050316</a>
 * </li>
 * <li>
 * The contents may be used to form the <code>&lt;enumerate&gt;</code> prompt
 * string. This is described in Section 2.2.4 of
 * <a href="http://www.w3.org/TR/2005/REC-voicexml20-20050316">
 * http://www.w3.org/TR/2005/REC-voicexml20-20050316</a>
 * </li>
 * <li>
 * And it specifies either an event to be thrown or the URI to go to when the
 * choice is selected.
 * </li>
 * </ul>
 *
 * @see org.jvoicexml.xml.vxml.Enumerate
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.xml.srgs.Grammar
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Choice
        extends AbstractVoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "choice";

    /**
     * The DTMF sequence for this choice. It is equivalent to a simple DTMF
     * <code>&lt;grammar&gt;</code> and DTMF properties apply to recognition
     * of the sequence. Unlike DTMF grammars, whitespace is optional:
     * dtmf="123#" is equivalent to dtmf="1 2 3 #".
     */
    public static final String ATTRIBUTE_DTMF = "dtmf";

    /**
     * Override the setting for accept in <code>&lt;menu&gt;</code> for this
     * particular choice. When set to "exact" (the default), the text of the
     * choice element defines the exact phrase to be recognized. When set to
     * "approximate", the text of the choice element defines an approximate
     * recognition phrase.
     */
    public static final String ATTRIBUTE_ACCEPT = "accept";

    /**
     * The URI of next dialog or document.
     */
    public static final String ATTRIBUTE_NEXT = "next";

    /**
     * Specify an expression to evaluate as a URI to transition to instead of
     * specifying a next.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

    /**
     * Specify an event to be thrown instead of specifying a next.
     */
    public static final String ATTRIBUTE_EVENT = "event";

    /**
     * An ECMAScript expression evaluating to the name of the event to be
     * thrown.
     */
    public static final String ATTRIBUTE_EVENTEXPR = "eventexpr";

    /**
     * A message string providing additional context about the event being
     * thrown.
     */
    public static final String ATTRIBUTE_MESSAGE = "message";

    /**
     * An ECMAScript expression evaluating to the message string.
     */
    public static final String ATTRIBUTE_MESSAGEEXPR = "messageexpr";

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

        ATTRIBUTE_NAMES.add(ATTRIBUTE_DTMF);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ACCEPT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NEXT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EVENT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_EVENTEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MESSAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MESSAGEEXPR);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHAUDIO);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
    }

    /**
     * Valid child tags for this node.
     */
    private static final Set<String> CHILD_TAGS;

    /**
     * Set the valid child tags for this node.
     */
    static {
        CHILD_TAGS = new java.util.HashSet<String>();

        CHILD_TAGS.add(Grammar.TAG_NAME);
    }

    /**
     * Construct a new choice object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Choice() {
        super(null);
    }

    /**
     * Construct a new choice object.
     * @param node The encapsulated node.
     */
    Choice(final Node node) {
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
        return new Choice(n);
    }

    /**
     * Retrieve the dtmf attribute.
     * @return Value of the dtmf attribute.
     * @see #ATTRIBUTE_DTMF
     */
    public String getDtmf() {
        return getAttribute(ATTRIBUTE_DTMF);
    }

    /**
     * Set the dtmf attribute.
     * @param dtmf Value of the dtmf attribute.
     * @see #ATTRIBUTE_DTMF
     */
    public void setDtmf(final String dtmf) {
        setAttribute(ATTRIBUTE_DTMF, dtmf);
    }

    /**
     * Retrieve the accept attribute.
     * @return Value of the accept attribute.
     * @see #ATTRIBUTE_ACCEPT
     */
    public String getAccept() {
        final String accept = getAttribute(ATTRIBUTE_ACCEPT);
        if (accept != null) {
            return accept;
        }
        return new String("exact");
    }

    /**
     * Set the accept attribute.
     * @param accept Value of the accept attribute.
     * @see #ATTRIBUTE_ACCEPT
     */
    public void setAccept(final String accept) {
        setAttribute(ATTRIBUTE_ACCEPT, accept);
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
     * Retrieve the event attribute.
     * @return Value of the event attribute.
     * @see #ATTRIBUTE_EVENT
     */
    public String getEvent() {
        return getAttribute(ATTRIBUTE_EVENT);
    }

    /**
     * Set the event attribute.
     * @param event Value of the event attribute.
     * @see #ATTRIBUTE_EVENT
     */
    public void setEvent(final String event) {
        setAttribute(ATTRIBUTE_EVENT, event);
    }

    /**
     * Retrieve the eventexpr attribute.
     * @return Value of the eventexpr attribute.
     * @see #ATTRIBUTE_EVENTEXPR
     */
    public String getEventexpr() {
        return getAttribute(ATTRIBUTE_EVENTEXPR);
    }

    /**
     * Set the eventexpr attribute.
     * @param eventexpr Value of the eventexpr attribute.
     * @see #ATTRIBUTE_EVENTEXPR
     */
    public void setEventexpr(final String eventexpr) {
        setAttribute(ATTRIBUTE_EVENTEXPR, eventexpr);
    }

    /**
     * Retrieve the message attribute.
     * @return Value of the message attribute.
     * @see #ATTRIBUTE_MESSAGE
     */
    public String getMessage() {
        return getAttribute(ATTRIBUTE_MESSAGE);
    }

    /**
     * Set the message attribute.
     * @param message Value of the message attribute.
     * @see #ATTRIBUTE_MESSAGE
     */
    public void setMessage(final String message) {
        setAttribute(ATTRIBUTE_MESSAGE, message);
    }

    /**
     * Retrieve the messageexpr attribute.
     * @return Value of the messageexpr attribute.
     * @see #ATTRIBUTE_MESSAGEEXPR
     */
    public String getMessageexpr() {
        return getAttribute(ATTRIBUTE_MESSAGEEXPR);
    }

    /**
     * Set the messageexpr attribute.
     * @param messageexpr Value of the messageexpr attribute.
     * @see #ATTRIBUTE_MESSAGEEXPR
     */
    public void setMessageexpr(final String messageexpr) {
        setAttribute(ATTRIBUTE_MESSAGEEXPR, messageexpr);
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
     * Create a new text within this node.
     * @param text The text to be added.
     * @return The new created text.
     */
    public Text addText(final String text) {
        final Document document = getOwnerDocument();
        final Node node = document.createTextNode(text);
        final Text textNode = new Text(node, getNodeFactory());
        appendChild(textNode);
        return textNode;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
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
