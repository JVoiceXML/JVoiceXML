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
package org.jvoicexml.callmanager.mmi.test;

import java.io.IOException;

import org.jvoicexml.callmanager.mmi.ETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.xml.MMIEvent;

/**
 * Dummy implementation of an {@link ETLProtocolAdapter}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class DummyETLProtocolAdapter implements ETLProtocolAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMMIEventListener(MMIEventListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMMIEventListener(MMIEventListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMMIEvent(final Object channel, final MMIEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
    }

}
