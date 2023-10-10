/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.mmi.events;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * Status information to be included in {@link LifeCycleResponse}s.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
@XmlType(name = "statusType")
@XmlEnum
public enum StatusType {
    /** Request was successful. */
    @XmlEnumValue("success")
    SUCCESS("success"),

    /** Request failed. */
    @XmlEnumValue("failure")
    FAILURE("failure");

    /** The status type. */
    private final String value;

    /**
     * Creates a new object.
     * @param v the status type
     */
    StatusType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusType fromValue(final String v) {
        for (StatusType c: StatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
