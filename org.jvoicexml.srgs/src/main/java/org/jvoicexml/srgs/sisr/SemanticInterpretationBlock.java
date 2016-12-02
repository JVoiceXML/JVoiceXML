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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class SemanticInterpretationBlock implements ExecutableSemanticInterpretation {
    private static final Logger LOGGER = Logger.getLogger(SemanticInterpretationBlock.class);
    private StringBuffer tagContent = new StringBuffer();
    private String lastRulename = null;

    public SemanticInterpretationBlock() {
    }

    public SemanticInterpretationBlock(String text) {
        append(text);
    }

    public void append(String text) {
        if (text == null || text.trim().length() == 0) {
            if (tagContent == null || tagContent.length() == 0) {
                StringWriter sw = new StringWriter();
                new Throwable("").printStackTrace(new PrintWriter(sw));
                LOGGER.debug("I am here: " + sw.toString());
            }
            return;
        }

        String cleanedText = text.trim();
        if (cleanedText.charAt(cleanedText.length() - 1) != ';') {
            cleanedText = cleanedText + ";";
        }

        tagContent.append(cleanedText);
        tagContent.append('\n');
    }

    public String getLastRulename() {
        return lastRulename;
    }

    public void setLastRulename(String lastRulename) {
        this.lastRulename = lastRulename;
    }

    public String getCurrentText() {
        return tagContent.toString();
    }

    public ExecutableSemanticInterpretation createMatchingInstance() {
        SemanticInterpretationBlock newCopy = new SemanticInterpretationBlock();
        newCopy.append(tagContent.toString());
        newCopy.lastRulename = lastRulename;
        return newCopy;
    }

    public void dump(String pad) {
        LOGGER.debug(pad + "SI: " + tagContent);
    }

    @Override
    public void execute(Context context, Scriptable scope) {
        LOGGER.debug("executing: " + tagContent);

        context.evaluateString(scope, tagContent.toString(),
                "SISR executable from TagCollection", 0, null);
    }

}
