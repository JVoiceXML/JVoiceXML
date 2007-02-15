/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.interpreter.GrammarProcessor;

/**
 * View of the interpreter on the main class.
 *
 * <p>
 * While {@link JVoiceXml} defines the view of {@link RemoteClient}s on the
 * main class, the interpreter needs more functionality.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
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
     * @return The document server, <code>null</code> if accessed remotely.
     */
    DocumentServer getDocumentServer();

    /**
     * Retrieves a reference to the grammar processor.
     * @return The grammar processor.
     */
    GrammarProcessor getGrammarProcessor();
}
