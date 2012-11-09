/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/BargeInType.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

/**
 * When bargein is enabled, the bargeintype attribute can be used to suggest
 * the type of bargein the platform will perform in response to voice or DTMF
 * input.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.5
 */
public enum BargeInType {
    /**
     * The prompt will be stopped as soon as speech or DTMF input is detected.
     * The prompt is stopped irrespective of whether or not the input matches a
     * grammar and irrespective of which grammars are active.
     */
    SPEECH("speech"),

    /**
     * The prompt will not be stopped until a complete match of an active
     * grammar is detected. Input that does not match a grammar is ignored
     * (note that this even applies during the timeout period); as a
     * consequence, a nomatch event will never be generated in the case of
     * hotword bargein.
     */
    HOTWORD("hotword");

    /** Name of the barge-in type. */
    private final String type;

    /**
     * Do not create from outside.
     * @param name name of the barge-in type.
     */
    private BargeInType(final String name) {
        type = name;
    }

    /**
     * Retrieves the name of this barge-in type.
     * @return Name of this type.
     */
    public String getType() {
        return type;
    }
}
