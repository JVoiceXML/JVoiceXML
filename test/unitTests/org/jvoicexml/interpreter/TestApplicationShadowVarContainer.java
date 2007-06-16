/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import junit.framework.TestCase;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.SemanticError;

/**
 * Test case for {@link ApplicationShadowVarContainer}.
 *
 * @author Dirk SChnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestApplicationShadowVarContainer
        extends TestCase {
    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private ApplicationShadowVarContainer application;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        scripting = new ScriptingEngine(null);

        try {
            application = scripting.createHostObject(
                    ApplicationShadowVarContainer.VARIABLE_NAME,
                    ApplicationShadowVarContainer.class);
        } catch (SemanticError ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.ApplicationShadowVarContainer#getLastresult()}.
     */
    public void testGetLastresult() {
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("testutterance");
        result.setConfidence(0.7f);
        result.setMode("voice");

        application.setRecognitionResult(result);

        try {
            assertEquals("testutterance", (String) scripting
                    .eval("application.lastresult$[0].utterance"));
        } catch (SemanticError ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test class o a recognition result.
     *
     * @author Dirk SChnelle
     * @version $Revision: $
     * @since 0.6
     *
     * <p>
     * Copyright &copy; 2007 JVoiceXML group - <a
     * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
     * </a>
     * </p>
     */
    private final class DummyRecognitionResult implements RecognitionResult {
        /** The confidence of the result. */
        private float confidence;

        /** The current mark. */
        private String mark;

        /** The mode. */
        private String mode;

        /** The utterance. */
        private String utterance;

        /** Result accepted. */
        private boolean accepted;

        /** Result rejected. */
        private boolean rejected;

        /**
         * {@inheritDoc}
         */
        public float getConfidence() {
            return confidence;
        }

        /**
         * {@inheritDoc}
         */
        public void setMark(final String name) {
            mark = name;
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
        public String getMode() {
            return mode;
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
            return accepted;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isRejected() {
            return rejected;
        }

        /**
         * Sets the confidence.
         * @param conf the confidence to set.
         */
        public void setConfidence(final float conf) {
            confidence = conf;
        }

        /**
         * Sets the mode.
         * @param newMode the mode to set
         */
        public void setMode(final String newMode) {
            mode = newMode;
        }

        /**
         * Sets the utterance.
         * @param utt the utterance to set
         */
        public void setUtterance(final String utt) {
            utterance = utt;
        }
    }
}
