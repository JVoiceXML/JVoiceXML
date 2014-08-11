/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 59 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.srgs.grammar;

//Comp 2.0.6

public class GrammarExceptionDetail {
    public static final int UNKNOWN_VALUE = -1;

    public static final int UNKNOWN_TYPE = -1;

    public static final int UNSUPPORTED_ALPHABET = 1;

    public static final int UNSUPPORTED_LANGUAGE = 4;

    public static final int UNSUPPORTED_LEXEME = 5;

    public static final int UNSUPPORTED_LEXICON = 6;

    public static final int UNSUPPORTED_PHONEME = 7;

    public static final int SYNTAX_ERROR = 9;

    private final int type;
    
    private final String textInfo;
    
    private final String grammarReference;

    private final String ruleName;

    private final int lineNumber;

    private final int charNumber;

    private final String message;

    public GrammarExceptionDetail(int type, String textInfo,
            String grammarReference, String ruleName, int lineNumber,
            int charNumber, String message) throws IllegalArgumentException {
        if ((lineNumber <= 0) && (lineNumber != UNKNOWN_VALUE)) {
            throw new IllegalArgumentException(
                    "Line number must be a positive integer or UNKNOWN_VALUE");
        }
        if ((charNumber <= 0) && (charNumber != UNKNOWN_VALUE)) {
            throw new IllegalArgumentException(
                    "Char number must be a positive integer or UNKNOWN_VALUE");
        }
        this.type = type;
        this.textInfo = textInfo;
        this.grammarReference = grammarReference;
        this.ruleName = ruleName;
        this.lineNumber = lineNumber;
        this.charNumber = charNumber;
        this.message = message;
    }

    public int getCharNumber() {
        if (getLineNumber() == UNKNOWN_VALUE) {
            return UNKNOWN_VALUE;
        }
        return charNumber;
    }

    public String getGrammarReference() {
        return grammarReference;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public int getType() {
        return type;
    }
}
