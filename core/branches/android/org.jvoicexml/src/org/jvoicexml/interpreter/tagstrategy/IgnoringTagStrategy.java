/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/IgnoringTagStrategy.java $
 * Version: $LastChangedRevision: 2655 $
 * Date:    $Date: 2011-05-18 03:13:16 -0500 (mié, 18 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Collection;

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * A {@link TagStrategy} that simply does nothing, but ignoring the
 * current node.
 * @author Dirk Schnelle_Walka
 * @version $Revision: 2655 $
 * @since 0.7
 */
final class IgnoringTagStrategy implements TagStrategy {

    /**
     * {@inheritDoc}
     */
    public void dumpNode(final VoiceXmlNode node) {
    }

    /**
     * {@inheritDoc}
     */
    public void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError {
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,
            final FormItem item, final VoiceXmlNode node)
        throws JVoiceXMLEvent {
    }

    /**
     * {@inheritDoc}
     */
    public void getAttributes(final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia,
            final VoiceXmlNode node) {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy newInstance() {
        return new IgnoringTagStrategy();
    }

    /**
     * {@inheritDoc}
     */
    public void validateAttributes() throws ErrorEvent {
    }

    /**
     * {@inheritDoc}
     */
    public void executeLocal(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node)
       throws JVoiceXMLEvent {
    }
}
