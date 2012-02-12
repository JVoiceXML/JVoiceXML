/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.xml.vxml.Filled;


/**
 * Implementation of a input item. Main purpose of this class is to allow a
 * distinction between <em>input</em> items and <em>control</em> items.
 *
 * <p>
 * An input item specifies an <em>input item variable</em> to gather input
 * from the user. Input items have prompts to tell the user what to say or key
 * in, grammars that define the allowed inputs, and event handlers that process
 * any resulting events. An input item may also have a
 * <code>&lt;filled&gt;</code> element that defines an action to take just
 * after the input item variable is filled.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @see ControlItem
 * @version $Revision$
 * @since 0.7
 */
public interface InputItem
    extends CatchContainer, PromptCountable, EventCountable {
    /**
     * Gets all nested <code>&lt;filled&gt;</code> elements.
     *
     * @return Collection about all nested <code>&lt;filled&gt;</code> tags.
     */
    Collection<Filled> getFilledElements();

}
