/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Emphasis;
import org.jvoicexml.xml.ssml.Lexicon;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.P;
import org.jvoicexml.xml.ssml.Phoneme;
import org.jvoicexml.xml.ssml.Prosody;
import org.jvoicexml.xml.ssml.S;
import org.jvoicexml.xml.ssml.SayAs;
import org.jvoicexml.xml.ssml.Sub;
import org.jvoicexml.xml.ssml.Voice;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A <code>&lt;prompt&gt;</code> element controls the output of synthesized
 * speech and prerecorded audio. Conceptually, prompts are instaneously
 * queued for play, so interpretation proceeds until the user needs to
 * provide an input. At this point, the prompts are played, and the system
 * waits for user input. Once the input is received from the speech
 * recognition subsystem (or the DTMF recognizer), interpretation
 * proceeds.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Prompt
        extends AbstractVoiceXmlNode {
    /** Name of the prompt tag. */
    public static final String TAG_NAME = "prompt";

    /**
     * Control whether the user can interrupt a prompt. This defaults to the
     * value of the bargein property.
     */
    public static final String ATTRIBUTE_BARGEIN = "bargein";

    /**
     * Sets the type of bargein to be <code>BARGEIN_TYPE_SPEECH</code>
     * or <code>BARGEIN_TYPE_HOTWORD</code>. This defaults to the value of
     * the bargein property.
     *
     * @see org.jvoicexml.xml.vxml.BargeInType#SPEECH
     * @see org.jvoicexml.xml.vxml.BargeInType#HOTWORD
     */
    public static final String ATTRIBUTE_BARGEINTYPE = "bargeintype";

    /**
     * An expression that must evaluate to <code>true</code> after conversion
     * to boolean in order for the prompt to be played. Default is
     * <code>true</code>.
     */
    public static final String ATTRIBUTE_COND = "cond";

    /**
     * A number that allows you to emit different prompts if the user is
     * doing something repeatedly. If omitted, it defaults to <code>1</code>.
     */
    public static final String ATTRIBUTE_COUNT = "count";

    /**
     * The timeout that will be used for the following user input. The
     * default is a Time Designation. The default noinput timeout is
     * platform specific.
     */
    public static final String ATTRIBUTE_TIMEOUT = "timeout";

    /**
     * The laanguage identifier for the prompt. If omitted, it defaults to
     * the value specified in the document's <code>xml:lang</code>
     * attribute.
     */
    public static final String ATTRIBUTE_XML_LANG = "xml:lang";

    /**
     * Declares the base URI from which relative URIs in the prompt are
     * resloved. This base declaration has precedence over the
     * <code>&lt;vxml&gt;</code> base URI declaration. If a local decalration
     * is omitted, the value is inherited down the document hierarchie.
     */
    public static final String ATTRIBUTE_XML_BASE = "xml:base";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_BARGEIN);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_BARGEINTYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_COND);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_COUNT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TIMEOUT);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_BASE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_XML_LANG);
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
        CHILD_TAGS.add(Foreach.TAG_NAME);
        CHILD_TAGS.add(Mark.TAG_NAME);
        CHILD_TAGS.add(Phoneme.TAG_NAME);
        CHILD_TAGS.add(Prosody.TAG_NAME);
        CHILD_TAGS.add(SayAs.TAG_NAME);
        CHILD_TAGS.add(Voice.TAG_NAME);
        CHILD_TAGS.add(Sub.TAG_NAME);
        CHILD_TAGS.add(P.TAG_NAME);
        CHILD_TAGS.add(S.TAG_NAME);
        CHILD_TAGS.add(Lexicon.TAG_NAME);
        CHILD_TAGS.add(Meta.TAG_NAME);
        CHILD_TAGS.add(Metadata.TAG_NAME);
    }

    /**
     * Construct a new prompt object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Prompt() {
        super(null);
    }

    /**
     * Construct a new prompt object.
     * @param node The encapsulated node.
     */
    Prompt(final Node node) {
        super(node);
    }

    /**
     * Retrieve the bargein attribute.
     * @return Value of the bargein attribute.
     * @see #ATTRIBUTE_BARGEIN
     */
    public String getBargein() {
        final String bargein = getAttribute(ATTRIBUTE_BARGEIN);
        if (bargein != null) {
            return bargein;
        }

        return Boolean.toString(true);
    }

    /**
     * Set the bargein attribute.
     * @param bargein Value of the bargein attribute.
     * @see #ATTRIBUTE_BARGEIN
     */
    public void setBargein(final String bargein) {
        setAttribute(ATTRIBUTE_BARGEIN, bargein);
    }

    /**
     * Checks if barge-in is enabled.
     * @return <code>true</code> if barge-in is enabled.
     *
     * @since 0.5
     */
    public boolean isBargein() {
        final String bargein = getBargein();

        return Boolean.valueOf(bargein);
    }

    /**
     * Set the bargein attribute.
     * @param bargein Value of the bargein attribute.
     * @see #ATTRIBUTE_BARGEIN
     *
     * @since 0.5
     */
    public void setBargein(final boolean bargein) {
        final String enable = Boolean.toString(bargein);

        setBargein(enable);
    }

    /**
     * Retrieve the bargeintype attribute.
     * @return Value of the bargeintype attribute.
     * @see #ATTRIBUTE_BARGEINTYPE
     */
    public String getBargeintypeName() {
        final BargeInType type = getBargeintype();

        return type.getType();
    }

    /**
     * Set the bargeintype attribute.
     * @param bargeintype Value of the bargein attribute.
     * @see #ATTRIBUTE_BARGEIN
     */
    public void setBargeintype(final String bargeintype) {
        BargeInType type = BargeInType.valueOf(bargeintype);
        setBargeintype(type);
    }

    /**
     * Retrieve the bargeintype attribute.
     * @return Value of the bargeintype attribute.
     * @see #ATTRIBUTE_BARGEINTYPE
     *
     * @since 0.5
     */
    public BargeInType getBargeintype() {
        final String type = getAttribute(ATTRIBUTE_BARGEINTYPE);

        return BargeInType.valueOf(type);
    }

    /**
     * Set the bargeintype attribute.
     * @param bargeintype Value of the bargein attribute.
     * @see #ATTRIBUTE_BARGEIN
     * @since 0.5
     */
    public void setBargeintype(final BargeInType bargeintype) {
        setAttribute(ATTRIBUTE_BARGEINTYPE, bargeintype.getType());
    }

    /**
     * Retrieve the cond attribute.
     * @return Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public String getCond() {
        final String cond = getAttribute(ATTRIBUTE_COND);
        if (cond == null) {
            return Boolean.toString(true);
        }

        return cond;
    }

    /**
     * Set the cond attribute.
     * @param cond Value of the cond attribute.
     * @see #ATTRIBUTE_COND
     */
    public void setCond(final String cond) {
        setAttribute(ATTRIBUTE_COND, cond);
    }

    /**
     * Retrieve the count attribute.
     * @return Value of the count attribute.
     * @see #ATTRIBUTE_COUNT
     */
    public String getCount() {
        final String count = getAttribute(ATTRIBUTE_COUNT);
        if (count != null) {
            return count;
        }

        return Integer.toString(1);
    }

    /**
     * Set the count attribute.
     * @param count Value of the count attribute.
     * @see #ATTRIBUTE_COUNT
     */
    public void setCount(final String count) {
        setAttribute(ATTRIBUTE_COUNT, count);
    }

    /**
     * Retrieve the count attribute.
     * @return Value of the count attribute.
     * @see #ATTRIBUTE_COUNT
     */
    public int getCountAsInt() {
        final String count = getCount();

        return Integer.parseInt(count);
    }

    /**
     * Set the count attribute.
     * @param count Value of the count attribute.
     * @see #ATTRIBUTE_COUNT
     */
    public void setCount(final int count) {
        setCount(Integer.toString(count));
    }

    /**
     * Retrieve the timeout attribute.
     * @return Value of the timeout attribute.
     * @see #ATTRIBUTE_TIMEOUT
     */
    public String getTimeout() {
        return getAttribute(ATTRIBUTE_TIMEOUT);
    }

    /**
     * Set the timeout attribute.
     * @param timeout Value of the count attribute.
     * @see #ATTRIBUTE_TIMEOUT
     */
    public void setTimeout(final String timeout) {
        setAttribute(ATTRIBUTE_TIMEOUT, timeout);
    }

    /**
     * Retrieve the xml:lang attribute.
     * @return Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public String getXmlLang() {
        return getAttribute(ATTRIBUTE_XML_LANG);
    }

    /**
     * Set the xml:lang attribute.
     * @param xmlLang Value of the xml:lang attribute.
     * @see #ATTRIBUTE_XML_LANG
     */
    public void setXmlLang(final String xmlLang) {
        setAttribute(ATTRIBUTE_XML_LANG, xmlLang);
    }

    /**
     * Retrieve the xml:base attribute.
     * @return Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public String getXmlBase() {
        return getAttribute(ATTRIBUTE_XML_BASE);
    }

    /**
     * Set the xml:base attribute.
     * @param xmlBase Value of the xml:base attribute.
     * @see #ATTRIBUTE_XML_BASE
     */
    public void setXmlBase(final String xmlBase) {
        setAttribute(ATTRIBUTE_XML_BASE, xmlBase);
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
     * Create a new text within this block.
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
     * Create a new instance for the given node.
     *
     * @param n The node to encapsulate.
     * @return The new instance.
     */
    public VoiceXmlNode newInstance(final Node n) {
        return new Prompt(n);
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
