/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/transformer/SrgsAbnf2SrgsAbnfGrammarTransformer.java $
 * Version: $LastChangedRevision: 2592 $
 * Date:    $Date: 2011-02-17 22:01:47 -0600 (jue, 17 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
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
package org.jvoicexml.implementation.grammar.transformer;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * An instance of this class is able to transform a SRGS grammar with XML format
 * into an {@link org.jvoicexml.xml.srgs.SrgsXmlDocument}.
 * The mime type of the accepted grammar is <code>application/srgs+xml</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2592 $
 * @since 0.6
 */
public final class SrgsAbnf2SrgsAbnfGrammarTransformer
        extends IdentGrammarTransformer {
    /**
     * Constructs a new object.
     */
    public SrgsAbnf2SrgsAbnfGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSourceType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getTargetType() {
        return GrammarType.SRGS_XML;
    }
}
