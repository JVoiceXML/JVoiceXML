/*
 * File:    $RCSfile: ExternalResource.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.RemoteConnectable;
import org.jvoicexml.event.error.NoresourceError;

/**
 * An external resource that is required by the used implementation
 * platform.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface ExternalResource
    extends RemoteConnectable {
    /**
     * Retrieves a unique identifier for this external resource.
     * @return unique identifier.
     */
    String getType();

    /**
     * Initializes and and acquires the needed resources.
     * @exception NoresourceError
     *            The resource could not be opened.
     */
    void open()
            throws NoresourceError;

    /**
     * Activates this resource, when it is retrieved from the pool.
     *
     * @since 0.5.5
     */
    void activate();

    /**
     * Passivates this resource, when it is returned to the pool.
     *
     * @since 0.5.5
     */
    void passivate();

    /**
     * Closes and releases the acquired resources.
     */
    void close();
}
