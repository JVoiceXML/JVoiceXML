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
package org.jvoicexml.interpreter.grammar;

import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The <code>ExternalGrammarImpl</code> is a simple Implementation
 * of the ExternalGrammar interface.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class ExternalGrammarImpl
        implements ExternalGrammar {

    /**
     * Keeps the grammar type.
     */
    private GrammarType grammartype;

    /**
     * Keeps the grammar content.
     */
    private final String grammarcontent;

    /**
     * Creates an <code>ExternalGrammar</code> with the given parameter.
     *
     * @param type
     *        Type of grammar.
     * @param content
     *        The grammar itself.
     */
    public ExternalGrammarImpl(final GrammarType type, final String content) {
        this.grammartype = type;
        this.grammarcontent = content;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return grammartype;
    }

    /**
     * {@inheritDoc}
     */
    public void setMediaType(final GrammarType type) {
        grammartype = type;
    }

    /**
     * {@inheritDoc}
     */
    public String getContents() {
        return grammarcontent;
    }

}
