/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;

/**
 * JNDI support for remote access to the VoiceXML interpreter.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5.5
 */
public interface JndiSupport {
    /**
     * Starts the service.
     * @exception IOException error starting the JNDI support
     */
    void startup() throws IOException;

    /**
     * Sets the reference to the interpreter. This method is called when
     * the JNDI support is started by the core interpreter.
     * @param jvxml the interpreter.
     */
    void setJVoiceXml(final JVoiceXml jvxml);

    /**
     * Stops the service.
     */
    void shutdown();

}
