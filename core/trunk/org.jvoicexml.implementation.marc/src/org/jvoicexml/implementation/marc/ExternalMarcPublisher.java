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
package org.jvoicexml.implementation.marc;

import java.io.IOException;

/**
 * MARC allows only one client at a time. This one and only client is
 * already busy with the {@link MarcSynthesizedOutput}.
 * {@link ExternalMarcPublisher}s can be used to generate other 
 * <a href="http://wiki.mindmakers.org/projects:BML:main">Behavior Markup
 * Language</a> messages that should be sento MARC.
 * <p>
 * Messages to MARC can only be sent when a session is active. This means
 * between calls to {@link #start()} and {@link #stop()}.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 */
public interface ExternalMarcPublisher {
    /**
     * Starts the external publisher. This method is called when the MARC
     * output is activated.
     * @exception IOException
     *            error starting the external publisher.
     */
    void start() throws IOException;

    /**
     * Sets the MARC client that can be used to communicate with MARC.
     * @param client the client
     */
    void setMarcClient(final MarcClient client);

    /**
     * Stops the external publisher. This method is called when the MARC
     * output is passivated.
     * @exception IOException
     *            error stopping the external publisher.
     */
    void stop() throws IOException;
}
