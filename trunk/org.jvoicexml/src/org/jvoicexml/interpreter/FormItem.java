/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Form items are the elements that can be visited in the main loop of the form
 * interpretation algorithm. Input items direct the FIA to gather a result for a
 * specific element. When the FIA selects a control item, the control item may
 * contain a block of procedural code to execute, or it may tell the FIA to set
 * up the initial prompt-and-collect for a mixed initiative form.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.4
 */
public interface FormItem
        extends FormItemVisitable, DialogConstruct {
    /**
     * Retrieves the form item variable.
     *
     * <p>
     * Each form has an associated form item variable. which by default is set
     * to <code>ECMA_SCRIPT_UNDEFINED</code> when the form is entered. This
     * form item variable will contain the result of interpreting the form item.
     * An input item's form variable can be given a name using the name
     * attribute or left nameless in which case an internal name is generated.
     * </p>
     *
     * @see org.mozilla.javascript.Context#getUndefinedValue()
     *
     * @return Result of interpreting this form item.
     */
    Object getFormItemVariable();

    /**
     * Sets the form item variable.
     *
     * @param value
     *        New value for the form item variable.
     */
    void setFormItemVariable(final Object value);

    /**
     * Retrieves the name of this <code>FormItem</code>..
     *
     * @return Name of a dialog scoped form item variable.
     */
    String getName();

    /**
     * Guard conditions, which governs whether or not this form item can be
     * selected by the form interpretation algorithm.
     *
     * <p>
     * This default guard condition just tests to see if the form item variable
     * has a value. If it does, this form item will not be visited.
     * </p>
     *
     * @return <code>true</code> if the form item's variable has no value.
     *
     * @see #getFormItemVariable()
     * @see org.mozilla.javascript.Context#getUndefinedValue()
     */
    boolean isSelectable();

    /**
     * Retrieves the encapsualed <code>VoiceXmlNode</code>.
     *
     * @return Related <code>VoiceXmlNode</code>.
     */
    VoiceXmlNode getNode();

    /**
     * Selector for the <code>expr</code> attribute.
     *
     * @return Value of the <code>expr</code> attribute.
     * @todo replace this with a superclass or an own interface.
     */
    String getExpr();

    /**
     * Get all nested <code>&lt;catch&gt;</code> elements.
     *
     * @return Collection of all nested <code>&lt;catch&gt;</code> tags.
     */
    Collection<AbstractCatchElement> getCatchElements();
}
