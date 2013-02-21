/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/transformer/SrgsXml2SrgsXmlGrammarTransformer.java $
 * Version: $LastChangedRevision: 2897 $
 * Date:    $Date: 2012-01-16 05:21:39 -0600 (lun, 16 ene 2012) $
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
package org.jvoicexml.implementation.grammar.transformer;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * An instance of this class is able to transform a SRGS grammar with XML format
 * into an {@link org.jvoicexml.xml.srgs.SrgsXmlDocument}.
 * The mime type of the accepted grammar is <code>application/srgs+xml</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2897 $
 * @since 0.6
 */
public final class SrgsXml2SrgsXmlGrammarTransformer
        extends IdentGrammarTransformer {
    /**
     * Constructs a new object.
     */
    public SrgsXml2SrgsXmlGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getSourceType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getTargetType() {
        return GrammarType.SRGS_XML;
    }
}
