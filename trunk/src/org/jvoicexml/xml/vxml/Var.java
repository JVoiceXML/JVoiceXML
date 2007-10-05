/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright &copy; 2005-2007 JVoiceXML group
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

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * This element declares a variable. It can occur in executable content or as a
 * child of <code>&lt;form&gt;</code> or <code>&lt;vxml&gt;</code>.
 *
 * <p>
 * <code>
 * &lt;var name="phone" expr="'6305551212'"/&gt; <br>
 * &lt;var name="y" expr="document.z+1"/&gt;
 * </code>
 * </p>
 *
 * <p>
 * If it occurs in executable content, it declares a variable in the anonymous
 * scope associated with the enclosing <code>&lt;block&gt;</code>,
 * <code>&lt;filled&gt</code>; or catch element. This declaration is made
 * only when the &lt;var&gt; element is executed. If the variable is already
 * declared in this scope, subsequent declarations act as assignments, as in
 * ECMAScript.
 * </p>
 *
 * <p>
 * If a <code>&lt;var&gt;</code> is a child of a <code>&lt;form&gt;</code>
 * element, it declares a variable in the dialog scope of the
 * <code>&lt;form&gt;</code>. This declaration is made during the form's
 * initialization phase. The <code>&lt;var&gt;</code> element is not a form
 * item, and so is not visited by the Form Interpretation Algorithm's main loop.
 * </p>
 *
 * <p>
 * If a <code>&lt;var&gt;</code> is a child of a <code>&lt;vxml&gt;</code>
 * element, it declares a variable in the document scope; and if it is the child
 * of a <code>&lt;vxml&gt;</code> element in a root document then it also
 * declares the variable in the application scope. This declaration is made when
 * the document is initialized; initializations happen in document order.
 * </p>
 *
 *
 * @author Steve Doyle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Var
        extends AbstractVoiceXmlNode {

    /** Name of the var tag. */
    public static final String TAG_NAME = "var";

    /**
     * The name of the variable that will hold the result.
     */
    public static final String ATTRIBUTE_NAME = "name";

    /**
     * The initial value of the variable (optional). If there is no
     * expr attribute, the variable retains its current value if any.
     * Variables start out with the ECMAScript value undefined if they
     * are not given initial values.
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
        ATTRIBUTE_NAMES.add(ATTRIBUTE_NAME);
    }

    /**
     * Construct a new var object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Var() {
        super(null);
    }

    /**
     * Construct a new var object.
     * @param node The encapsulated node.
     */
    Var(final Node node) {
        super(node);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private Var(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Retrieve the name attribute.
     * @return Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public String getName() {
        return getAttribute(ATTRIBUTE_NAME);
    }

    /**
     * Set the name attribute.
     * @param name Value of the name attribute.
     * @see #ATTRIBUTE_NAME
     */
    public void setName(final String name) {
        setAttribute(ATTRIBUTE_NAME, name);
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
     * @param expr Value of the id attribute.
     * @see #ATTRIBUTE_EXPR
     */
    public void setExpr(final String expr) {
        setAttribute(ATTRIBUTE_EXPR, expr);
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
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new Var(n, factory);
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
