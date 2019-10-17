/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

/**
 * Factory for the default grammar types.
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class JVoiceXmlGrammarTypeFactory implements GrammarTypeFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarType getGrammarType(final String attribute) {
        if (attribute == null) {
            return null;
        }
        if (GrammarType.JSGF.getType().equals(attribute)) {
            return GrammarType.JSGF;
        }
        if (GrammarType.SRGS_ABNF.getType().equals(attribute)) {
            return GrammarType.SRGS_ABNF;
        }
        if (GrammarType.SRGS_XML.getType().equals(attribute)) {
            return GrammarType.SRGS_XML;
        }
        if (GrammarType.GSL.getType().equals(attribute)) {
            return GrammarType.GSL;
        }
        if (GrammarType.GSL_BINARY.getType().equals(attribute)) {
            return GrammarType.GSL_BINARY;
        }
        return null;
    }

}
