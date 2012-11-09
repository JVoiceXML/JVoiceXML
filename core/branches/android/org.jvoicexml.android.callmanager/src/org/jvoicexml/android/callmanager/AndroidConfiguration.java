/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.android.callmanager;

import java.util.Collection;
import org.jvoicexml.Configuration;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.JndiSupport;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
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
import org.jvoicexml.interpreter.variables.VariableProviders;
import org.jvoicexml.test.interpreter.tagstrategy.DummyInitializationTagStrategyFactory;
import org.jvoicexml.test.interpreter.tagstrategy.DummyTagStrategyFactory;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Menu;

import android.os.Looper;
import android.util.Log;

/**
 * Dummy implementation of a configuration object.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public final class AndroidConfiguration 
    implements Configuration {
    
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Collection<T> loadObjects(final Class<T> baseClass,
            final String root) {
        if (baseClass == TagStrategyFactory.class) {
            final Collection<T> col = new java.util.ArrayList<T>();
            try {
                T value = (T) new DummyTagStrategyFactory();
                col.add(value);
                return col;
            } catch (Exception e) {
                return null;
            }
        }
        if (baseClass == GrammarIdentifier.class) {
        	final Collection<T> col = new java.util.ArrayList<T>();
            try {
                T value = (T) new SrgsXmlGrammarIdentifier();
                col.add(value);
                return col;
            } catch (Exception e) {
                return null;
            }
        }
        if (baseClass == JndiSupport.class) {
        	return null;
        }
        if (baseClass == VariableProviders.class) {
        	final Collection<T> col = new java.util.ArrayList<T>();
            try {
                T value = (T) new VariableProviders();
                col.add(value);
                return col;
            } catch (Exception e) {
                return null;
            }
        }
        
        
        return null;
        
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
                return (T) new DummyInitializationTagStrategyFactory();
            } catch (Exception e) {
                return null;
            }
        } else if (baseClass == TagStrategyFactory.class) {
            try {
                return (T) new DummyTagStrategyFactory();
            } catch (Exception e) {
                return null;
            }           
        }
        else if(baseClass == GrammarProcessor.class){
        	try {
        		return(T) new JVoiceXmlGrammarProcessor();
            } catch (Exception e) {
                return null;
            }   
        }        
        else if (baseClass == ImplementationPlatformFactory.class) {
            try {
                return (T) new AndroidImplementationPlatformFactory();
            } catch (Exception e) {
            	Log.e("creando AndroidImplementationPlatformFactory",e.getMessage());
                return null;
            }         
        } else if (baseClass == TagStrategyRepository.class) {
            return (T) new JVoiceXmlTagStrategyRepository();
        } else if (baseClass == SpeechRecognizerProperties.class) {
            return (T) new SpeechRecognizerProperties();
        } else if (baseClass == DtmfRecognizerProperties.class) {
            return (T) new DtmfRecognizerProperties();         
	    } else if (baseClass == DocumentServer.class) {
	        return (T) new AndroidDocumentServer();
	    }else if (baseClass == DialogFactory.class) {
	    	final JVoiceXmlDialogFactory factory = new JVoiceXmlDialogFactory();
	    	factory.addDialogMapping(Form.TAG_NAME, new ExecutablePlainForm());
	    	factory.addDialogMapping(Menu.TAG_NAME, new ExecutableMenuForm());
	    	return (T) factory;
	    }else if (baseClass == CallManager.class) {
	    	return null;
	    }
        
        return null;
    }	
	

}
