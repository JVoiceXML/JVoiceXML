    /*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/ObservableSpokenInput.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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


/**
 * A {@link org.jvoicexml.UserInput} that can be monitored by
 * {@link SpokenInputListener}s.
 *
 * <p>
 * Implementations must implement this interface to propagate input events
 * to the interpreter.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 2129 $
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public interface ObservableSpokenInput {
    /**
     * Adds a listener for user input events.
     *
     * <p>
     * The implementation of this interface must notify the listener
     * about all events.
     * </p>
     *
     * @param listener The listener.
     * @since 0.5
     */
    void addListener(final SpokenInputListener listener);

    /**
     * Removes a listener for user input events.
     *
     * @param listener The listener.
     * @since 0.6
     */
    void removeListener(final SpokenInputListener listener);
}
