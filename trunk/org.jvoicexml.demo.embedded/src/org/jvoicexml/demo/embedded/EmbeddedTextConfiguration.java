/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/mock/config/MockConfiguration.java $
 * Version: $LastChangedRevision: 3659 $
 * Date:    $Date: 2013-03-01 15:33:27 +0100 (Fri, 01 Mar 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.demo.embedded;

import java.util.Collection;

import org.jvoicexml.Configuration;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.documentserver.schemestrategy.FileSchemeStrategy;
import org.jvoicexml.documentserver.schemestrategy.HttpSchemeStrategy;
import org.jvoicexml.implementation.PlatformFactory;
import org.jvoicexml.implementation.grammar.GrammarTransformer;
import org.jvoicexml.implementation.grammar.transformer.SrgsXml2SrgsXmlGrammarTransformer;
import org.jvoicexml.implementation.jvxml.BufferedCharacterInput;
import org.jvoicexml.implementation.jvxml.JVoiceXmlImplementationPlatformFactory;
import org.jvoicexml.implementation.text.TextPlatformFactory;
import org.jvoicexml.interpreter.DialogFactory;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.InitializationTagStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyRepository;
import org.jvoicexml.interpreter.dialog.ExecutableMenuForm;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.dialog.JVoiceXmlDialogFactory;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarProcessor;
import org.jvoicexml.interpreter.grammar.identifier.SrgsXmlGrammarIdentifier;
import org.jvoicexml.interpreter.tagstrategy.JVoiceXmlTagStrategyRepository;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Menu;

/**
 * Dummy implementation of a configuration object.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3659 $
 * @since 0.7.4
 */
public final class EmbeddedTextConfiguration implements Configuration {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Collection<T> loadObjects(final Class<T> baseClass,
            final String root) {
        final Collection<T> col = new java.util.ArrayList<T>();
        if (baseClass == TagStrategyFactory.class) {
            try {
                T value = (T) new EmbeddedTagStrategyFactory();
                col.add(value);
            } catch (Exception e) {
                return null;
            }
        } else if (baseClass == PlatformFactory.class) {
            final TextPlatformFactory factory = new TextPlatformFactory();
            factory.setInstances(1);
            col.add((T) factory);
        } else if (baseClass == GrammarIdentifier.class) {
            final GrammarIdentifier identifier = new SrgsXmlGrammarIdentifier();
            col.add((T) identifier);
        } else if (baseClass == GrammarTransformer.class) {
            final GrammarTransformer transformer =
                    new SrgsXml2SrgsXmlGrammarTransformer();
            col.add((T) transformer);
        }
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T loadObject(final Class<T> baseClass, final String key) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadObject(final Class<T> baseClass) {
        if (baseClass == InitializationTagStrategyFactory.class) {
            try {
                return (T) new EmbeddedInitializationTagStrategyFactory();
            } catch (Exception e) {
                return null;
            }
        } else if (baseClass == TagStrategyFactory.class) {
            try {
                return (T) new EmbeddedTagStrategyFactory();
            } catch (Exception e) {
                return null;
            }
        } else if (baseClass == TagStrategyRepository.class) {
            return (T) new JVoiceXmlTagStrategyRepository();
        } else if (baseClass == DocumentServer.class) {
            final JVoiceXmlDocumentServer server =
                    new JVoiceXmlDocumentServer();
            server.addSchemeStrategy(new HttpSchemeStrategy());
            server.addSchemeStrategy(new FileSchemeStrategy());
            return (T) server;
        } else if (baseClass == ImplementationPlatformFactory.class) {
            return (T) new JVoiceXmlImplementationPlatformFactory();
        } else if (baseClass == SpeechRecognizerProperties.class) {
            return (T) new SpeechRecognizerProperties();
        } else if (baseClass == DtmfRecognizerProperties.class) {
            return (T) new DtmfRecognizerProperties();
        } else if (baseClass == DialogFactory.class) {
            final JVoiceXmlDialogFactory factory = new JVoiceXmlDialogFactory();
            factory.addDialogMapping(Form.TAG_NAME, new ExecutablePlainForm());
            factory.addDialogMapping(Menu.TAG_NAME, new ExecutableMenuForm());
            return (T) factory;
        } else if (baseClass == GrammarProcessor.class) {
            return (T) new JVoiceXmlGrammarProcessor();
        } else if (baseClass == BufferedCharacterInput.class) {
            return (T) new BufferedCharacterInput();
        }
        return null;
    }

}
