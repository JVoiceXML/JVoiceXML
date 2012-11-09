/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/startup/Startup.java $
 * Version: $LastChangedRevision: 2869 $
 * Date:    $Date: 2011-12-23 03:31:52 -0600 (vie, 23 dic 2011) $
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

package org.jvoicexml.startup;

import org.jvoicexml.JVoiceXmlMain;

/**
 * Startup the JVoiceXML voice browser.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2869 $
 * @since 0.7
 */
public final class Startup {
    /**
     * Do not make instances.
     */
    private Startup() {
    }

    /**
     * The main method, which starts the interpreter.
     *
     * @param args Command line arguments. None expected.
     *
     * @since 0.4
     */
    public static void main(final String[] args) {
        final JVoiceXmlMain jvxml = new JVoiceXmlMain();

        // Start the interpreter as a thread.
        jvxml.start();

        // Wait until the interpreter thread terminates.
        jvxml.waitShutdownComplete();

        System.exit(0);
    }
}
