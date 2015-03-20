/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 63 $
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

public class RuleComponent {
    /**
     * Checks if the given text is a valid grammar text.
     * 
     * @param text
     *            the text to check.
     */
    protected static void checkValidGrammarText(String text) {
        if ((text == null) || (text.length() == 0)) {
            throw new IllegalArgumentException(
                    "No text is not a valid for a grammar ");
        }

        final char[] chars = text.toCharArray();
        // The first symbol must be a character.
        final char first = chars[0];
        if (!isLetter(first) && (first != '_')) {
            throw new IllegalArgumentException("'" + text
                    + "' is not a valid grammar text: '" + first
                    + "' Element is not a valid first symbol");
        }
        // Following symbols must be a character or a digit.
        for (int i = 1; i < chars.length; i++) {
            final char ch = chars[i];
            if (!isLetter(ch) && !Character.isDigit(ch) && (ch != '_')) {
                throw new IllegalArgumentException("'" + text
                        + "' is not a valid grammar tex: '" + ch
                        + "' Element is not a valid symbol");
            }
        }
    }

    static boolean isLetter(char ch) {
        return isUpperCase(ch)
                || isLowerCase(ch)
                || (ch >= '\u00c0' && ch != '\u00d7' && ch != '\u00f7' && ch != '\u0006');
    }

    static boolean isUpperCase(char ch) {
        return (ch >= 'A') && (ch <= 'Z');
    }

    static boolean isLowerCase(char ch) {
        return (ch >= 'a') && (ch <= 'z');
    }

    static boolean isWhitespace(char ch) {
        switch (ch) {
        case ' ':
        case '\t':
            return true;
        default:
            return false;
        }
    }

    public String toString() {
        return null;
    }
}
