/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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


package org.jvoicexml.implementation.mobicents;

import javax.speech.recognition.FinalRuleResult;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultToken;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Mobicents 1.0 implementation of the result of the recognition process.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class MobicentsRecognitionResult
        implements RecognitionResult {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MobicentsRecognitionResult.class);

    /** The semantic interpretation of the utterance. */
    private Object interpretation;

    /** The result returned by the recognizer. */
    private final Result result;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /**
     * Constructs a new object.
     * @param res The result returned by the recognizer.
     */
    public MobicentsRecognitionResult(final Result res) {
        result = res;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        if (result == null) {
            return null;
        }

        if (!isAccepted()) {
            return null;
        }

        // Create the utterance from the best tokens.
        final ResultToken[] tokens = result.getBestTokens();
        final StringBuilder utterance = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            utterance.append(tokens[i].getSpokenText());
            utterance.append(' ');
        }

        return utterance.toString().trim();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        if (result == null) {
            return false;
        }

        return result.getResultState() == Result.ACCEPTED;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        if (result == null) {
            return false;
        }

        return result.getResultState() == Result.REJECTED;
    }

    /**
     * {@inheritDoc}
     */
    public void setMark(final String mark) {
        markname = mark;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return markname;
    }

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        /** @todo Retrieve the confidence level. */
        return 1.0f;
    }

    /**
     * {@inheritDoc}
     */
    public float[] getWordsConfidence() {
        /** @todo Retrieve the confidence level of each word. */
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
    public String[] getWords() {
        final ResultToken[] tokens = result.getBestTokens();
        final String[] words = new String[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            words[i] = tokens[i].getWrittenText();
        }
        return words;
    }

    /**
     * {@inheritDoc}
     */
    public ModeType getMode() {
        return ModeType.VOICE;
    }

    /**
     * {@inheritDoc}
     * @throws SemanticError 
     */
    @Override
    public Object getSemanticInterpretation(final DataModel model) throws SemanticError {
        if (interpretation == null) {
            final FinalRuleResult finalResult = (FinalRuleResult) result;
            final Object[] objecttags = finalResult.getTags();
            if ((objecttags == null) || (objecttags.length == 0)) {
                return null;
            }
            final String[] tags = toString(objecttags);
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

    /**
     * Converts given object tags into a string representation.
     * 
     * @param objecttags
     *            the object tags to convert.
     * @return tags as string
     * @since 0.7.7
     */
    private String[] toString(final Object[] objecttags) {
        final String[] tags = new String[objecttags.length];
        for (int i = 0; i < objecttags.length; i++) {
            final Object o = objecttags[i];
            if (o != null) {
                tags[i] = o.toString();
            }
        }
        return tags;
    }

    /**
     * {@inheritDoc}
     * @since 0.7.3
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(MobicentsRecognitionResult.class.getCanonicalName());
        str.append('[');
        str.append(getUtterance());
        str.append(',');
        str.append(interpretation);
        str.append(',');
        str.append(isAccepted());
        str.append(',');
        str.append(getConfidence());
        str.append(',');
        str.append(getMode());
        str.append(',');
        str.append(getMark());
        str.append(']');
        return str.toString();
    }
}
