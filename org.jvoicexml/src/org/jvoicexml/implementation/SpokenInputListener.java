/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/SpokenInputListener.java $
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 04:25:46 -0600 (lun, 10 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.jvoicexml.event.ErrorEvent;


/**
 * Listener for events from the {@link SpokenInput} implementation.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2493 $
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @see org.jvoicexml.UserInput
 * @since 0.5
 */
public interface SpokenInputListener {
    /**
     * Notification about status changes in the {@link SpokenInput}.
     * @param event the input event..
     * @since 0.6
     */
    void inputStatusChanged(final SpokenInputEvent event);

    /**
     * An error occured while an output processes an input.
     * <p>
     * This method is intended to feed back errors that happen while the
     * {@link org.jvoicexml.UserInput} processes an input asynchronously.
     * </p>
     * @param error the error
     * @since 0.7.4
     */
    void inputError(final ErrorEvent error);
}
