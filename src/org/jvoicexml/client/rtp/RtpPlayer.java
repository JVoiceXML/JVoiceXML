/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.rtp;

import java.io.IOException;
import java.net.URI;

import javax.media.ControllerListener;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

/**
 * RTP player for playing the output on the client side.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class RtpPlayer
        extends Thread {
    /** URI of the RTP source stream. */
    private final URI uri;

    /** The player. */
    private Player player;

    /** The listener for controller events. */
    private ControllerListener listener;

    /**
     * Constructs a new object.
     *
     * @param rtpUri
     *            RTP source stream.
     */
    public RtpPlayer(final URI rtpUri) {
        uri = rtpUri;
        setDaemon(true);
        setName("RTP Player");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        final MediaLocator loc = new MediaLocator(uri.toString());

        try {
            player = javax.media.Manager.createPlayer(loc);
        } catch (NoPlayerException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        listener = new RtpControllerListener();
        player.addControllerListener(listener);
        player.realize();
    }

    /**
     * Stops the current output.
     */
    public void stopPlaying() {
        if (player == null) {
            return;
        }

        if (listener != null) {
            player.removeControllerListener(listener);
            listener = null;
        }

        player.stop();
        player = null;

        interrupt();
    }
}
