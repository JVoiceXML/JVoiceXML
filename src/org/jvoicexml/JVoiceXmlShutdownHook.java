/*
 * File:    $RCSfile: JVoiceXmlShutdownHook.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import org.apache.log4j.Logger;

/**
 * A shutdown hook, to safely terminate the main process.
 *
 * @see org.jvoicexml.JVoiceXmlMain
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
final class JVoiceXmlShutdownHook
        implements Runnable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlShutdownHook.class);

    /** Reference to the main process. */
    private final JVoiceXml jvxml;

    /**
     * Constructs a new object.
     * @param jvoicexml Reference to the main process.
     */
    public JVoiceXmlShutdownHook(final JVoiceXml jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("JVoiceXML interrupted! Shutting down...");
        }

        jvxml.shutdown();
    }
}
