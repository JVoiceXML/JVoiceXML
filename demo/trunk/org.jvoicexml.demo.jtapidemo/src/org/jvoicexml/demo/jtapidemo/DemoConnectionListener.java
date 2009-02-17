/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.demo.jtapidemo;

import javax.telephony.Address;
import javax.telephony.CallEvent;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.MetaEvent;

import org.apache.log4j.Logger;

/**
 * Connection listener for the demo call.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
final class DemoConnectionListener implements ConnectionListener {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(DemoConnectionListener.class);

    /**
     * {@inheritDoc}
     */
    public void callActive(final CallEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void callEventTransmissionEnded(final CallEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void callInvalid(final CallEvent event) {
        System.out.println("*** invalid");
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionAlerting(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionConnected(final ConnectionEvent event) {
        Address address = event.getConnection().getAddress();
        LOGGER.info("connected to " + address);
    }

    /**
     * {@inheritDoc}
     */
    public void connectionCreated(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionDisconnected(final ConnectionEvent event) {
        System.out.println("*** disconnected");
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connectionFailed(final ConnectionEvent event) {
        System.out.println("*** failed");
    }

    /**
     * {@inheritDoc}
     */
    public void connectionInProgress(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void connectionUnknown(final ConnectionEvent event) {
    }
}
