/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.callmanager.CallParameters;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.ConnectionInformationCreationException;
import org.jvoicexml.callmanager.TerminalConnectionInformationFactory;

/**
 * A factory for the {@link JtapiConnectionInformation}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class JtapiConnectionInformationFactory
    implements TerminalConnectionInformationFactory {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(JtapiConnectionInformationFactory.class);

    /**
     * {@inheritDoc}
     */
    public ConnectionInformation createConnectionInformation(final CallManager callManager,
            final ConfiguredApplication application,
            final CallParameters parameters)
        throws ConnectionInformationCreationException {
        final JVoiceXmlTerminal term =
            (JVoiceXmlTerminal) parameters.getTerminal();
        final String output = application.getOutputType();
        final String input = application.getInputType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating connection information with output '"
                    + output + "' and input '" + input + "' for terminal '"
                    + term.getName() + "'");
        }
        try {
            JtapiConnectionInformation info =
                new JtapiConnectionInformation(term, output, input);
            final URI calledId = parameters.getCalledId();
            info.setCalledDevice(calledId);
            final URI callingId = parameters.getCallerId();
            info.setCallingDevice(callingId);
            return info;
        } catch (UnknownHostException e) {
            throw new ConnectionInformationCreationException(e.getMessage(), e);
        }
    }
}
