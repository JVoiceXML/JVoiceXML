/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2018 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.srgs.Ruleref;

/**
 * Speial rule NULL as defined in https://www.w3.org/TR/speech-grammar/#S2.2.3
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 *
 */
class NullRule extends SrgsRule {
    public NullRule() {
        super(Ruleref.SPECIAL_VALUE_NULL);
    }

    /**
     * @see SrgsRule#match(List, int)
     */
    @Override
    public MatchConsumption match(List<String> tokens, int index) {
        // Never match
        return null;
    }
}
