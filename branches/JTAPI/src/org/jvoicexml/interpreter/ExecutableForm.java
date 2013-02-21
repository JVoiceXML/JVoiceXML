/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.w3c.dom.NodeList;


/**
 * <code>ExecutableForm</code>s are either <code>&lt;form&gt;</code> or
 * a <code>&lt;menu&gt;</code> and are interpreted via the
 * <code>FormInterpretationAlgorithm</code>.
 * They can contain <code>FormItem</code>s.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.interpreter.FormItem
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public interface ExecutableForm
        extends DialogConstruct {
    /** Default name of an unnamed form. */
    String UNNAMED_FORM = "unnamed";

    /**
     * Retrieves the identifier of this <code>ExecutableForm</code>.
     * It allows the <code>ExecutableForm</code> to be target of
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
     * Retrieves all <code>FormItem</code>s, defined in this form.
     * @param context The current context.
     * @return Collection of <code>FormItem</code>s.
     */
    Collection<FormItem> getFormItems(final VoiceXmlInterpreterContext context);
}
