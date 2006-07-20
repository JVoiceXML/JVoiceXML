/*
 * File:    $RCSfile: GrammarRegistry.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2005/12/13 08:28:03 $
 * Author:  $Author: schnelle $
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

import java.util.Collection;

import javax.speech.recognition.RuleGrammar;

/**
 * Provides access to active grammars.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface GrammarRegistry {
    /**
     * Adds the given rule grammar to the list of known grammars.
     * @param grammar RuleGrammar
     */
    void addGrammar(final RuleGrammar grammar);

    /**
     * Get all registered grammars.
     *
     * @return Collection
     */
    Collection<RuleGrammar> getGrammars();
}
