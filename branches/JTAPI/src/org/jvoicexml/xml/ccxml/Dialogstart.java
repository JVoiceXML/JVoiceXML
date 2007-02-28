/*
 * File:    $RCSfile: Dialogstart.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 * State: $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.xml.ccxml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jvoicexml.xml.XmlNode;
import org.w3c.dom.Node;

/**
 * <code>&lt;dialogstart&gt;</code> is used to start a dialog and associate
 * the dialog with a connection or conference. (See Section 10 for a discussion
 * of connections and bridges). The element includes either a URI reference to
 * the initial document for the dialog or the identity of a previously prepared
 * dialog. The dialog executes on a separate logical execution thread (this MAY
 * be a thread, process, or system depending upon platform implementation) and
 * does not block the processing of further events by the CCXML session.
 *
 * If the dialog cannot be started for any reason, an error.dialog.notstarted
 * event is posted to the event queue of the CCXML session that processed the
 * <code>&lt;dialogstart&gt;</code> request. When the dialog completes, a
 * dialog.exit event is posted to the event queue of the CCXML session that
 * started it.
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Dialogstart
        extends AbstractCcxmlNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "dialogstart";

    /**
     * An Optional ECMAScript expression which returns the identifier of`a
     * confe2ence bridge. A connection will be allocated for the dialog being
     * prepared. If the attribute value is invalid, an error.semantic event will
     * be thrown.
     */
    public static final String ATTRIBUTE_CONFERENCEID = "conferenceid";

    /**
     * An Optional ECMAScript expression which returns the identifier of a
     * connection. The specified connection will be associated with the dialog
     * being prepared. If the attribute value is invalid, an error.semantic
     * event will be thrown.
     */
    public static final String ATTRIBUTE_CONNECTIONID = "connectionid";

    /**
     * An ECMAScript left hand side expression evaluating to a previously
     * defined variable. The value of the attribute will receive a dialog
     * identifier value for the launched dialog interpreter instance. This
     * identifier may be used on future invocations of dialogstart,
     * dialogterminate, join, or unjoin.
     */
    public static final String ATTRIBUTE_DIALOGID = "dialogid";

    /**
     * An ECMAScript expression which returns a character string that indicates
     * the HTTP method to use.
     */
    public static final String ATTRIBUTE_ENCTYPE = "enctype";

    /**
     * The character string returned is interpreted as a time interval. This
     * indicates that the document is willing to use content whose age is no
     * greater than the specified time in seconds (cf. 'max-age' in HTTP 1.1
     * [RFC2616]). The document is not willing to use stale content, unless
     * maxstale is also provided.
     */
    public static final String ATTRIBUTE_MAXAGE = "maxage";

    /**
     * The character string returned is interpreted as a time interval. This
     * indicates that the document is willing to use content that has exceeded
     * its expiration time (cf. 'max-age' in HTTP 1.1 [RFC2616]). If maxstale is
     * assigned a value, then the document is willing to accept content that has
     * exceeded its expiration time by no more than the specified number of
     * seconds.
     */
    public static final String ATTRIBUTE_MAXSTALE = "maxstale";

    /**
     * An ECMAScript expression that defines the direction of the media flow
     * between the Dialog and the Connection or Conference specified by the
     * connectionid or conferenceid attribute.
     */
    public static final String ATTRIBUTE_MEDIADIRECTION = "mediadirection";

    /**
     * An ECMAScript expression which returns a character string that indicates
     * the HTTP method to use.
     */
    public static final String ATTRIBUTE_METHOD = "method";

    /**
     * A list of one or more whitespace separated CCXML variable names. These
     * variables will be submitted to the server, with the same qualification as
     * used in the namelist. When an ECMAscript variable is submitted to the
     * server, its value is first converted into a string before being
     * submitted. If the variable is an ECMAScript Object, the mechanism by
     * which it is submitted is not currently defined. Instead of submitting
     * ECMAScript Objects directly, the application developer may explicitly
     * submit the properties of an Object. e.g. "date.month date.year".
     */
    public static final String ATTRIBUTE_NAMELIST = "namelist";

    /**
     * An ECMAScript expression which returns a character string identifying the
     * URI of the dialog document that the dialog interpreter should prepare.
     */
    public static final String ATTRIBUTE_SRC = "src";

    /**
     * An ECMAScript expression which returns a character string that specifies
     * the MIME type of the document, and as a result determines which dialog
     * manager environment is actually used.
     */
    public static final String ATTRIBUTE_TYPE = "type";

    /**
     * An ECMAScript expression which returns a dialog identifier of a dialog
     * previously prepared by the execution of a dialogprepare element. If the
     * specified dialog identifier refers to an unknown dialog or a dialog that
     * has already been executed, by way of another dialogstart element, an
     * error.dialogwrongstate event is thrown.
     */
    public static final String ATTRIBUTE_PREPAREDDIALOGID = "prepareddialogid";

    /**
     * Supported attribute names for this node.
     */
    protected static final ArrayList<String> ATTRIBUTE_NAMES;

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES = new java.util.ArrayList<String>();

        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONFERENCEID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_CONNECTIONID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_DIALOGID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_ENCTYPE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXAGE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MAXSTALE);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_MEDIADIRECTION);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_METHOD);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAMELIST);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_PREPAREDDIALOGID);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_SRC);
        ATTRIBUTE_NAMES.add(ATTRIBUTE_TYPE);
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

    }

    /**
     * Construct a new dialogstart object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Dialogstart() {
        super(null);
    }

    /**
     * Construct a new dialogstart object.
     * @param node The encapsulated node.
     */
    Dialogstart(final Node node) {
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
    public XmlNode newInstance(final Node n) {
        return new Dialogstart(n);
    }

    /**
     * Retrieve the conferenceid attribute.
     * @return Value of the conferenceid attribute.
     * @see #ATTRIBUTE_CONFERENCEID
     */
    public String getConferenceid() {
        return getAttribute(ATTRIBUTE_CONFERENCEID);
    }

    /**
     * Set the conferenceid attribute.
     * @param conferenceid Value of the conferenceid attribute.
     * @see #ATTRIBUTE_CONFERENCEID
     */
    public void setConferenceid(final String conferenceid) {
        setAttribute(ATTRIBUTE_CONFERENCEID, conferenceid);
    }

    /**
     * Retrieve the connectionid attribute.
     * @return Value of the connectionid attribute.
     * @see #ATTRIBUTE_CONNECTIONID
     */
    public String getConnectionid() {
        return getAttribute(ATTRIBUTE_CONNECTIONID);
    }

    /**
     * Set the connectionid attribute.
     * @param connectionid Value of the connectionid attribute.
     * @see #ATTRIBUTE_CONNECTIONID
     */
    public void setConnectionid(final String connectionid) {
        setAttribute(ATTRIBUTE_CONNECTIONID, connectionid);
    }

    /**
     * Retrieve the dialogid attribute.
     * @return Value of the dialogid attribute.
     * @see #ATTRIBUTE_DIALOGID
     */
    public String getDialogid() {
        return getAttribute(ATTRIBUTE_DIALOGID);
    }

    /**
     * Set the dialogid attribute.
     * @param dialogid Value of the dialogid attribute.
     * @see #ATTRIBUTE_DIALOGID
     */
    public void setDialogid(final String dialogid) {
        setAttribute(ATTRIBUTE_DIALOGID, dialogid);
    }

    /**
     * Retrieve the enctype attribute.
     * @return Value of the enctype attribute.
     * @see #ATTRIBUTE_ENCTYPE
     */
    public String getEnctype() {
        return getAttribute(ATTRIBUTE_ENCTYPE);
    }

    /**
     * Set the enctype attribute.
     * @param enctype Value of the enctype attribute.
     * @see #ATTRIBUTE_ENCTYPE
     */
    public void setEnctype(final String enctype) {
        setAttribute(ATTRIBUTE_ENCTYPE, enctype);
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
     * Retrieve the mediadirection attribute.
     * @return Value of the mediadirection attribute.
     * @see #ATTRIBUTE_MEDIADIRECTION
     */
    public String getMediadirection() {
        return getAttribute(ATTRIBUTE_MEDIADIRECTION);
    }

    /**
     * Set the mediadirection attribute.
     * @param mediadirection Value of the mediadirection attribute.
     * @see #ATTRIBUTE_MEDIADIRECTION
     */
    public void setMediadirection(final String mediadirection) {
        setAttribute(ATTRIBUTE_MEDIADIRECTION, mediadirection);
    }

    /**
     * Retrieve the method attribute.
     * @return Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     */
    public String getMethod() {
        return getAttribute(ATTRIBUTE_METHOD);
    }

    /**
     * Set the method attribute.
     * @param method Value of the method attribute.
     * @see #ATTRIBUTE_METHOD
     */
    public void setMethod(final String method) {
        setAttribute(ATTRIBUTE_METHOD, method);
    }

    /**
     * Retrieve the namelist attribute.
     * @return Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public String getNamelist() {
        return getAttribute(ATTRIBUTE_NAMELIST);
    }

    /**
     * Set the namelist attribute.
     * @param namelist Value of the namelist attribute.
     * @see #ATTRIBUTE_NAMELIST
     */
    public void setNamelist(final String namelist) {
        setAttribute(ATTRIBUTE_NAMELIST, namelist);
    }

    /**
     * Retrieve the prepareddialogid attribute.
     * @return Value of the prepareddialogid attribute.
     * @see #ATTRIBUTE_PREPAREDDIALOGID
     */
    public String getPrepareddialogid() {
        return getAttribute(ATTRIBUTE_PREPAREDDIALOGID);
    }

    /**
     * Set the prepareddialogid attribute.
     * @param prepareddialogid Value of the prepareddialogid attribute.
     * @see #ATTRIBUTE_PREPAREDDIALOGID
     */
    public void setPrepareddialogid(final String prepareddialogid) {
        setAttribute(ATTRIBUTE_PREPAREDDIALOGID, prepareddialogid);
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
     * Retrieve the type attribute.
     * @return Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public String getType() {
        return getAttribute(ATTRIBUTE_TYPE);
    }

    /**
     * Set the type attribute.
     * @param type Value of the type attribute.
     * @see #ATTRIBUTE_TYPE
     */
    public void setType(final String type) {
        setAttribute(ATTRIBUTE_TYPE, type);
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
