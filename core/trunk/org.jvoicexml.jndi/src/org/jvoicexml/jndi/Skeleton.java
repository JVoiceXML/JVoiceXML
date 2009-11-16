/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/jndi/Skeleton.java $
 * Version: $LastChangedRevision: 1874 $
 * Date:    $LastChangedDate: 2009-10-20 09:07:58 +0200 (Di, 20 Okt 2009) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jndi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Skeleton that forwards all remote method calls from the
 * <code>Stub</code> on the client side to the exported object in
 * the JNDI namespace.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1874 $
 * @since 0.4
 * @see org.jvoicexml.client.jndi.Stub
 */
public interface Skeleton
        extends Remote {
    /**
     * Retrieves the name to which the skeleton is bound.
     * @return Name of the skeleton.
     * @exception RemoteException
     *            Error calling the remote method.
     */
    String getSkeletonName()
            throws RemoteException;
}
