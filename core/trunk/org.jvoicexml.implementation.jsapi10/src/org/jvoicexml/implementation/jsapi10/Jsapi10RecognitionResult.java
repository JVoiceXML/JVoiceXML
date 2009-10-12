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

import javax.speech.recognition.FinalRuleResult;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultToken;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

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
    private ScriptableObject interpretation;

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
        return new float[0];
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
    public ScriptableObject getSemanticInterpretation() {
        if (interpretation == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating semantic interpretation...");
            }
            final FinalRuleResult res = (FinalRuleResult) result;
            final String[] tags = res.getTags();
            final Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_6);
            // create a initial scope, do NOT allow access to all java objects
            // check later if sealed initial scope should be used.
            final Scriptable scope = context.initStandardObjects();
            context.evaluateString(scope, "out = new Object();", "expr", 1,
                    null);
            for (String tag : tags) {
                final String[] pair = tag.split("=");
                final String[] nestedctx = pair[0].split("\\.");
                String seq = "";
                for (String part : nestedctx) {
                    if (!seq.equals(pair[0])) {
                        if (!seq.isEmpty()) {
                            seq += ".";
                        }
                        seq += part;
                        context.evaluateString(scope, "out." + seq
                                + " = new Object();", "expr", 1, null);
                    }
                }
                final String source;
                if (pair.length < 2) {
                    source = "out." + pair[0] + "=true;";
                } else {
                    source = "out." + pair[0] + "=" + pair[1] + ";";
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("setting: '" + source + "'");
                }
                context.evaluateString(scope, source, "expr", 1, null);
            }
            interpretation = (ScriptableObject) scope.get("out", scope);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...created semantic interpretation");
            }
        }
        return interpretation;
    }
}
