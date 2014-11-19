/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/MarkStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import java.util.Collection;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.SsmlParser;
import org.jvoicexml.interpreter.SsmlParsingStrategy;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Strategy of the FIA to execute a <code>&lt;mark&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Value
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7.4
 */
final class MarkStrategy extends AbstractSsmlParsingStrategy
        implements SsmlParsingStrategy {
    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Mark.ATTRIBUTE_NAMEEXPR);
    }

    /**
     * Constructs a new object.
     */
    MarkStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     */
    public SsmlNode cloneNode(final SsmlParser parser, final DataModel model,
            final SsmlDocument document, final SsmlNode parent,
            final VoiceXmlNode node) throws SemanticError {
        final Mark mark = (Mark) parent.addChild(Mark.TAG_NAME);

        // Copy all attributes into the new node and replace the name
        // attribute by an evaluated nameexpr attribute if applicable.
        final Collection<String> names = node.getAttributeNames();
        for (String name : names) {
            Object value = getAttribute(name);
            if (value != null) {
                if (name.equals(Mark.ATTRIBUTE_NAMEEXPR)) {
                    value = model.evaluateExpression(value.toString(),
                            Object.class);
                    name = Mark.ATTRIBUTE_NAME;
                }
                mark.setAttribute(name, value.toString());
            }
        }

        return mark;
    }
}
