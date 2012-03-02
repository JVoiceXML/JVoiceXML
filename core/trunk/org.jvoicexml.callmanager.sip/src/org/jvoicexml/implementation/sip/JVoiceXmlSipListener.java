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
package org.jvoicexml.implementation.sip;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

/**
 * A listener for SIP messages.
 * @author dirk
 * @version $Revision: $
 * @since 0.7.6
 */
public class JVoiceXmlSipListener implements SipListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void processDialogTerminated(final DialogTerminatedEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processIOException(final IOExceptionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processRequest(final RequestEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processResponse(final ResponseEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTimeout(final TimeoutEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTransactionTerminated(
            final TransactionTerminatedEvent event) {
    }
}
