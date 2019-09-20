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
 * Enumeration of status information to be included in {@link StatusResponse}s.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
@XmlType(name = "statusResponseType")
@XmlEnum
public enum StatusResponseType {
    /** Status alive. */
    @XmlEnumValue("alive")
    ALIVE("alive"),
    /** Status dead. */
    @XmlEnumValue("dead")
    DEAD("dead");

    /** Concrete value. */
    private final String value;

    StatusResponseType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusResponseType fromValue(String v) {
        for (StatusResponseType c: StatusResponseType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
