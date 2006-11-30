/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
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

import java.io.Serializable;
import java.net.URI;

/**
 * An <code>Application</code> is a set of documents sharing the same
 * application root document.
 *
 * <p>
 * Whenever the user interacts with a document in an application, its
 * application root document is also loaded. The application root document
 * remains loaded while the user is transitioning between other documents in the
 * same application, and it is unloaded when the user transitions to a document
 * that is not in the application. While it is loaded, the application root
 * document's variables are available to the other documents as application
 * variables, and its grammars remain active for the duration of the
 * application.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
public interface Application
        extends Serializable {
    /**
     * Retrieves the system wide unique identifier of this application.
     *
     * @return Identifier of this application.
     */
    String getId();

    /**
     * Retrieves the URI to retrieve the root document from the document server.
     *
     * @return URI to retrieve the boot document
     */
    URI getUri();
}
