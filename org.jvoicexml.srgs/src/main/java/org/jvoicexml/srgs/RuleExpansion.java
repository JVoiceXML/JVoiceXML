/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.srgs;

import java.util.List;

import org.jvoicexml.srgs.sisr.ExecutableSemanticInterpretation;

/**
 * An expansion to the rule to actually work on the received tokens.
 * @author Dirk Schnelle-Walka
 */
public interface RuleExpansion {
    /**
     * Process the token at offset.
     * @param tokens received tokens
     * @param offset current offset in tokens
     * @return
     */
    MatchConsumption match(List<String> tokens, int offset);

    void setExecutionSemanticInterpretation(
            ExecutableSemanticInterpretation si);

    /**
     * Dumps the current contents on the console.
     * @param pad space padding
     */
    void dump(String pad);
}
