/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/RuleGrammarImplementation.java $
 * Version: $LastChangedRevision: 213 $
 * Date:    $Date: 2007-02-08 17:44:26 +0100 (Do, 08 Feb 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Implementation of a SRGS XML grammar.
 *
 * @author Dirk Schnelle
 * @version $Revision: 213 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public final class SrgsXmlGrammarImplementation
    implements GrammarImplementation<SrgsXmlDocument> {
    /** The encapsulated grammar. */
    private final SrgsXmlDocument document;

    /**
     * Constructs a new object.
     * @param doc the grammar.
     */
    public SrgsXmlGrammarImplementation(final SrgsXmlDocument doc) {
        document = doc;
    }

    /**
     * {@inheritDoc}
     */
    public SrgsXmlDocument getGrammar() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "dummy";
    }
}
