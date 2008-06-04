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

import org.jvoicexml.event.error.BadFetchError;
import org.w3c.dom.NodeList;


/**
 * There are two kinds of dialogs: forms and menus.
 * Forms define an interaction that collects values for a set of form item
 * variables. Each field may specify a grammar that defines the allowable
 * inputs for that field. If a form-level grammar is present, it can be used to
 * fill several fields from one utterance. A menu presents the user with a
 * choice of options and then transitions to another dialog based on that
 * choice.
 *
 * <p>
 * {@link Dialog}s are either <code>&lt;form&gt;</code> or
 * a <code>&lt;menu&gt;</code> and are interpreted via the
 * <code>FormInterpretationAlgorithm</code>.
 * They can contain <code>FormItem</code>s.
 * </p>
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.interpreter.FormItem
 * @see org.jvoicexml.xml.vxml.Form
 * @see org.jvoicexml.xml.vxml.Menu
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public interface Dialog
        extends DialogConstruct {
    /** Default name of an unnamed form. */
    String UNNAMED_FORM = "unnamed";

    /**
     * Retrieves the identifier of this <code>Dialog</code>.
     * It allows the <code>Dialog</code> to be target of
     * a <code>&lt;goto&gt;</code> or a <code>&lt;submit&gt;</code>.
     *
     * <p>
     * If the related form or menu does not provide an id,
     * <code>UNNAMED_FORM</code> is returned as the id for this form.
     * </p>
     *
     * @return Identifier for this form.
     * @see #UNNAMED_FORM
     */
    String getId();

    /**
     * Retrieves the child nodes of this executable form.
     * @return Child nodes of this executable form
     * @todo Check if this can be replaced by a collection.
     */
    NodeList getChildNodes();

    /**
     * Retrieves all {@link FormItem}s, defined in this form.
     * @param context The current context.
     * @return Collection of <code>FormItem</code>s.
     * @exception BadFetchError
     *            Error obtaining the form items.
     */
    Collection<FormItem> getFormItems(final VoiceXmlInterpreterContext context)
        throws BadFetchError;
}
