/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/Stub.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
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

package org.jvoicexml.client.jndi;

import javax.naming.Referenceable;

/**
 * Stub at the client side that remotely calls the skeleton.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.4
 */
public interface Stub
        extends Referenceable {
    /**
     * Retrieves the name to which the stub is bound.
     * @return Name of the stub.
     */
    String getStubName();
}
