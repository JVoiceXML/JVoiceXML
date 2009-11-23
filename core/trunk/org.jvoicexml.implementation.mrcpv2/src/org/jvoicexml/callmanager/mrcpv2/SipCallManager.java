/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mrcpv2;

import java.util.Collection;

import org.jvoicexml.callmanager.BaseCallManager;
import org.jvoicexml.callmanager.ConfiguredApplication;
import org.jvoicexml.callmanager.Terminal;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A SIP call manager.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class SipCallManager extends BaseCallManager {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Terminal> createTerminals() throws NoresourceError {
        final Collection<ConfiguredApplication> applications =
            getApplications();
        final Collection<Terminal> terminals =
            new java.util.ArrayList<Terminal>();
        for (ConfiguredApplication application : applications) {
            final String terminalName = application.getTerminal();
            final Terminal terminal = new SipTerminal(terminalName, this);
            terminals.add(terminal);
        }
        return terminals;
    }
}
