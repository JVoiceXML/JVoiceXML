package org.jvoicexml.callmanager.mmi;
/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 11:25:46 +0100 (Mo, 10 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;

import org.jvoicexml.CallManager;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A callmanager for MMI integration.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public class MMICallManager implements CallManager {
    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /** The adapter for the used ETL protocol. */
    private ETLProtocolAdapter adapter;

    /** Reference to the voice modality component. */
    private VoiceModalityComponent mc;

    /**
     * Sets the adapter for the ETL specific protocol.
     * @param protocolAdapter the adapter to use.
     */
    public void setProtocolAdapter(final ETLProtocolAdapter protocolAdapter) {
        adapter = protocolAdapter;
    }

    /**
     * {@link}
     */
    @Override
    public void setJVoiceXml(JVoiceXml jvoicexml) {
        jvxml = jvoicexml;

    }

    /**
     * {@link}
     */
    @Override
    public void start() throws NoresourceError, IOException {
        if (adapter == null) {
            throw new IOException(
                    "Unable to hook to the ETL without a protocol adapter!");
        }
        mc = new VoiceModalityComponent();
        mc.startAcceptingLifecyleEvents(adapter);
    }

    /**
     * {@link}
     */
    @Override
    public void stop() {
        if (mc == null) {
            return;
        }
        mc.stopAcceptingLifecycleEvents();
    }

}
