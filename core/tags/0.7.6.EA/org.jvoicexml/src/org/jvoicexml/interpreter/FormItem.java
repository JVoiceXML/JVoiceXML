/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
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
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.4
 */
public interface FormItem
        extends FormItemVisitable, DialogConstruct {
    /**
     * Initializes this form item.
     * @param scripting current scripting engine
     * @throws SemanticError
     *         error initializing this form item
     * @throws BadFetchError
     *         error initializing this form item
     * @since 0.7.3
     */
    void init(final ScriptingEngine scripting)
        throws SemanticError, BadFetchError;

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
     * Sets the value of the form item variable.
     *
     * @param value
     *        New value for the form item variable.
     * @exception SemanticError
     *        error setting the value
     */
    void setFormItemVariable(final Object value) throws SemanticError;

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
     * @exception SemanticError
     *            error evaluating the cond condition.
     * @see #getFormItemVariable()
     * @see org.mozilla.javascript.Context#getUndefinedValue()
     */
    boolean isSelectable() throws SemanticError;

    /**
     * Retrieves the encapsulated <code>VoiceXmlNode</code>.
     *
     * @return Related <code>VoiceXmlNode</code>.
     */
    VoiceXmlNode getNode();

    /**
     * Retrieves the tag name of the encapsulated node.
     * @return tag name of the encapsulated node.
     * @since 0.7
     */
    String getNodeTagName();

    /**
     * Retrieves the evaluated <code>expr</code> attribute.
     * @param scripting the scripting engine to use for evaluation
     *
     * @return evaluated expression of the <code>expr</code> attribute.
     * @exception SemanticError
     *            error evaluating the <code>expr</code> attribute.
     */
    Object evaluateExpression(final ScriptingEngine scripting)
            throws SemanticError;

    /**
     * An expression to evaluate in conjunction with the test of the form item
     * variable. If absent, this defaults to <code>true</code>, or in the case
     * of <code>&lt;initial&gt;</code>, a test to see if any input item
     * variable has been filled in.
     * @return <code>true</code> if the <code>cond</code> attribute of the
     * form item evaluates to <code>true</code>.
     * @exception SemanticError
     *            error evaluating the cond attribute.
     */
    boolean getCondition() throws SemanticError;

    /**
     * Checks if this form item is modal. This causes all grammars to be
     * disabled except the ones defined in the current form item.
     * @return <code>true</code> if the form item is modal.
     * @since 0.7.2
     */
    boolean isModal();
}
