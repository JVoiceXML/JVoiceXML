/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/transformer/SrgsXml2JsgfGrammarTransformer.java $
 * Version: $LastChangedRevision: 2592 $
 * Date:    $Date: 2011-02-17 22:01:47 -0600 (jue, 17 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
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
 * An instance of this class is able to transform a SRGS grammar with XML format
 * into RuleGrammar instance.<br>
 * The mime type of the accepted grammar is application/srgs+xml.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2592 $
 */
public final class SrgsXml2JsgfGrammarTransformer
        extends XsltGrammarTransformer {
    /**
     * Standard constructor to instantiate as much
     * <code>GrammarTransformer</code> as you need.
     */
    public SrgsXml2JsgfGrammarTransformer() {
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
        return GrammarType.JSGF;
    }

    @Override
    protected String getStylesheetResourceName() {
        return "srgs2jsgftransformer.xsl";
    }
}
