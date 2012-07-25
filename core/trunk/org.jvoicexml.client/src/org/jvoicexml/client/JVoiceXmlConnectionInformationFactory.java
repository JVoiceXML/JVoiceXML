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

import org.jvoicexml.CallControl;
import org.jvoicexml.UserInput;

/**
 * {@link ConnectionInformationFactory} for the default identifiers
 * deployed with JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class JVoiceXmlConnectionInformationFactory
        implements ConnectionInformationFactory {
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
        return new BasicConnectionInformationController(call, output, input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getCallControlIdentifiers() {
        return new String[] {"dummy"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSystemOutputIdentifiers() {
        return new String[] {"jsapi10", "jsapi20", "mary", "marc"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getUserInputIdentifiers() {
        return new String[] {"jsapi10", "jsapi20", "mary", "marc"};
    }

}
