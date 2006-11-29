/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/KeyedPlatformPool.java $
 * Version: $LastChangedRevision: 110 $
 * Date:    $Date: 2006-09-04 09:27:06 +0200 (Mo, 04 Sep 2006) $
 * Author:  $LastChangedBy: schnelle $
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

import org.jvoicexml.event.error.NoresourceError;

/**
 * Objects that implement this interface can be connected to a
 * {@link RemoteClient}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 119 $
 *
 * @since 0.5.5
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface RemoteConnectable {
    /**
     * Establishes a connection from the given {@link RemoteClient} to this
     * object.
     * @param client data container with connection relevant data.
     * @throws NoresourceError
     *         error establishing the connection.
     */
    void connect(final RemoteClient client)
        throws NoresourceError;
}
