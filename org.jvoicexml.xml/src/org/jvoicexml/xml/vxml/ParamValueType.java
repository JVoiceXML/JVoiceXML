/*
 * File:    $RCSfile: ParamValueType.java,v $
 * Version: $Revision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $, Dirk Schnelle-Walka, project lead
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.xml.vxml;



/**
 * Valuetype attribute of the <code>&lt;param&gt;</code> tag.
 *
 * @see org.jvoicexml.xml.vxml.Param
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.5
 */
public enum ParamValueType {
    /**
     * The associated value is plain data.
     */
    DATA("data"),

    /**
     * The associated value is reference by an URI.
     */
    REF("ref");

    /** Name of the value type. */
    private final String type;

    /**
     * Creates a new object.
     * @param valueType Name of the value type.
     */
    private ParamValueType(final String valueType) {
        type = valueType;
    }

    /**
     * Retrieves the name of this value type.
     * @return Name of this type.
     */
    public String getType() {
        return type;
    }
}
