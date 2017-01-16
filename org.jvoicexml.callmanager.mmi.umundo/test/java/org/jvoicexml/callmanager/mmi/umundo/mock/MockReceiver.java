/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.umundo.mock;

import org.apache.log4j.Logger;
import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

/**
 * A receiver for test purpose.
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 */
public final class MockReceiver implements ITypedReceiver {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(MockReceiver.class);

    /** The latest received object. */
    private Object receivedObject;

    /** Synchronization to wait for objects to receive. */
    private Object lock;

    /**
     * Constructs a new object.
     */
    public MockReceiver() {
        lock = new Object();
    }

    /**
     * Retrieves the latest received object.
     * @return the latest received object
     */
    public Object getReceivedObject() {
        return receivedObject;
    }

    /**
     * Waits until a message object has been received.
     * @throws InterruptedException
     *         waiting interrupted
     */
    public void waitReceivedObject() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveObject(final Object object, final Message msg) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received: " + object);
        }
        receivedObject = object;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
