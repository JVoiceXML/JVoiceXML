/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.documentserver;

import org.apache.log4j.Logger;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 * Observer for the character set detection.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
class JVoiceXmlCharsetDetectionObserver implements nsICharsetDetectionObserver {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlDocumentServer.class);
    /** The detected character set. */
    private String charset;

    /**
     * Retrieves the character set.
     * @return the character set
     */
    public String getCharset() {
        return charset;
    }

    /**
     * {@inheritDoc}
     * {@inheritDoc}
     */
    @Override
    public void Notify(final String set) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("probably charset '" + set + "'");
        }
        charset = set;
    }
}
