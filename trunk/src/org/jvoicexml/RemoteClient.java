/*
 * File:    $HeadURL:https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/RemoteClient.java $
 * Version: $LastChangedRevision:161 $
 * Date:    $Date:2006-11-30 10:36:05 +0100 (Do, 30 Nov 2006) $
 * Author:  $LastChangedBy:schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.io.Serializable;

/**
 * Data container that holds all the information that is needed to connect the
 * server side resources {@link SystemOutput}, {@link UserInput}, and
 * {@link CallControl} to the client.
 *
 * <p>
 * The implementing object is created at the client side and transferred
 * to the the JVoiceXml server via serialization. The implementation
 * platform then calls the <code>connect()</code> method to start the
 * communication of the client with the server side resources.
 * </p>
 *
 * <p>
 * A <code>RemoteClient</code> may also specify the server side resource it
 * wants to use. Each {@link SystemOutput}, {@link UserInput}, and
 * {@link CallControl} can be identified using a unique string. If the
 * <code>RemoteClient</code> does not specify a resource, the default resource
 * is taken.
 * </p>
 *
 * @see RemoteConnectable
 *
 * @author Dirk Schnelle
 * @version $Revision:161 $
 *
 * @since 0.5.5
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface RemoteClient extends Serializable {
    /**
     * Retrieves a unique identifier for the {@link SystemOutput} to use.
     * @return Identifier for the {@link SystemOutput}, or <code>null</code>
     * if the default resource should be used.
     */
    String getSystemOutput();

    /**
     * Retrieves a unique identifier for the {@link UserInput} to use.
     * @return Identifier for the {@link UserInput}, or <code>null</code>
     * if the default resource should be used.
     */
    String getUserInput();

    /**
     * Retrieves a unique identifier for the {@link CallControl} to use.
     * @return Identifier for the {@link CallControl}, or <code>null</code>
     * if the default resource should be used.
     */
    String getCallControl();
}
