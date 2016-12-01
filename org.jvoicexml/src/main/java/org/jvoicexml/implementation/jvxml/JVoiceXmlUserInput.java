/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.implementation.SpokenInputProvider;
import org.jvoicexml.implementation.dtmf.BufferedDtmfInput;
import org.jvoicexml.implementation.grammar.GrammarCache;
import org.jvoicexml.implementation.grammar.LoadedGrammar;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Basic wrapper for {@link UserInput}.
 * 
 * <p>
 * The {@link UserInput} encapsulates two external resources. A basic
 * implementation for the {@link DtmfInput} is provided by the interpreter. The
 * unknown resource is the spoken input, which must be obtained from a resource
 * pool. This class combines these two as the {@link UserInput} which is been
 * used by the rest of the interpreter.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.5
 */
final class JVoiceXmlUserInput implements UserInput, SpokenInputProvider {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(JVoiceXmlUserInput.class);

    /** The character input device. */
    private final BufferedDtmfInput dtmfInput;

    /** The spoken input device. */
    private final SpokenInput spokenInput;

    /** The cache of already processed grammars. */
    private final GrammarCache cache;

    /**
     * Constructs a new object.
     * 
     * @param input
     *            the spoken input implementation.
     * @param dtmf
     *            the buffered character input.
     */
    public JVoiceXmlUserInput(final SpokenInput input,
            final BufferedDtmfInput dtmf) {
        spokenInput = input;
        dtmfInput = dtmf;
        cache = new GrammarCache();
    }

    /**
     * Retrieves the spoken input.
     * 
     * @return spoken input.
     * 
     * @since 0.5.5
     */
    @Override
    public SpokenInput getSpokenInput() {
        return spokenInput;
    }

    /**
     * Retrieves the character input.
     * 
     * @return character input.
     * 
     * @since 0.5.5
     */
    public DtmfInput getDtmfInput() {
        return dtmfInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int activateGrammars(final Collection<GrammarDocument> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError,
            UnsupportedFormatError {
        // Separate grammars for the DTMF and voice recognizer
        final Collection<GrammarImplementation<?>> voiceGrammars =
                new java.util.ArrayList<GrammarImplementation<?>>();
        final Collection<GrammarImplementation<?>> dtmfGrammars =
                new java.util.ArrayList<GrammarImplementation<?>>();
        for (GrammarDocument grammar : grammars) {
            final GrammarImplementation<?> grammarImplementation =
                    loadGrammar(grammar);
            final ModeType type = grammarImplementation.getModeType();
            // A grammar is voice by default.
            if (type == ModeType.DTMF) {
                dtmfGrammars.add(grammarImplementation);
            } else {
                voiceGrammars.add(grammarImplementation);
            }
        }
        
        // Activate the specific grammars per mode type
        if (!voiceGrammars.isEmpty()) {
            spokenInput.activateGrammars(voiceGrammars);
        }
        if ((dtmfInput != null) && !dtmfGrammars.isEmpty()) {
            dtmfInput.activateGrammars(dtmfGrammars);
        }
        return voiceGrammars.size() + dtmfGrammars.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deactivateGrammars(final Collection<GrammarDocument> grammars)
            throws NoresourceError, BadFetchError {
        final Collection<GrammarImplementation<?>> voiceGrammars =
                new java.util.ArrayList<GrammarImplementation<?>>();
        final Collection<GrammarImplementation<?>> dtmfGrammars =
                new java.util.ArrayList<GrammarImplementation<?>>();

        for (GrammarDocument grammar : grammars) {
            GrammarImplementation<?> impl = cache.getImplementation(grammar);
            if (impl == null) {
                LOGGER.warn("no implementation for grammar " + grammar);
                continue;
            }
            final ModeType type = grammar.getModeType();
            // A grammar is voice by default.
            if (type == ModeType.DTMF) {
                dtmfGrammars.add(impl);
            } else {
                voiceGrammars.add(impl);
            }
        }

        if (!voiceGrammars.isEmpty()) {
            spokenInput.deactivateGrammars(voiceGrammars);
        }
        if ((dtmfInput != null) && !dtmfGrammars.isEmpty()) {
            dtmfInput.deactivateGrammars(dtmfGrammars);
        }
        return voiceGrammars.size() + dtmfGrammars.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        return spokenInput.getSupportedBargeInTypes();
    }

    /**
     * Creates a {@link GrammarImplementation} from the contents provided by the
     * reader. If the grammar contained in the reader already exists, it is
     * over-written.
     *
     * <p>
     * This method is mainly needed for non SRGS grammars, e.g. JSGF. Loading an
     * SRGS grammar is quite easy and can be implemented e.g. as
     * </p>
     * <p>
     * <code>
     * final InputSource inputSource = new InputSource(reader);<br>
     * SrgsXmlDocument doc = new SrgsXmlDocument(inputSource);<br>
     * &#47;&#47; Pass it to the recognizer<br>
     * return doc;
     * </code>
     * </p>
     *
     * @param document
     *            the grammar to read. The type is one of the supported types of
     *            the implementation, that has been requested via
     *            {@link #getSupportedGrammarTypes(ModeType)}.
     *
     * @return Read grammar.
     *
     * @since 0.3
     *
     * @exception NoresourceError
     *                The input resource is not available.
     * @exception BadFetchError
     *                Error reading the grammar.
     * @exception UnsupportedFormatError
     *                Invalid grammar format.
     */
    private GrammarImplementation<?> loadGrammar(final GrammarDocument document)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        final URI uri = document.getURI();

        // Check if the grammar has already been loaded
        if (cache.contains(document)) {
            LOGGER.info("grammar from '" + uri + "' already loaded");
            return cache.getImplementation(document);
        }

        // Actually load and cache the grammar
        final GrammarType type = document.getMediaType();
        try {
            LOGGER.info("loading '" + type + "' grammar from '" + uri + "'");
            final GrammarImplementation<?> implementation = spokenInput
                    .loadGrammar(uri, type);
            final LoadedGrammar loaded = new LoadedGrammar(document,
                    implementation);
            cache.add(loaded);
            return implementation;
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final SpokenInputListener listener) {
        spokenInput.addListener(listener);
        if (dtmfInput != null) {
            dtmfInput.addListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final SpokenInputListener listener) {
        spokenInput.removeListener(listener);
        if (dtmfInput != null) {
            dtmfInput.removeListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRecognition(final DataModel model,
            final SpeechRecognizerProperties speech,
            final DtmfRecognizerProperties dtmf) throws NoresourceError,
            BadFetchError {
        spokenInput.startRecognition(model, speech, dtmf);
        if (dtmfInput != null) {
            dtmfInput.startRecognition(model, speech, dtmf);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopRecognition() {
        spokenInput.stopRecognition();
        if (dtmfInput != null) {
            dtmfInput.stopRecognition();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrammarType> getSupportedGrammarTypes(
            final ModeType mode) {
        if (mode == ModeType.DTMF) {
            final Collection<GrammarType> types =
                    new java.util.ArrayList<GrammarType>();
            types.add(GrammarType.SRGS_XML);
            return types;
        } else {
            return spokenInput.getSupportedGrammarTypes();
        }
    }

    /**
     * Checks if the corresponding input device is busy.
     * 
     * @return <code>true</code> if the input devices is busy.
     */
    public boolean isBusy() {
        return spokenInput.isBusy();
    }
}
