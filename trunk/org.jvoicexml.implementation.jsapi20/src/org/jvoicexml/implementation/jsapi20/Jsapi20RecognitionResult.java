/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import javax.speech.recognition.FinalResult;
import javax.speech.recognition.RecognizerProperties;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultToken;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.srgs.ModeType;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * JSAPI 20 implementation of the result of the recognition process.
 * 
 * @author Dirk Schnelle-Walka
 * @author Markus Baumgart
 * @version $Revision$
 * @since 0.6
 */
public final class Jsapi20RecognitionResult implements RecognitionResult {
    /** The Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(Jsapi20RecognitionResult.class);

    /** The semantic interpretation of the utterance. */
    private Object interpretation;

    /** The result returned by the recognizer. */
    private final Result result;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /**
     * Constructs a new object.
     * 
     * @param res
     *            The result returned by the recognizer.
     */
    public Jsapi20RecognitionResult(final Result res) {
        result = res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUtterance() {
        if (result == null) {
            return null;
        }

        if (!isAccepted()) {
            return null;
        }

        // construct the utterance from the tokens
        final ResultToken[] tokens = result.getBestTokens();
        final StringBuilder utterance = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            utterance.append(tokens[i].getText());
            utterance.append(' ');
        }

        return utterance.toString().trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccepted() {
        if (result == null) {
            return false;
        }

        return result.getResultState() == Result.ACCEPTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMark(final String mark) {
        markname = mark;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMark() {
        return markname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getConfidence() {
        final FinalResult finalResult = (FinalResult) result;

        // map the actual confidence in javax.speech's range
        // [MIN_CONFIDENCE; MAX_CONFIDENCE](int) to a new Float-value in [0; 1]
        // e.g. be MAX_CONFIDENCE = 20; MIN_CONFIDENCE = -10;
        // then, a value of 2 from the FinalResult (working in [-10; 20])
        // should be mapped to 0.4f (in [0; 1])
        // [because +2(int) is 2/5th of the complete RecognizerProperties'
        // range as is 0.4f in [0; 1]]

        // get the whole range (in the example above => 20 - -10 = 30;
        final float range = RecognizerProperties.MAX_CONFIDENCE
                - RecognizerProperties.MIN_CONFIDENCE;

        // set the value and shift it (again, with the sample above: set the
        // value from +2 in [-10; 20] to +12 [0; 30] and divide by range (30)
        final float confidence = finalResult.getConfidenceLevel();
        if (confidence == RecognizerProperties.UNKNOWN_CONFIDENCE) {
            return 1.0f;
        }
        return (confidence - RecognizerProperties.MIN_CONFIDENCE) / range;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float[] getWordsConfidence() {
        final ResultToken[] rt = result.getBestTokens();
        final float[] wordsConfidence = new float[rt.length];
        for (int i = 0; i < rt.length; ++i) {
            wordsConfidence[i] = rt[i].getConfidenceLevel();
        }
        return wordsConfidence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getWords() {
        final ResultToken[] tokens = result.getBestTokens();
        final String[] words = new String[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            words[i] = tokens[i].getText();
        }
        return words;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModeType getMode() {
        return ModeType.valueOf("VOICE");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSemanticInterpretation() {
        if (interpretation == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating semantic interpretation...");
            }
            final FinalResult finalResult = (FinalResult) result;
            final Object[] objecttags = finalResult.getTags(0);
            if ((objecttags == null) || (objecttags.length == 0)) {
                return null;
            }
            final String[] tags = toString(objecttags);
            final Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_6);

            final Scriptable scope = context.initStandardObjects();
            context.evaluateString(scope, "var out = new Object();", "expr", 1,
                    null);
            for (String tag : tags) {
                if (tag.trim().endsWith(";")) {
                    context.evaluateString(scope, tag, "expr", 1, null);
                } else {
                    context.evaluateString(scope, "var out = '" + tag + "';",
                            "expr", 1, null);
                }
            }
            interpretation = scope.get("out", scope);
            if (interpretation instanceof ScriptableObject) {
                final String json = ScriptingEngine
                        .toJSON((ScriptableObject) interpretation);
                LOGGER.info("created semantic interpretation out=" + json);
            } else {
                LOGGER.info("created semantic interpretation '"
                        + interpretation + "'");
            }
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
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(getClass().getCanonicalName());
        str.append('[');
        str.append(result);
        str.append(']');
        return str.toString();
    }
}
