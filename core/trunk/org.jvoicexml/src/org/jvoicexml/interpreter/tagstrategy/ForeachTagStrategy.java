/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.tagstrategy;

import java.util.Collection;

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.SsmlParser;
import org.jvoicexml.interpreter.SsmlParsingStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Foreach;

/**
 * Strategy of the FIA to execute a <code>&lt;foreach&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Foreach
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class ForeachTagStrategy
        extends AbstractSsmlParsingStrategy
        implements SsmlParsingStrategy {
    /** The ECMA script array to iteratate over. */
    private Object[] array;

    /** Name of the variable that stores each item. */
    private String item;

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError {
        super.evalAttributes(context);
        final String arrayName = (String) getAttribute(Foreach.ATTRIBUTE_ARRAY);
        final ScriptingEngine scripting = context.getScriptingEngine();
        array = scripting.getVariableAsArray(arrayName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes() throws ErrorEvent {
        item = (String) getAttribute(Foreach.ATTRIBUTE_ITEM);

        if ((item == null) || (array == null)) {
            throw new BadFetchError("Both item and array must be specified");
        }
    }

    /**
     * {@inheritDoc}
     */
    public SsmlNode cloneNode(final SsmlParser parser,
            final ScriptingEngine scripting, final SsmlDocument document,
            final SsmlNode parent, final VoiceXmlNode node)
        throws SemanticError {
        final Foreach foreach = (Foreach) node;

        for (int i = 0; i < array.length; i++) {
            final Object value = array[i];
            scripting.setVariable(item, value);

            parser.cloneNode(document, parent, foreach);
        }

        return null;
    }
}
