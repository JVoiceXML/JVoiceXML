/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/ResourceFactory.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $LastChangedDate $
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

package org.jvoicexml.implementation;

import org.jvoicexml.event.error.NoresourceError;


/**
 * Factory for external resources with type <code>T</code>, that can be
 * connected to a {@link org.jvoicexml.ConnectionInformation}.
 *
 * @param <T> Type of {@link ExternalResource} to produce in this
 * factory.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
 * @since 0.5.5
 */
public interface ResourceFactory<T extends ExternalResource> {
    /**
     * Returns the class type of the resource to create.
     * @return type of the resource.
     * @since 0.7
     */
    Class<T> getResourceType();

    /**
     * Factory method to create a resource.
     * @return The created resource.
     * @throws NoresourceError
     *         error creating the requested resource.
     */
    T createResource() throws NoresourceError;

    /**
     * Retrieves the number of instances, that this factory
     * will create using {@link #createResource()}.
     * @return Number of instances.
     */
    int getInstances();

    /**
     * Retrieves the type of the resources that are created by this
     * factory.
     *
     * @return Type of this implementation platform.
     */
    String getType();
}
