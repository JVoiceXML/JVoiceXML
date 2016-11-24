/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.xml.ssml;

/**
 * Parses a percentage. A non-negative percentage is an unsigned number
 * immediately followed by <code>"%"</code>.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 */
final class HertzParser {
    /**
     * Prevent construction.
     */
    private HertzParser() {
    }

    /**
     * Parses the given percentage value into a float. Throws an
     * {@link NumberFormatException} if the value can not be parsed.
     * @param value percentage value
     * @return parsed value
     */
    public static float parse(final String value) {
        final String number = value.substring(0, value.length() - 2);
        return Float.parseFloat(number);
    }

    /**
     * Checks if the given percentage value is a relative percentage.
     * @param value percentage value
     * @return <code>true</code> if the percentage value is relative.
     */
    public static boolean isRelative(final String value) {
        final char pre = value.charAt(0);
        return (pre == '+') || (pre == '-');
    }
}
