/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml;

import java.math.BigDecimal;

/**
 * Utility class to parse a given time into milliseconds.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */

public final class TimeParser {
    /** Number of milliseconds per seconds. */
    private static final int MSECS_PER_SEC = 1000;

    /** The time to parse. */
    private final String time;

    /**
     * Constructs a new object.
     * @param str the given time to parse.
     */
    public TimeParser(final String str) {
        time = str;
    }

    /**
     * Parses the given time into milliseconds.
     * @return number of milliseconds, <code>-1</code> if the value can not
     *         be converted to a number.
     */
    public long parse() {
        if (time == null) {
            return -1;
        }
        final boolean isSeconds;
        if (time.endsWith("ms")) {
            isSeconds = false;
        } else if (time.endsWith("s")) {
            isSeconds = true;
        } else {
            return -1;
        }
        final String timeValue;
        final long factor;
        if (isSeconds) {
            timeValue = time.substring(0, time.length() - 1);
            factor = MSECS_PER_SEC;
        } else {
            timeValue = time.substring(0, time.length() - "ms".length());
            factor = 1;
        }
        final BigDecimal number;
        try {
            number = new BigDecimal(timeValue);
        } catch (NumberFormatException e) {
            return -1;
        }
        float flt = number.floatValue();
        return (long) (flt * factor);
    }
}
