/*
 * File:    $RCSfile: MappedDocumentRepository.java,v $
 * Version: $Revision: 1.6 $
 * Date:    $Date: 2005/06/10 23:36:44 $
 * Author:  $Author: bytenerd $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy;

import java.net.URI;

import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Simple VoiceXML document repository based on a map mechanism.
 *
 * <p>
 * Documents are stored in the map with their <code>URI</code> as a key.
 * This <code>URI</code> defines it's own scheme <code>jvxmlmap</code>.
 * </p>
 *
 * <p>
 * Main purpose of this repository is to have an easy and lightweight
 * in-memory repository for fast tests.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.6 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface MappedDocumentRepository {
    /**
     * Gets an URI that can be evaluated by this scheme strategy for the
     * given ssp.
     * @param ssp Scheme specific part
     * @return Valid URI for this strategy, <code>null</code> in case of an
     * error.
     */
    URI getUri(final String ssp);

    /**
     * Adds the given document to this repository.
     * @param uri URI as a key for later retrieval.
     * @param document Document to be added.
     */
    void addDocument(final URI uri, final String document);

    /**
     * Adds the given document to this repository.
     * @param uri URI as a key for later retrieval.
     * @param document Document to be added.
     */
    void addDocument(final URI uri, final VoiceXmlDocument document);
}
