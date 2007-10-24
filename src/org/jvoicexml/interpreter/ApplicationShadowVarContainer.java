/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.lang.reflect.Method;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.mozilla.javascript.ScriptableObject;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Component that provides a container for the shadowed variables for the
 * standard application variables.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
@SuppressWarnings("serial")
public final class ApplicationShadowVarContainer
        extends ScriptableObject {
    /** Logger instance. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ApplicationShadowVarContainer.class);

    /** Name of the application variable. */
    public static final String VARIABLE_NAME = "application";

    /** The raw string of words that were recognized for this interpretation. */
    private LastResultShadowVarContainer[] lastresults;

    /**
     * Constructs a new object.
     */
    public ApplicationShadowVarContainer() {
        Method getLastresultMethod = null;
        try {
            getLastresultMethod = ApplicationShadowVarContainer.class.getMethod(
                    "getLastresult", (Class<?>[]) null);
        } catch (SecurityException e) {
            // Should not happen.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getLocalizedMessage(), e);
            }
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // Should not happen.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getLocalizedMessage(), e);
            }
            e.printStackTrace();
        }

        defineProperty("lastresult$", null, getLastresultMethod, null,
                READONLY);
    }

    /**
     * Sets the recognition result.
     * @param result the recognition result.
     */
    public void setRecognitionResult(final RecognitionResult result) {
        final String utterance = result.getUtterance();
        final float confidence = result.getConfidence();
        final ModeType mode = result.getMode();

        lastresults = new LastResultShadowVarContainer[1];

        lastresults[0] =
            new LastResultShadowVarContainer(utterance, confidence, mode.getMode());
    }

    /**
     * This method is a callback for rhino which gets called on instantiation.
     * (virtual js constructor)
     */
    public void jsContructor() {
    }

    /**
     * Retrieves the last result.
     * @return the last result.
     */
    public LastResultShadowVarContainer[] getLastresult() {
        return lastresults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return ApplicationShadowVarContainer.class.getSimpleName();
    }

}
