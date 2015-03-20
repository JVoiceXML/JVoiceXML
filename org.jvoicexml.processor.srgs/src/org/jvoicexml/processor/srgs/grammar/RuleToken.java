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

//Comp. 2.0.6

public class RuleToken extends RuleComponent {
    private String text;

    public RuleToken(String text) throws IllegalArgumentException {
        if ((text == null) || (text.length() == 0)) {
            throw new IllegalArgumentException("'" + text + "'"
                    + " is not a valid grammar text");
        }

        // TODO Check causes for IllegalArgumentsException

        final char[] chars = text.toCharArray();
        StringBuffer str = new StringBuffer();
        int pos = 0;
        do {
            while ((pos < chars.length) && isWhitespace(chars[pos])) {
                ++pos;
            }

            if ((pos < chars.length) && (str.length() > 0)) {
                str.append(' ');
            }

            while ((pos < chars.length)
                    && !isWhitespace(chars[pos])) {
                str.append(chars[pos]);
                ++pos;
            }
        } while (pos < text.length());

        this.text = str.toString();
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return text;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RuleToken other = (RuleToken) obj;
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!text.equals(other.text)) {
            return false;
        }
        return true;
    }
}
