/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * This class implements the GrammarTransformer interface. An instance
 * of this class is able to transform a SRGS grammar with XML format into an
 * ABNF grammar instance. The mime type of the accepted grammar is
 * application/srgs.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 *
 * @see org.jvoicexml.implementation.grammar.GrammarTransformer
 * @version $Revision$
 */
public final class SrgsXml2SrgsAbnfGrammarTransformer
        extends XsltGrammarTransformer {
    /**
     * Standard constructor to instantiate as much
     * <code>GrammarHandler</code> as you need.
     */
    public SrgsXml2SrgsAbnfGrammarTransformer() {

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
        return GrammarType.SRGS_ABNF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getStylesheetResourceName() {
        return "srgs2abnftransformer.xsl";
    }
}

