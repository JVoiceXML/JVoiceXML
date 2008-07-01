/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/callmanager/jtapi/JtapiConfiguredApplication.java $
 * Version: $LastChangedRevision: 426 $
 * Date:    $Date: 2007-09-03 21:57:35 +0200 (Mo, 03 Sep 2007) $
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
package org.jvoicexml.callmanager.jtapi;

import org.jvoicexml.callmanager.ConfiguredApplication;

/**
 * TAPI enhanced configured application.
 *
 * @author Dirk Schnelle
 * @version $Revision: 426 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JtapiConfiguredApplication
        extends ConfiguredApplication {
    /** Port number for RTP. */
    private int port;

    /**
     * Retreives the port number.
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port number.
     * @param portNumber the port to set
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }
}
