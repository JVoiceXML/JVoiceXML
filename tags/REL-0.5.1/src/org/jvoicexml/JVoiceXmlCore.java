/*
 * File:    $RCSfile: JVoiceXmlCore.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/04/19 11:03:16 $
 * Author:  $Author: schnelle $
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

import org.jvoicexml.documentserver.DocumentServer;
import org.jvoicexml.interpreter.GrammarProcessor;

/**
 * Internal view on the main class.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4.1
 */
public interface JVoiceXmlCore
        extends JVoiceXml {
    /**
     * Retrieves a reference to the document server.
     *
     * <p>
     * Unsupported for remote access.
     * </p>
     *
     * @return The doucment server, <code>null</code> if accessed remotely.
     */
    DocumentServer getDocumentServer();

    /**
     * Retrieves a reference to the grammar processor.
     *
     * <p>
     * Unsupported for remote access.
     * </p>
     *
     * @return The grammar processor.K
     */
    GrammarProcessor getGrammarProcessor();

    /**
     * Retrieves a reference to the application registry.
     *
     * @return The application registry.
     */
    ApplicationRegistry getApplicationRegistry();
}
