/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

import javax.speech.recognition.FinalResult;
import javax.speech.recognition.RecognizerProperties;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultToken;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * JSAPI 20 implementation of the result of the recognition process.
 *
 * @author Dirk Schnelle-Walka
 * @author Markus Baumgart
 * @version $Revision$
 * @since 0.6
 */
public final class Jsapi20RecognitionResult
        implements RecognitionResult {
    /**The Logger for this class.*/
    private static final Logger LOGGER =
        Logger.getLogger(Jsapi20RecognitionResult.class);
    
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
    public Jsapi20RecognitionResult(final Result res) {
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
        final FinalResult finalResult = (FinalResult) result;
        
        //map the actual confidence in javax.speech's range [MIN_CONFIDENCE; MAX_CONFIDENCE](int) to a new Float-value in [0; 1] 
        // e.g. be MAX_CONFIDENCE = 20; MIN_CONFIDENCE = -10;
        // then, a value of 2 from the FinalResult (working in [-10; 20]) should be mapped to 0.4f (in [0; 1])
        // [because +2(int) is 2/5th of the complete RecognizerProperties' range as is 0.4f in [0; 1]]
        
        //get the whole range (in the example above => 20 - -10 = 30;
        float range = RecognizerProperties.MAX_CONFIDENCE - RecognizerProperties.MIN_CONFIDENCE;
        
        //set the value and shift it (again, with the sample above: set the value from +2 in [-10; 20] to +12 [0; 30] and divide by range (30)
        float confidence = (finalResult.getConfidenceLevel() - RecognizerProperties.MIN_CONFIDENCE) / range;
        return (confidence);
    }

    /**
     * {@inheritDoc}
     */
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
            final String[] tags = (String[])((FinalResult)result).getTags(0);
            if ((tags == null) || (tags.length == 0)) {
                return null;
            }
            for (int i = 0; i < tags.length; i++) {
                if (tags[i].startsWith("out.")) {
                    tags[i] = tags[i].substring(tags[i].indexOf('.') + 1);
                }
            }
                           
            final Context context = Context.enter();
            context.setLanguageVersion(Context.VERSION_1_6);
            
            final Scriptable scope = context.initStandardObjects();
            context.evaluateString(scope, "out = new Object();", "expr", 1, null);
            final Collection<String> props = new java.util.ArrayList<String>();                
            for (String tag : tags) {
                final String source;
                String[] pair = tag.split("=");
                if (pair.length < 2) {
                    source = "out = '" + pair[0] + "';"; // e.g. out = 'help';
                } else {
                    String[] nestedctx = pair[0].split(".");
                    String seq = "";
                    for (String part : nestedctx) {
                        /** traverse and create the nested context (e.g. out.foo.bar)*/
                        if (!seq.equals(pair[0])) {
                            if (!seq.isEmpty()) {
                                seq += ".";
                            }
                            seq += part;
                            if (!props.contains("out." + seq)) {
                                /** the subcontext hasn't been created so let's do this here */
                                final String expr = "out." + seq + " = new Object();";
                                props.add("out." + seq);
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("setting: '" + expr + "'");
                                }
                                context.evaluateString(scope, expr, "expr", 1, null);
                            }
                        }
                    }
                    source = "out." + pair[0] + " = '" + pair[1] + "';";
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("setting: '" + source + "'");
                }
                context.evaluateString(scope, source, "source", 1, null);                       
            }
            interpretation = scope.get("out", scope);
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("... created semantic interpretation");
            }
        }
        return interpretation;
    }
}
