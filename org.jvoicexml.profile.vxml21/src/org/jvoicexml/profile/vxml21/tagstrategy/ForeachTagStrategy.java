/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/ForeachTagStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.SsmlParser;
import org.jvoicexml.profile.SsmlParsingStrategy;
import org.jvoicexml.profile.TagStrategyExecutor;
import org.jvoicexml.profile.vxml21.VoiceXml21SsmlParser;
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
 * @version $Revision: 4080 $
 * @since 0.6
 */
final class ForeachTagStrategy extends AbstractTagStrategy
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
        if (arrayName == null) {
            throw new SemanticError("Both item and array must be specified");
        }
        final DataModel model = context.getDataModel();
        array = model.evaluateExpression(arrayName, Object[].class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes(final DataModel model) throws ErrorEvent {
        item = (String) getAttribute(Foreach.ATTRIBUTE_ITEM);
        if ((item == null) && (array == null)) {
            throw new BadFetchError("Both item and array must be specified");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlNode cloneNode(final SsmlParser parser, final DataModel model,
            final SsmlDocument document, final SsmlNode parent,
            final VoiceXmlNode node) throws SemanticError {
        final Foreach foreach = (Foreach) node;
        final VoiceXml21SsmlParser vxml21parser = (VoiceXml21SsmlParser) parser;
        for (int i = 0; i < array.length; i++) {
            final Object value = array[i];
            model.updateVariable(item, value);
            vxml21parser.cloneNode(document, parent, foreach);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem formItem,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final Foreach foreach = (Foreach) node;
        final DataModel model = context.getDataModel();
        // TODO This results in a fragmentation of multiple SSML documents
        // that will be passed to the implementation platform
        for (int i = 0; i < array.length; i++) {
            final Object value = array[i];
            model.updateVariable(item, value);
            final TagStrategyExecutor executor = fia.getTagStrategyExecutor();
            executor.executeChildNodes(context, interpreter, fia, formItem,
                    foreach);
        }
    }
}
