/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.event;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.variables.ApplicationShadowVarContainer;

/**
 * Strategy to process a recognition event coming from the implementation
 * platform.
 *
 * <p>
 * A {@link InputItemRecognitionEventStrategy} may be responsible to handle events
 * for an input item.
 * Grammars specified within an input item produce a field-level result which
 * may fill only the particular input item in which they are contained. 
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.2
 * @see org.jvoicexml.ImplementationPlatform
 */
final class InputItemRecognitionEventStrategy
        extends AbstractInputItemEventStrategy<FieldFormItem>
        implements EventStrategyPrototype {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(InputItemRecognitionEventStrategy.class);

    /**
     * Constructs a new object.
     */
    InputItemRecognitionEventStrategy() {
    }

    /**
     * Constructs a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The VoiceXML interpreter.
     * @param algorithm
     *        The FIA.
     * @param formItem
     *        The current form item.
     */
    public InputItemRecognitionEventStrategy(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm algorithm,
            final FormItem formItem) {
        super(ctx, interpreter, algorithm, formItem,
                RecognitionEvent.EVENT_TYPE);
    }

    /**
     * Sets the result in the application shadow variable.
     * @param result the current recognition result.
     * @throws SemanticError
     *         Error creating the shadow variable.
     * @since 0.6
     */
    private void setApplicationLastResult(final RecognitionResult result)
        throws SemanticError {
        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        final ScriptingEngine scripting = context.getScriptingEngine();
        if (scripting.isVariableDefined(
                ApplicationShadowVarContainer.VARIABLE_NAME)) {
            final ApplicationShadowVarContainer application =
                (ApplicationShadowVarContainer)
                scripting.eval(ApplicationShadowVarContainer.VARIABLE_NAME);

            application.setRecognitionResult(result);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handleEvent(final FieldFormItem field,
            final JVoiceXMLEvent event) throws JVoiceXMLEvent {
        final RecognitionEvent recognitionEvent = (RecognitionEvent) event;
        final RecognitionResult result =
                recognitionEvent.getRecognitionResult();

        setApplicationLastResult(result);
        
        //TODO under dev.
        //check if a (correct) confidencelevel was specified
        VoiceXmlInterpreterContext ctx = getVoiceXmlInterpreterContext();
        String strConfLevel = ctx.getProperty("confidencelevel");
        Float confLevel = null;
        if (strConfLevel != null) {
            try {
                confLevel = Float.parseFloat(strConfLevel);
            } catch (Exception e) {
                throw new SemanticError(
                        "The <property>'s confidencelevel could not be parsed.");
            }
        }
        //if there was no confidencelevel set, refer to the default of 0.5
        //see http://www.w3.org/TR/voicexml20/#dml6.3.2
        if (confLevel == null)
            confLevel = 0.5f;
        
        if (result.getConfidence() < confLevel) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result not accepted: " +
                                "confidence was too low: " +
                                "expected: " + confLevel + ", " +
                                "actual: " + result.getConfidence());
            }
            return false;
        }
        
        if (!result.isAccepted()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result not accepted");
            }

            return false;
        }
        
        final String markname = result.getMark();
        if (markname != null) {
            field.setMarkname(markname);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventStrategy newInstance(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia,
            final FormItem item) {
        return new InputItemRecognitionEventStrategy(ctx, interpreter, fia,
                item);
    }
}
