/*
 * File:    $RCSfile: MappedDocumentStrategy.java,v $
 * Version: $Revision: 1.5 $
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;

/**
 * Scheme strategy for the <code>MapedDocumentRepository</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.5 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class MappedDocumentStrategy
        implements SchemeStrategy {
    /** Scheme for which this scheme strategy is repsonsible. */
    public static final String SCHEME_NAME = "jvxmlmap";

    /**
     * Construct a new object.
     */
    public MappedDocumentStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public String getScheme() {
        return SCHEME_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(final URI uri)
            throws BadFetchError {
        final DocumentMap repository =
                DocumentMap.getInstance();

        final String document = repository.getDocument(uri);
        if (document == null) {
            return null;
        }

        try {
            return new ByteArrayInputStream(document.getBytes("UTF-8"));
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
    }
}
