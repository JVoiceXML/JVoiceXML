/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml.implementation.jsapi20/src/org/jvoicexml/implementation/jsapi20/RuleGrammarImplementation.java $
 * Version: $LastChangedRevision: 1050 $
 * Date:    $Date: 2008-09-19 11:54:22 -0700 (Fri, 19 Sep 2008) $
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

package org.jvoicexml.zanzibar.jvoicexml.impl;

import java.net.URI;

import javax.speech.recognition.RuleGrammar;


import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Implementation of a JSGF grammar.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1050 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public final class RuleGrammarImplementation
    implements org.jvoicexml.implementation.GrammarImplementation<RuleGrammar> {
    /** The encapsulated grammar. */
    private final RuleGrammar grammar;

    /**
     * Constructs a new object.
     * @param ruleGrammar the grammar.
     */
    public RuleGrammarImplementation(final RuleGrammar ruleGrammar) {
        grammar = ruleGrammar;
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar getGrammar() {
        return grammar;
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
        //return grammar.getReference();
        return grammar.getName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean accepts(final String utterance) {
        // TODO Auto-generated method stub
        return true;
    }

	public boolean accepts(RecognitionResult result) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean equals(org.jvoicexml.implementation.GrammarImplementation<RuleGrammar> other) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public ModeType getModeType() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
	public RuleGrammar getGrammarDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getURI() {
		// TODO Auto-generated method stub
		return null;
	}


}
