/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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


package org.jvoicexml.implementation.jsapi10.jvxml;

import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.RuleGrammar;

import org.apache.log4j.Logger;

import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.ResultListener;

/**
 * Result listener for results from the sphinx recognizer.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
class Sphinx4ResultListener
        implements ResultListener {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(Sphinx4ResultListener.class);

    /** The recognizer which is notified when a result is obtained. */
    private final Sphinx4Recognizer recognizer;

    /**
     * Construct a new result listener.
     * @param rec The recognizer.
     */
    public Sphinx4ResultListener(final Sphinx4Recognizer rec) {
        recognizer = rec;
    }

    /**
     * Method called when a result is generated.
     * @param result The new result.
     */
    public void newResult(final Result result) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received result: " + result);
            LOGGER.debug("isFinal: " + result.isFinal());
        }

        if (!result.isFinal()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result is not final. forget about it.");
            }

            return;
        }

        final RuleGrammar grammar = recognizer.getRuleGrammar();
        final Sphinx4Result res = new Sphinx4Result(grammar, result);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("number of finalized tokens: " + res.numTokens());
        }

        if (res.numTokens() == 0) {
            res.setResultState(javax.speech.recognition.Result.REJECTED);

            final ResultEvent event =
                    new ResultEvent(res, ResultEvent.RESULT_REJECTED);
            recognizer.fireResultRejected(event);
        } else {
            res.setResultState(javax.speech.recognition.Result.ACCEPTED);

            final ResultEvent event =
                    new ResultEvent(res, ResultEvent.RESULT_ACCEPTED);
            recognizer.fireResultAccepted(event);
        }

    }
}
