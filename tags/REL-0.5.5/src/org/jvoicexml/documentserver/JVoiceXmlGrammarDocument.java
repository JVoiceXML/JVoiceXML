/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Basic implementation of a {@link GrammarDocument}.
 *
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public final class JVoiceXmlGrammarDocument
        implements GrammarDocument {

    /** The grammar type. */
    private GrammarType type;

    /** The grammar document. */
    private final String document;

    /**
     * Creates a new object.
     *
     * @param content
     *        The grammar itself.
     */
    public JVoiceXmlGrammarDocument(final String content) {
        this.document = content;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public void setMediaType(final GrammarType grammartype) {
        type = grammartype;
    }

    /**
     * {@inheritDoc}
     */
    public String getDocument() {
        return document;
    }

}
