/*
 * JVoiceXML JST server plugin
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jvoicexml.eclipse.jst.server;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jst.server.generic.core.internal.GenericServerBehaviour;
import org.eclipse.wst.server.core.IServer;

/**
 * Eclipse JST server behavior for JVoiceXML.
 *
 * <p>
 * Basically just extends the Generic to allow for the pinging of the server
 * during start-up
 * </p>
 *
 * @author Aurelian Maga
 * @version 0.1
 *
 */

public final class JVoiceXMLServerBehaviour extends GenericServerBehaviour {
    /** Reference to the ping thread. */
    private JVoiceXMLPingThread ping;

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final boolean force) {
        try {
            if (ping != null) {
                ping.stopPinging();
                ping = null;
            }
            // always force the stop
            super.stop(true);
        } catch (Exception e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupLaunch(final ILaunch launch, final String launchMode,
            final IProgressMonitor monitor) throws CoreException {
        setServerState(IServer.STATE_STARTING);
        setMode(launchMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void startPingThread() {
        if (ping == null) {
            ping = new JVoiceXMLPingThread(this);

            ping.start();
        }
    }

    /**
     * Marks the server as started.
     * @param started <code>true</code> if the server is started.
     */
    public void setStarted(final boolean started) {
        if (started) {
            setServerState(IServer.STATE_STARTED);
        } else {
            setServerState(IServer.STATE_STOPPED);
        }
    }
}
