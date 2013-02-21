/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/callmanager/ConnectionInformationFactory.java $
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 04:25:46 -0600 (lun, 10 ene 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager;

import org.jvoicexml.CallManager;
import org.jvoicexml.ConnectionInformation;

/**
 * Some {@link CallManager}s will require to use a custom implementation of a
 * {@link ConnectionInformation}. This factory allows to create those custom
 * implementations.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2493 $
 * @since 0.7
 */
public interface ConnectionInformationFactory {
    /**
     * Factory method to retrieve a new {@link ConnectionInformation}.
     * @param callManager the calling call manager instance
     * @param application the called configured application.
     * @param parameters additional optional parameters.
     * @return created connection information container
     * @exception ConnectionInformationCreationException
     *            error creating theconnection information
     */
    ConnectionInformation createConnectionInformation(
            final CallManager callManager,
            final ConfiguredApplication application,
            final CallParameters parameters)
        throws ConnectionInformationCreationException;
}
