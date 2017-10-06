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

package org.jvoicexml.srgs.sisr;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class AddToMatchedText implements ExecutableSemanticInterpretation {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(AddToMatchedText.class);
    private String matchedText;

    public AddToMatchedText(String text) {
        matchedText = text;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dump(final String pad) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(pad + "Added '" + matchedText + "' to matched text");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Context context, final Scriptable scope) {
        String metaCurrent = (String) context.evaluateString(scope,
                "meta.current().text;", "AddToMatchedText:get meta", 0, null);

        if (metaCurrent.length() == 0) {
            context.evaluateString(scope,
                    "meta.current=function() {return {text:'" + matchedText
                            + "', score:1.0}};", "AddToMatchedText:set meta1",
                    0, null);
        } else {
            context.evaluateString(scope,
                    "meta.current=function() {return {text:'" + metaCurrent
                            + " " + matchedText + "', score:1.0}};".replace("'", "\\'"),
                    "AddToMatchedText:set meta1", 0, null);
        }
    }
}
