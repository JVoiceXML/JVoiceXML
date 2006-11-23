/*
 * File:    $RCSfile: LoggingConfiguration.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2005/10/27 01:35:53 $
 * Author:  $Author: shaun_c $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.logging;


/**
 * Wrapper for easy access to configuration properties for logging.
 *
 * @author Shaun Childers
 * @author Dirk Schnelle
 *
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @see org.jvoicexml.documentserver.JVoiceXmlDocumentServer
 */
public final class LoggingConfiguration {
    /** Configuration key. */
    public static final String CONFIG_KEY = "logging";

    /** Logger class string representation. */
    private Class logger = null;

    /**
     * Constructs a new object.
     */
    public LoggingConfiguration() {
    }

    /**
     * Sets the name of the logger class.
     * @param name Name of the logger class.
     */
    public void setLogger(final String name) {
        try {
            logger = Class.forName(name);
        } catch (Exception e) {
            // Catch any exceptions and just return null
            logger = null;
        }
    }

    /**
     * Retrieves the logging implementation class configured.
     *
     * @return The <code>Logger</code> to use.
     */
    public Class getLoggingImplementation() {
        return logger;
    }
}
