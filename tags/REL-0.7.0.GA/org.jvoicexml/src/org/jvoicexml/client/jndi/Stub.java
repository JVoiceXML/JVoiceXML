/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/jndi/client/Stub.java $
 * Version: $LastChangedRevision: 211 $
 * Date:    $Date: 2007-02-05 09:31:17 +0100 (Mo, 05 Feb 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.jndi;

import javax.naming.Referenceable;

/**
 * Stub at the client side that remotely calls the
 * {@link org.jvoicexml.jndi.Skeleton}.
 *
 * @author Dirk Schnelle
 * @version $Revision: 211 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 * @see org.jvoicexml.jndi.Skeleton
 */
public interface Stub
        extends Referenceable {
    /**
     * Retrieves the name to which the stub is bound.
     * @return Name of the stub.
     */
    String getStubName();
}
