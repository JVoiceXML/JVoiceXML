/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.jvoicexml.impl;


import java.net.URI;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

public final class JSGFGrammarImplementation
    implements GrammarImplementation<String> {
    /** The encapsulated grammar. */
    private final String document;

    /**
     * Constructs a new object.
     * @param doc the grammar.
     */
    public JSGFGrammarImplementation(final String doc) {
        document = doc;
    }

    /**
     * {@inheritDoc}
     */
    public String getGrammar() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getMediaType() {
        return GrammarType.JSGF;
    }

    /**
     * {@inheritDoc}
     */
    public boolean accepts(final String utterance) {
        // TODO Auto-generated method stub
        return true;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

	public boolean accepts(RecognitionResult result) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean equals(GrammarImplementation<String> other) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public ModeType getModeType() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
	public String getGrammarDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getURI() {
		// TODO Auto-generated method stub
		return null;
	}
}