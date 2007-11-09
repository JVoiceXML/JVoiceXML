/*
 * File:    $RCSfile: FormItemVisitor.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

package org.jvoicexml.interpreter;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.formitem.BlockFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.formitem.InputItem;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.interpreter.formitem.RecordFormItem;
import org.jvoicexml.interpreter.formitem.SubdialogFormItem;
import org.jvoicexml.interpreter.formitem.TransferFormItem;

/**
 * A visitor for form items. Form items are visited in the <em>collect</em>
 * phase of the form interpretation algorithm. Visitable form items have
 * to implement the <code>FormItemVisitable</code> interface.
 *
 * @see org.jvoicexml.interpreter.FormItemVisitable
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.interpreter.FormItem
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface FormItemVisitor {
    /**
     * A <code>&lt;block&gt;</code> element is visited by setting its form
     * item variable to <code>true</code>, evaluating its content, and then
     * bypassing the process phase. No input is collected, and the next
     * iteration of the FIA's main loop is entered.
     *
     * @param block The block form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the block.
     */
    EventHandler visitBlockFormItem(final BlockFormItem block)
            throws JVoiceXMLEvent;

    /**
     * If a <code>&lt;field&gt;</code> is visited, the FIA selects and queues
     * up any prompts based on the item'sprompt counter and prompt
     * conditions. Then it activates and listens for the field level
     * grammar(s) and any higher-level grammars, and waits for the item
     * to be filled or for some events to be generated.
     *
     * @param field The field form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the field.
     */
    EventHandler visitFieldFormItem(final InputItem field)
            throws JVoiceXMLEvent;

    /**
     * This element controls the initial interaction in a mixed initiative form.
     * Its prompts should be written to encourage the user to say something
     * matching a form level grammar. When at least one input item variable is
     * filled as a result of recognition during an <code>&lt;initial&gt;</code>
     * element, the form item variable of <code>&lt;initial&gt;</code> becomes
     * <code>true</code>, thus removing it as an alternative for the FIA.
     *
     * @param initial The field form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the initial form item.
     */
    EventHandler visitInitialFormItem(final InitialFormItem initial)
            throws JVoiceXMLEvent;

    /**
     * This input item invokes a platform-specific <em>object</em> with various
     * parameters. The result of the platform object is an ECMAScript Object.
     * One platform object could be a builtin dialog that gathers credit card
     * information. Another could gather a text message using some proprietary
     * DTMF text entry method. There is no requirement for implementations to
     * provide platform-specific objects, although implementations must handle
     * the <code>&lt;object&gt;</code> element by throwing
     * <code>error.unsupported.objectname</code> if the particular
     * platform-specific object is not supported (note that
     * <code>objectname</code> in <code>error.unsupported.objectname</code> is
     * a fixed string, so not substituted with the name of the unsupported
     * object; more specific error information may be provided in the event
     * <code>_message</code> special variable as described in
     * <a href="http://www.w3.org/TR/voicexml20#dml5.2.2">Section 5.2.2</a>).
     *
     * @param object The object form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the object form item.
     */
    EventHandler visitObjectFormItem(final ObjectFormItem object)
            throws JVoiceXMLEvent;

    /**
     * An input item whose value is an audio clip recorded by the user. A
     * <code>&lt;record&gt;</code> element could collect a voice mail message,
     * for instance.
     *
     * @param record The record form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the record form item.
     */
    EventHandler visitRecordFormItem(final RecordFormItem record)
            throws JVoiceXMLEvent;

    /**
     * A <code>&lt;>subdialog&gt;</code> input item is roughly like a function
     * call. It invokes another dialog on the current page, or invokes another
     * VoiceXML document. It returns an ECMAScript Object as its result.
     *
     * @param subdialog The subdialog form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the subdialog form item.
     */
    EventHandler visitSubdialogFormItem(final SubdialogFormItem subdialog)
            throws JVoiceXMLEvent;

    /**
     * An input item which transfers the user to another telephone number. If
     * the transfer returns control, the field variable will be set to the
     * result status.
     *
     * @param transfer The transfer form item to visit.
     *
     * @return Event handler for the process phase.
     *
     * @exception JVoiceXMLEvent
     *            Error or event executing the transfer form item.
     */
    EventHandler visitTransferFormItem(final TransferFormItem transfer)
            throws JVoiceXMLEvent;
}
