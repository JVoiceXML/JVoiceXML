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


package org.jvoicexml.implementation.jsapi10;

import java.util.Collection;

import javax.speech.recognition.FinalRuleResult;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultToken;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * JSAPI 1.0 implementation of the result of the recognition process.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class Jsapi10RecognitionResult
        implements RecognitionResult {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(Jsapi10RecognitionResult.class);

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
    public Jsapi10RecognitionResult(final Result res) {
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
     */
    @Override
    public Object getSemanticInterpretation() {
        if (interpretation == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating semantic interpretation...");
            }
            final FinalRuleResult res = (FinalRuleResult) result;
            final String[] tags = res.getTags();
            if ((tags == null) || (tags.length == 0)) {
                return null;
            }
            final Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_6);
            // create a initial scope, do NOT allow access to all java objects
            // check later if sealed initial scope should be used.
            final Scriptable scope = context.initStandardObjects();
            context.evaluateString(scope, "out = new Object();", "expr", 1,
                    null);
            final Collection<String> props = new java.util.ArrayList<String>();
            for (String tag : tags) {
                final String[] pair = tag.split("=");
                // For Talking Java the '=' sign must be escaped. If so:
                // remove it from the tag.
                if (pair[0].endsWith("\\")) {
                    pair[0] = pair[0].substring(0, pair[0].length() - 1);
                }
                final String source;
                if (pair.length < 2) {
                    source = "out = " + pair[0] + ";";
                } else {
                    final String[] nestedctx = pair[0].split("\\.");
                    String seq = "";
                    for (String part : nestedctx) {
                        if (!seq.equals(pair[0])) {
                            if (!seq.isEmpty()) {
                                seq += ".";
                            }
                            seq += part;
                            if (!props.contains("out." + seq)) {
                                final String expr = "out." + seq
                                    + " = new Object();";
                                props.add("out." + seq);
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("setting: '" + expr + "'");
                                }
                                context.evaluateString(scope, expr, "expr", 1,
                                        null);
                            }
                        }
                    }
                    source = "out." + pair[0] + " = " + pair[1] + ";";
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("setting: '" + source + "'");
                }
                context.evaluateString(scope, source, "expr", 1, null);
            }
            interpretation = scope.get("out", scope);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...created semantic interpretation");
            }
        }
        return interpretation;
    }

    /**
     * {@inheritDoc}
     * @since 0.7.3
     */
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(Jsapi10RecognitionResult.class.getCanonicalName());
        str.append('[');
        str.append(getUtterance());
        str.append(',');
        str.append(getSemanticInterpretation());
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
