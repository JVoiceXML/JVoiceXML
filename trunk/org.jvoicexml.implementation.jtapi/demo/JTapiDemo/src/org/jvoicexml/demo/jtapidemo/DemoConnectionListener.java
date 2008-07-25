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

import javax.telephony.CallEvent;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.MetaEvent;

/**
 * Connection listener for the demo call.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.7
 */
final class DemoConnectionListener implements ConnectionListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void callActive(final CallEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callEventTransmissionEnded(final CallEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void callInvalid(final CallEvent event) {
        System.out.println("*** invalid");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void multiCallMetaMergeEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void multiCallMetaMergeStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void multiCallMetaTransferEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void multiCallMetaTransferStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void singleCallMetaProgressEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void singleCallMetaProgressStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void singleCallMetaSnapshotEnded(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void singleCallMetaSnapshotStarted(final MetaEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionAlerting(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionConnected(final ConnectionEvent event) {
        System.out.println("*** connected");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionCreated(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionDisconnected(final ConnectionEvent event) {
        System.out.println("*** disconnected");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionFailed(final ConnectionEvent event) {
        System.out.println("*** failed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionInProgress(final ConnectionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectionUnknown(final ConnectionEvent event) {
    }
}
