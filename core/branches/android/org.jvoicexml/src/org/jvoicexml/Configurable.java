/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/Configurable.java $
 * Version: $LastChangedRevision: 2509 $
 * Date:    $Date: 2011-01-16 07:40:14 -0600 (dom, 16 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

/**
 * A component that requires further configuration. This is usually the case for
 * container components. 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2509 $
 * @since 0.7.4
 */
public interface Configurable {
    /**
     * Initializes this component with data from the given configuration object.
     * @param configuration the current configuration, maybe <code>null</code>
     * @exception ConfigurationException
     *            error initializing the component.
     * @since 0.7.4
     */
    void init(Configuration configuration) throws ConfigurationException;
}
