/*
 * File:    $RCSfile: EventObserver.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/05/02 09:40:38 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event;

/**
 * Event observer to catch events generated from the
 * <code>ImplementationPlatform</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * @see org.jvoicexml.implementation.ImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
public interface EventObserver {
    /**
     * Notification about an event in the implementation platform.
     * @param event The event.
     */
    void notifyEvent(final JVoiceXMLEvent event);
}
