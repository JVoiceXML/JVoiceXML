/*
 * File:    $RCSfile: PromptCountable.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter.formitem;

import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Tapered prompts are those that may change with each attempt.
 * Information-requesting prompts may become more terse under the assumption
 * that the user is becoming more familiar with the task. Help messages become
 * more detailed perhaps, under the assumption that the user needs more help.
 * Or, prompts can change just to make the interaction more interesting.
 *
 * Each input item, <code>&lt;initial&gt;</code>, and menu has an internal
 * prompt counter that is reset to one each time the form or menu is entered.
 * Whenever the system selects a given input item in the select phase of FIA
 * and FIA does perform normal selection and queuing of prompts, the input
 * item's associated prompt counter is incremented. This is the mechanism
 * supporting tapered prompts.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.3.1
 */
public interface PromptCountable {
    /**
     * Retrieves the name.
     * @return Name of the countable.
     */
    String getName();

    /**
     * Retrieves the corresponding node in the VoiceXML document.
     * @return Corresponding node in the VoiceXML document.
     */
    VoiceXmlNode getNode();

    /**
     * Retrieves the value of the prompt counter.
     * @return Value of the prompt counter.
     */
    int getPromptCount();

    /**
     * Increments the prompt counter.
     */
    void incrementPromptCount();

    /**
     * Resets the prompt counter.
     */
    void resetPromptCount();
}
