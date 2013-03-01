/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.test.implementation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.implementation.jsapi10.RuleGrammarImplementation;
import org.jvoicexml.mock.implementation.MockUserInput;
import org.jvoicexml.xml.srgs.GrammarType;

import edu.cmu.sphinx.jsapi.SphinxRecognizerModeDesc;


/**
 * This class provides a dummy implementation of a
 * {@link org.jvoicexml.UserInput} for testing purposes of the JSAPI 1.0
 * implementation platform.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class Jsapi10DummyUserInput extends MockUserInput {
    /** Buffer size when reading a grammar. */
    private static final int BUFFER_SIZE = 1024;

    /** Test recognizer. */
    private static Recognizer recognizer;

    static {
        final SphinxRecognizerModeDesc desc = new SphinxRecognizerModeDesc();
        try {
            recognizer = (Recognizer) desc.createEngine();
            recognizer.allocate();
        } catch (EngineException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new object.
     */
    public Jsapi10DummyUserInput() {
        addSupportedGrammarType(GrammarType.JSGF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GrammarImplementation<?> handleLoadGrammar(final Reader reader,
            final GrammarType type) throws NoresourceError, BadFetchError {
        if (type != GrammarType.JSGF) {
            return null;
        }
        if (recognizer == null) {
            return null;
        }
        final StringBuilder jsgf = new StringBuilder();
        final char[] buffer = new char[BUFFER_SIZE];
        int num = 0;
        do {
            try {
                num = reader.read(buffer);
                if (num > 0) {
                    jsgf.append(buffer, 0, num);
                }
            } catch (IOException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        } while (num > 0);
        final RuleGrammar grammar;
        final Reader jsgfReader = new StringReader(jsgf.toString());
        try {
            grammar = recognizer.loadJSGF(jsgfReader);
        } catch (GrammarException e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (EngineStateError e) {
            throw new NoresourceError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        return new RuleGrammarImplementation(grammar, jsgf.toString());
    }
}
