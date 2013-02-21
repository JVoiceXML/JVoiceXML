/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.jvxml;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.Session;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ObservableSynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.implementation.SynthesizedOutputProvider;

/**
 * Basic wrapper for {@link SystemOutput}.
 *
 * <p>
 * The {@link JVoiceXmlSystemOutput} encapsulates two external resources, the
 * {@link SynthesizedOutput} and the {@link AudioFileOutput}. Both resources
 * are obtained from a pool using the same type.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
final class JVoiceXmlSystemOutput
    implements SystemOutput, ObservableSynthesizedOutput,
        SynthesizedOutputProvider {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlSystemOutput.class);

    /** The synthesizer output device. */
    private final SynthesizedOutput synthesizedOutput;

    /** The current session. */
    private final Session session;

    /**
     * Constructs a new object.
     * @param synthesizer the synthesizer output device.
     * @param currentSession the current session.
     */
    public JVoiceXmlSystemOutput(final SynthesizedOutput synthesizer,
            final Session currentSession) {
        synthesizedOutput = synthesizer;
        session = currentSession;
    }

    /**
     * Retrieves the synthesized output resource.
     * @return the synthesized output resource.
     */
    public SynthesizedOutput getSynthesizedOutput() {
        return synthesizedOutput;
    }

    /**
     * {@inheritDoc}
     */
    public void queueSpeakable(final SpeakableText speakable,
            final String sessionId, final DocumentServer documentServer)
        throws NoresourceError, BadFetchError {
        synthesizedOutput.queueSpeakable(speakable, sessionId, documentServer);
    }

    /**
     * {@inheritDoc}
     */
    public void cancelOutput() throws NoresourceError {
        final boolean supportsBargeIn = synthesizedOutput.supportsBargeIn();
        if (!supportsBargeIn) {
            LOGGER.warn("implementation platform does not support barge-in");
            return;
        }
        synthesizedOutput.cancelOutput();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SynthesizedOutputListener listener) {
        if (synthesizedOutput instanceof ObservableSynthesizedOutput) {
            final ObservableSynthesizedOutput observable =
                synthesizedOutput;
            observable.addListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(
            final SynthesizedOutputListener listener) {
        if (synthesizedOutput instanceof ObservableSynthesizedOutput) {
            final ObservableSynthesizedOutput observable =
                synthesizedOutput;
            observable.removeListener(listener);
        }
    }

    /**
     * Checks if the corresponding output device is busy.
     * @return <code>true</code> if the output devices is busy.
     */
    public boolean isBusy() {
        return synthesizedOutput.isBusy();
    }
}
