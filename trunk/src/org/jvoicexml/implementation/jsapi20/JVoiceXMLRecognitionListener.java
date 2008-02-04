/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/jsapi10/JVoiceXMLRecognitionListener.java $
 * Version: $LastChangedRevision: 248 $
 * Date:    $Date: 2007-03-12 08:20:28 +0000 (Seg, 12 Mar 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultListener;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.UserInputListener;
import org.apache.log4j.Logger;
import org.jvoicexml.xml.vxml.BargeInType;
import java.util.Collection;

/**
 * Thread that waits for input from the recognizer.
 *
 * @author Dirk Schnelle
 * @version $Revision: 248 $
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class JVoiceXMLRecognitionListener implements ResultListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXMLRecognitionListener.class);

    /** Listener for user input events. */
    private Collection<UserInputListener> listener;

    /**
     * Construct a new object.
     * @param inputListener Listener for user input events.
     */
    public JVoiceXMLRecognitionListener(final Collection<UserInputListener> inputListener) {
        listener = inputListener;
    }


    public void resultUpdate(final ResultEvent resultEvent) {
        switch (resultEvent.getId()) {
        case ResultEvent.AUDIO_RELEASED:
            audioReleased(resultEvent);
            break;
        case ResultEvent.GRAMMAR_FINALIZED:
            grammarFinalized(resultEvent);
            break;
        case ResultEvent.RESULT_ACCEPTED:
            resultAccepted(resultEvent);
            break;
        case ResultEvent.RESULT_CREATED:
            resultCreated(resultEvent);
            break;
        case ResultEvent.RESULT_REJECTED:
            resultRejected(resultEvent);
            break;
        case ResultEvent.RESULT_UPDATED:
            resultUpdated(resultEvent);
            break;
        case ResultEvent.TRAINING_INFO_RELEASED:
            trainingInfoReleased(resultEvent);
            break;
        }
    }

    /**
     * A <code>AUDIO_RELEASED</code> event has occured. This event is only
     * issued to finalized results. See the documentation of the
     * <code>isAudioAvailable</code> method the <code>FinalResult</code>
     * interface for details.
     *
     * <p>
     * The event is issued to each <code>ResultListener</code> attached to the
     * <code>Recognizer</code> and to the <code>Result</code>. If a
     * <code>GRAMMAR_FINALIZED</code> event was issued, then the matched
     * <code>Grammar</code> is known, and the event is also issued to each
     * <code>ResultListener</code> attached to that <code>Grammar</code>.
     * </p>
     *
     * @param resultEvent ResultEvent
     */
    public void audioReleased(final ResultEvent resultEvent) {
    }

    /**
     * A <code>GRAMMAR_FINALIZED</code> event has occured because the
     * <code>Recognizer</code> has determined which <code>Grammar</code> is
     * matched by the incoming speech.
     *
     * <p>
     * The event is issued to each <code>ResultListener</code> attached to the
     * <code>Recognizer</code>, <code>Result</code>, and matched
     * <code>Grammar</code>.
     * </p>
     *
     * @param resultEvent ResultEvent
     */
    public void grammarFinalized(final ResultEvent resultEvent) {
    }

    /**
     * An <code>RESULT_ACCEPTED</code> event has occured indicating that a
     * <code>Result</code> has transitioned from the <code>UNFINALIZED</code>
     * state to the <code>ACCEPTED</code> state.
     *
     * <p>
     * Since the <code>Result</code> source for this event is finalized, the
     * <code>Result</code> object can be safely cast to the
     * <code>FinalResult</code> interface.
     *
     * @param resultEvent ResultEvent
     */
    public void resultAccepted(final ResultEvent resultEvent) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("result accepted: " + resultEvent);
        }

        if (listener != null) {
            synchronized (listener) {
                for (UserInputListener current : listener) {
                    current.speechStarted(BargeInType.HOTWORD);
                }
            }
        }

        final Result result = (Result) resultEvent.getSource();

        if (listener == null) {
            return;
        }

        final RecognitionResult recognitionResult =
                new Jsapi20RecognitionResult(result);

            synchronized (listener) {
                for (UserInputListener current : listener) {
                    current.resultAccepted(recognitionResult);
                }
            }
    }

    /**
     * A <code>RESULT_CREATED</code> event is issued when a
     * <code>Recognizer</code> detects incoming speech that may match an active
     * grammar of an application.
     *
     * @param resultEvent ResultEvent
     */
    public void resultCreated(final ResultEvent resultEvent) {
        if (listener != null) {
            synchronized (listener) {
                for (UserInputListener current : listener) {
                    current.speechStarted(BargeInType.SPEECH);
                }
            }
        }
    }

    /**
     * An <code>RESULT_REJECTED</code> event has occured indicating that a
     * <code>Result</code> has transitioned from the <code>UNFINALIZED</code>
     * state to the <code>REJECTED</code> state.
     *
     * @param resultEvent ResultEvent
     */
    public void resultRejected(final ResultEvent resultEvent) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("result rejected");
        }

        final Result result = (Result) resultEvent.getSource();

        if (listener == null) {
            return;
        }

        final RecognitionResult recognitionResult =
            new Jsapi20RecognitionResult(result);

        synchronized (listener) {
            for (UserInputListener current : listener) {
                current.resultRejected(recognitionResult);
            }
        }
    }

    /**
     * A <code>RESULT_UPDATED</code> event has occured because a token has been
     * finalized and/or the unfinalized text of a result has changed.
     *
     * @param resultEvent ResultEvent
     */
    public void resultUpdated(final ResultEvent resultEvent) {
    }

    /**
     * A <code>TRAINING_INFO_RELEASED</code> event has occured. This event is
     * only issued to finalized results. See the documentation of the
     * <code>isTrainingInfoAvailable</code> method the <code>FinalResult</code>
     * interface for details.
     *
     * @param resultEvent ResultEvent
     */
    public void trainingInfoReleased(final ResultEvent resultEvent) {
    }


}
