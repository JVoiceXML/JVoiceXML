/*
 * File:    $RCSfile: Audio.java,v $
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

package org.jvoicexml.xml.ssml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.vxml.Enumerate;
import org.jvoicexml.xml.vxml.Value;
import org.w3c.dom.Node;

/**
 * Audio can be played in any prompt. The audio content can be specified via a
 * URI, and in VoiceXML it can also be in an audio variable previously recorded.
 *
 * <p>
 * <code>
 * &lt;prompt&gt; <br>
 * Your recorded greeting is <br>
 * &lt;audio expr="greeting"/&gt;
 * To rerecord, press 1.<br>
 * To keep it, press pound. <br>
 * To return to the main menu press star M.<br>
 * To exit press star, star X. <br>
 * &lt;/prompt&gt;
 * </code>
 * </p>
 *
 * <p>
 * The <code>&lt;audio&gt;</code> element can have alternate content in case
 * the audio sample is not available:
 * </p>
 *
 * <p>
 * <code>
 * &lt;prompt&gt; <br>
 * &lt;audio src="welcome.wav"&gt;<br>
 * &lt;emphasis&gt;Welcome &lt;/emphasis&gt; to the Voice Portal. <br>
 * &lt;/audio&gt; <br>
 * &lt;/prompt&gt;
 * </code>
 * </p>
 *
 * <p>
 * If the audio file cannot be played (e.g. 'src' referencing or 'expr'
 * evaluating to an invalid URI, a file with an unsupported format, etc), the
 * content of the audio element is played instead. The content may include text,
 * speech markup, or another audio element. If the audio file cannot be played
 * and the content of the audio element is empty, no audio is played and no
 * error event is thrown.
 * </p>
 *
 * <p>
 * If <code>&lt;audio&gt;</code> contains an 'expr' attribute evaluating to
 * ECMAScript undefined, then the element, including its alternate content, is
 * ignored. This allows a developer to specify <code>&lt;audio&gt;</code>
 * elements with dynamically assigned content which, if the element is not
 * required, can be ignored by assigning its 'expr' a null value.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.xml.vxml.Prompt
 *
 * @author Steve Doyle
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Audio
        extends AbstractSsmlNode implements VoiceXmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "audio";

    /**
     * The URI of the audio prompt.
     */
    public static final String ATTRIBUTE_SRC = "src";

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
     * An ECMAScript expression which determines the source of the audio to be
     * played. The expression may be either a reference to audio previously
     * recorded with the <code>&lt;record/&gt;</code> item or evaluate to the
     * URI of an audio resource to fetch.
     */
    public static final String ATTRIBUTE_EXPR = "expr";

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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHHINT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_FETCHTIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRC);
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

        CHILD_TAGS.add(Audio.TAG_NAME);
        CHILD_TAGS.add(Enumerate.TAG_NAME);
        CHILD_TAGS.add(Value.TAG_NAME);
        CHILD_TAGS.add(Break.TAG_NAME);
        CHILD_TAGS.add(Emphasis.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(P.TAG_NAME);
        CHILD_TAGS.add(S.TAG_NAME);
    }

    /**
     * Construct a new audio object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Audio() {
        super(null);
    }

    /**
     * Construct a new audio object.
     * @param node The encapsulated node.
     */
    Audio(final Node node) {
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
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n) {
        return new Audio(n);
    }

    /**
     * Retrieve the src attribute.
     * @return Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public String getSrc() {
        return getAttribute(ATTRIBUTE_SRC);
    }

    /**
     * Set the src attribute.
     * @param src Value of the src attribute.
     * @see #ATTRIBUTE_SRC
     */
    public void setSrc(final String src) {
        setAttribute(ATTRIBUTE_SRC, src);
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
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        return CHILD_TAGS.contains(tagName);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getAttributeNames() {
        return ATTRIBUTE_NAMES;
    }
}
