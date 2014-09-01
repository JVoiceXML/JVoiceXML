/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/ScriptingEngine.java $
 * Version: $LastChangedRevision: 4209 $
 * Date:    $Date: 2014-08-15 09:10:19 +0200 (Fri, 15 Aug 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.interpreter;

import java.util.EventObject;

/**
 * Event that may happen while a session is being executed.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
@SuppressWarnings("serial")
public class SessionEvent extends EventObject {
    public static final int SESSION_STARTED = 1;
    public static final int SESSION_INPUT = 2;
    public static final int SESSION_OUTPUT = 3;
    public static final int SESSION_ENDED = 4;

    /** The type of the event. */
    private final int type;

    /**
     * Constructs a new object.
     * @param source the source issuing the event
     * @param eventType the type of the event
     */
    public SessionEvent(Object source, final int eventType) {
        super(source);
        type = eventType;
    }

    /**
     * Retrieves the type of the event
     * @return the ype of the event
     * @since 0.7.7
     * @see #SESSION_STARTED
     * @see #SESSION_INPUT
     * @see #SESSION_OUTPUT
     * @see #SESSION_ENDED
     */
    public int getType() {
        return type;
    }
}
