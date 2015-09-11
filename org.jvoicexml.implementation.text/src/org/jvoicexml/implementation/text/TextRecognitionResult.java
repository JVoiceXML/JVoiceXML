/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.text;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * A recognition result for text based input. Since textual input is less error
 * prone than pure speech recognition we will always accept the result with full
 * confidence.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class TextRecognitionResult implements RecognitionResult {
    /** The semantic interpretation of the utterance. */
    private Object interpretation;

    /** The received utterance. */
    private final String utterance;

    /** The last reached mark. */
    private String mark;

    /** Reference to the grammarChecker Object. */
    private final GrammarChecker grammarChecker;

    /** The Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextRecognitionResult.class);

    /**
     * Constructs a new object.
     * 
     * @param text
     *            the received text.
     * @param checker
     *            the checker for the grammar.
     */
    public TextRecognitionResult(final String text,
            final GrammarChecker checker) {
        utterance = text;
        grammarChecker = checker;
    }

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        return 1.0f;
    }

    /**
     * {@inheritDoc}
     */
    public ModeType getMode() {
        return ModeType.VOICE;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        if (grammarChecker == null) {
            return false;
        }

        final String[] utterances = utterance.split(" ");
        return grammarChecker.isValid(utterances);
    }

    /**
     * {@inheritDoc}
     */
    public void setMark(final String newMark) {
        mark = newMark;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return mark;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getWords() {
        if (utterance == null) {
            return null;
        }
        return utterance.split(" ");
    }

    /**
     * {@inheritDoc}
     */
    public float[] getWordsConfidence() {
        final String[] words = getWords();
        final float[] confidences = new float[words.length];
        for (int i = 0; i < confidences.length; i++) {
            confidences[i] = 1.0f;
        }
        return confidences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSemanticInterpretation(final DataModel model)
            throws SemanticError {
        if (interpretation == null) {
            if (grammarChecker == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("there is no grammar graph"
                            + " cannot get semantic interpretation");
                }
                return null;
            }

            final String[] tags = grammarChecker.getInterpretation();
            if ((tags == null) || (tags.length == 0)) {
                return null;
            }
            for (int i = 0; i < tags.length; i++) {
                if (tags[i].startsWith("out.")) {
                    tags[i] = tags[i].substring(tags[i].indexOf('.') + 1);
                }
            }

            model.createVariable("out");
            for (String tag : tags) {
                if (tag.trim().endsWith(";")) {
                    model.evaluateExpression(tag, Object.class);
                } else {
                    model.updateVariable("out", tag);
                }
            }
            interpretation = model.readVariable("out", Object.class);
            final String log = model.toString(interpretation);
            LOGGER.info("created semantic interpretation '" + log + "'");
        }
        return interpretation;
    }
}
