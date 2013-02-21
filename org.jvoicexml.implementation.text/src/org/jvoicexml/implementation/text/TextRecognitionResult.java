/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.processor.srgs.GrammarChecker;
import org.jvoicexml.xml.srgs.ModeType;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * A recognition result for text based input. Since textual input is less
 * error prone than pure speech recognition we will always accept the
 * result with full confidence.
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

    /**Reference to the grammarChecker Object.*/
    private final GrammarChecker grammarChecker;
       
    /**The Logger for this class.*/
    private static final Logger LOGGER =
        Logger.getLogger(TextRecognitionResult.class);
    
    
    /**
     * Constructs a new object.
     * @param text the received text.
     * @param checker the checker for the grammar.
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
    public boolean isRejected() {
        return !isAccepted();
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
    public Object getSemanticInterpretation() {
        if (interpretation == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating semantic interpretation...");
            }
            
            if (grammarChecker == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("there is no grammar graph" 
                            + " cannot get semantic interpretation");
                }
                return null;
            }

            final String[] tags =  grammarChecker.getInterpretation();
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
            context.evaluateString(scope, "out = new Object();", "expr", 1,
                    null);
            final Collection<String> props = new java.util.ArrayList<String>();
            for (String tag : tags) {
                final String[] pair = tag.split("=");
               
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
}
