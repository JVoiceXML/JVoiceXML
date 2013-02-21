/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/FilledMode.java $
 * Version: $LastChangedRevision: 2325 $
 * Date:    $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-08-25 02:23:51 -0500 (mié, 25 ago 2010) $, Dirk Schnelle-Walka, project lead
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
 * Filled mode of the <code>&lt;filled&gt;</code> tag.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.7.3
 */
public enum FilledMode {
    /**
     * The action is executed when all of the mentioned input items are filled
     * by the last user input.
     */
    ALL("all"),
    /**
     * The action is executed when any of the specified input items is filled by
     * the last user input.
     */
    ANY("any");


    /** Name of the mode. */
    private final String mode;

    /**
     * Do not create from outside.
     * @param name name of the barge-in type.
     */
    private FilledMode(final String name) {
        mode = name;
    }

    /**
     * Retrieves the name of this filled mode.
     * @return Name of this mode.
     */
    public String getMode() {
        return mode;
    }
}
