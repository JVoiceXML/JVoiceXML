/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.client;

import org.jvoicexml.ConnectionInformation;

/**
 * A factory or {@link ConnectionInformation}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public interface ConnectionInformationFactory {
    /**
     * Retrieves all identifiers for {@link org.jvoicexml.CallControl} that are
     * supported by this factory
     * @return all identifiers.
     */
    String[] getCallControlIdentifiers();

    /**
     * Retrieves all identifiers for {@link org.jvoicexml.SystemOutput} that are
     * supported by this factory
     * @return all identifiers.
     */
    String[] getSystemOutputIdentifiers();

    /**
     * Retrieves all identifiers for {@link org.jvoicexml.UserInput} that are
     * supported by this factory
     * @return all identifiers.
     */
    String[] getUserInputIdentifiers();

    /**
     * Creates a {@link ConnectionInformation} with the specified identifiers.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     * @exception UnsupportedResourceIdentifierException
     *           if one of the specified identifiers is not supported in
     *           this factory
     */
    ConnectionInformation createConnectionInformation(final String call,
            final String output, final String input)
                    throws UnsupportedResourceIdentifierException;
}
