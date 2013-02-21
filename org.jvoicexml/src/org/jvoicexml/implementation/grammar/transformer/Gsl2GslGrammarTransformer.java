/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/grammar/transformer/Gsl2GslGrammarTransformer.java $
 * Version: $LastChangedRevision: 2670 $
 * Date:    $Date: 2011-05-19 08:19:45 -0500 (jue, 19 may 2011) $
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
 * An instance of this class is able to transform a GSL grammar document
 * into a GSL grammar implementation.
 * The mime type of the accepted grammar is
 * <code>application/x-nuance-gsl</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2670 $
 * @since 0.7.5
 */
public final class Gsl2GslGrammarTransformer
        extends IdentGrammarTransformer {
    /**
     * {@inheritDoc}
     */
    public GrammarType getSourceType() {
        return GrammarType.GSL;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getTargetType() {
        return GrammarType.GSL;
    }
}
