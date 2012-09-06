/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/TestLog4jHandler.java $
 * Version: $LastChangedRevision: 2870 $
 * Date:    $Date: 2011-12-23 03:38:54 -0600 (vie, 23 dic 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Test cases for {@link Log4jHandler}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2870 $
 * @since 0.7
 */

public final class TestLog4jHandler {

    /**
     * Test method for {@link org.jvoicexml.Log4jHandler#publish(java.util.logging.LogRecord)}.
     */
    @Test
    public void testPublishLogRecord() {
        // Need to create an object to initialize the logging.
        new Log4jHandler();
        Logger logger = Logger.getLogger(TestLog4jHandler.class.getName());
        logger.log(Level.FINE, "test fine");
        logger.log(Level.WARNING, "test warning");
    }

}
