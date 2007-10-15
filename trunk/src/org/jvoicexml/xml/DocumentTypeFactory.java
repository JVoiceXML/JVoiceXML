/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
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

package org.jvoicexml.xml;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * Factory for document types.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface DocumentTypeFactory {
    /**
     * Factory method to create a document type for the given document.
     * This enables the user of the library to use custom document types.
     *
     * <p>
     * It is guaranteed that there is no document type in the given document
     * if this method is called.
     * </p>
     * @param document the document.
     * @return created document type.
     */
    DocumentType createDocumentType(final Document document);
}
