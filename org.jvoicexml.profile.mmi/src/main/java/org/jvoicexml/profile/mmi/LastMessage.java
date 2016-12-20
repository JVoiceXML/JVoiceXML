/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.processor.srgs/src/org/jvoicexml/processor/srgs/GrammarChecker.java $
 * Version: $LastChangedRevision: 4184 $
 * Date:    $Date: 2014-08-11 09:20:42 +0200 (Mon, 11 Aug 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.mmi;

import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * The last received external message.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
@SuppressWarnings("serial")
public class LastMessage extends JVoiceXMLEvent {
    /** The media type of the external message. */
    private final String contentType;

    /** The event name. */
    private final String event;

    /** The conent of the message. */
    private Object content;

    /**
     * Constructs a new object.
     * 
     * @param type
     *            The media type of the external message.
     * @param name
     *            The event name
     * @param value
     *            The content of the message
     */
    public LastMessage(final String type, final String name, final Object value) {
        contentType = type;
        event = name;
        content = value;
    }

    /**
     * Retrieves the content type.
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Retrieves the content.
     * @return the content
     */
    public Object getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return event;
    }

}
