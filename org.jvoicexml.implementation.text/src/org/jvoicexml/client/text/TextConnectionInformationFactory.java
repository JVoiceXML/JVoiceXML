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

package org.jvoicexml.client.text;

import org.jvoicexml.CallControl;
import org.jvoicexml.UserInput;
import org.jvoicexml.client.ConnectionInformationController;
import org.jvoicexml.client.ConnectionInformationFactory;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;

/**
 * A connection information factory for text based clients.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class TextConnectionInformationFactory
        implements ConnectionInformationFactory {
    /**
     * Constructs a new object.
     */
    public TextConnectionInformationFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getCallControlIdentifiers() {
        return new String[] {"text"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSystemOutputIdentifiers() {
        return new String[] {"text"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getUserInputIdentifiers() {
        return new String[] {"text"};
    }

    /**
     * Checks if the the given identifier is among the given list of
     * identifiers.
     * @param identifier the identifier to check
     * @param identifiers the list of available identifiers
     * @param resource the resource to check
     * @throws UnsupportedResourceIdentifierException
     *         if the identifier is not among the list of given identifiers
     */
    private void checkIdentifiers(final String identifier,
            final String[] identifiers, final Class<?> resource)
                    throws UnsupportedResourceIdentifierException {
        for (String current : identifiers) {
            if (current.equals(identifier)) {
                return;
            }
        }
        final String message = String.format("%s is not supported for type %s",
                identifier, resource.getCanonicalName());
        throw new UnsupportedResourceIdentifierException(message);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionInformationController createConnectionInformation(
            final String call, final String output, final String input)
            throws UnsupportedResourceIdentifierException {
        // First check if all requested resources are available.
        final String[] calls = getCallControlIdentifiers();
        checkIdentifiers(call, calls, CallControl.class);
        final String[] outputs = getSystemOutputIdentifiers();
        checkIdentifiers(output, outputs, CallControl.class);
        final String[] inputs = getUserInputIdentifiers();
        checkIdentifiers(input, inputs, UserInput.class);

        // If successful: create a new connection info
        return new TextConnectionInformationController();
    }

}
