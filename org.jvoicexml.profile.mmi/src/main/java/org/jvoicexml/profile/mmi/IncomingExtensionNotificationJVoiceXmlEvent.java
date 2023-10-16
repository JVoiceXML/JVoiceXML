/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/AssignStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $LastChangedDate: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.mmi;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;

/**
 * Encapsulates an MMI event received from an external modality component or
 * interaction manager into a JVoiceXML structure so that it can be delivered
 * over the {@link org.jvoicexml.event.EventBus}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
@SuppressWarnings("serial")
public class IncomingExtensionNotificationJVoiceXmlEvent
    extends JVoiceXMLEvent {
    /** The encapsulated MMI event. */
    private final Mmi notification;

    /**
     * Constructs a new object.
     * 
     * @param mmi
     *            the MMI event
     */
    public IncomingExtensionNotificationJVoiceXmlEvent(final Mmi mmi) {
        notification = mmi;
    }

    /**
     * Retrieves the encapsulated extension notification.
     * 
     * @return encapsulated extension notification
     */
    public Mmi getExtensionNotification() {
        return notification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        final ExtensionNotification ext = notification
                .getExtensionNotification();
        return "org.jvoicexml.event.plain.implementation." + ext.getName();
    }
}
