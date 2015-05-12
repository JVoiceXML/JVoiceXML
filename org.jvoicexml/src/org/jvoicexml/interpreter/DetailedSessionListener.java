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

import org.jvoicexml.Session;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * A listener to session events. In contrast to the
 * {@link org.jvoicexml.SessionListener} the events are not exposed remotely.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public interface DetailedSessionListener {
    /**
     * The session has started.
     * 
     * @param session
     *            the session
     */
    void sessionStarted(final Session session);

    /**
     * An event was sent over the {@link org.jvoicexml.event.EventBus}.
     * 
     * @param session
     *            the session
     * @param event
     *            the event with detailed data
     */
    void sessionEvent(final Session session, final JVoiceXMLEvent event);

    /**
     * The session has ended.
     * 
     * @param session
     *            the session
     */
    void sessionEnded(final Session session);
}
