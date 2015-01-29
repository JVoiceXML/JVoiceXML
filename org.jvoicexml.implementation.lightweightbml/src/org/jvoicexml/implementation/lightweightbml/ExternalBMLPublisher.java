/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.lightweightbml;

import java.io.IOException;

/**
 * TalkingHead allows only one client at a time. This one and only client is
 * already busy with the {@link BMLSynthesizedOutput}.
 * {@link ExternalBMLPublisher}s can be used to generate other 
 * LightweightBML messages that should be sento TalkingHead.
 * Messages to TalkingHead can only be sent  between calls to {@link #start()} and
 * {@link #stop()}.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public interface ExternalBMLPublisher {
    /**
     * Starts the external publisher. This method is called when the BML
     * output is activated.
     * @exception IOException
     *            error starting the external publisher.
     */
    void start() throws IOException;

    /**
     * Sets the BML client that can be used to communicate with an Avatar.
     * @param client the client
     */
    void setBMLClient(final BMLClient client);

    /**
     * Stops the external publisher. This method is called when the BML
     * output is passivated.
     * @exception IOException
     *            error stopping the external publisher.
     */
    void stop() throws IOException;
}
