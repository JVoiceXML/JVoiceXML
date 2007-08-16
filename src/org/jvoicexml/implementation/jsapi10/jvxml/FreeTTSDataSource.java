/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/jvxml/SystemOutputFactory.java $
 * Version: $LastChangedRevision: 172 $
 * Date:    $LastChangedDate: 2006-12-14 09:35:30 +0100 (Do, 14 Dez 2006) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;

import javax.media.Time;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

/**
 * A {@link javax.media.protocol.DataSource} for FreeTTS.
 *
 * @author Dirk Schnelle
 * @version $Revision: 172 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
final class FreeTTSDataSource extends PullDataSource {
    /** The one and only source stream. */
    private FreeTTSPullSourceStream stream;

    /**
     * Constructs a new object.
     */
    public FreeTTSDataSource() {
        stream = new FreeTTSPullSourceStream();
    }

    /**
     * @return the completed
     */
    public boolean isCompleted() {
        return stream.endOfStream();
    }

    /**
     * {@inheritDoc}
     */
    public PullSourceStream[] getStreams() {
        PullSourceStream[] streams = new PullSourceStream[1];

        streams[0] = stream;

        return streams;
    }

    /**
     * {@inheritDoc}
     */
    public void connect() throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect() {
    }

    /**
     * {@inheritDoc}
     */
    public String getContentType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getControl(final String arg0) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getControls() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Time getDuration() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void start() throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    public void stop() throws IOException {
    }
}
