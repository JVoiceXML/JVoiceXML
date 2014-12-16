/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/ExternalGrammarDocument.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver;

import java.net.URI;

import org.jvoicexml.GrammarDocument;

/**
 * A storage for documents for ASR and TTS that are generated while executing
 * a session. The main task of this component is to manage a set of URIs
 * associated with these documents.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class DocumentStorage {

    public DocumentStorage() {
    }

    /**
     * Adds the given grammar document to the documents store and retrieves
     * the URI to access it externally.
     * @param sessionId the id of the initiating session
     * @param document the document to add
     * @return the URI to access the document
     */
    public URI addGrammarDocument(final String sessionId,
            final GrammarDocument document) {
        return null;
    }

    /**
     * Clears all documents associated with the given session.
     * @param sessionId the id of the session
     */
    public void clear(final String sessionId) {
    }
}
