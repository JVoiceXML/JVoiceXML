/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.jvoicexml.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DtmfRecognizerProperties;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SpeechRecognizerProperties;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
import org.mrcp4j.message.header.IllegalValueException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;


public class Mrcpv2UserInput implements UserInput {

    private static final Logger _logger =  Logger.getLogger(Mrcpv2UserInput.class);

    private SpeechClient client;
    
    private MrcpImplementationPlatform iplatform;
    
    private Reader _loadedGrammarReader;
    private GrammarType _loadedGrammarType;
    private String _activatedGrammarName;
    private boolean _activatedGrammarState;
    //private UserInputListener _listener;
    private int count = 0;

	private String grammar;

    public Mrcpv2UserInput(SpeechClient client) {
        super();
        this.client = client;
    }
    

    public void setImplementationPlatform(MrcpImplementationPlatform platform){
        iplatform = platform;
    }
    
    //-------------------------------------------------------------------------------------
    
    public void activate() {
        _logger.debug("Mrcpv2UserInput activate method being called.  Not implemented!");

    }

    public void close() {
        _logger.debug("Mrcpv2UserInput activate method being called.  Not implemented!");

    }


    public void open() throws NoresourceError {
        _logger.debug("Mrcpv2UserInput activate method being called.  Not implemented!");

    }

    public void passivate() {
        _logger.debug("Mrcpv2UserInput activate method being called.  Not implemented!");

    }

    public void connect(ConnectionInformation arg0) throws IOException {
        _logger.debug("Mrcpv2UserInput activate method being called.  Not implemented!");

    }

    //--------------------------------------------------------------------------------------------

    public void startRecognition() throws NoresourceError, BadFetchError {
        _logger.debug("Mrcpv2UserInput: start recognition");
        try {
            //_loadedGrammarReader.reset();
        	//not hotword, grammar is attached (not a url)
        	_logger.warn("recognizing with grammar: "+grammar);
            client.recognize(grammar, false, true, 30000);
            iplatform.inputStarted();
        } catch (MrcpInvocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoMediaControlChannelException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    }  
    
    
    public void stopRecognition() {
        _logger.debug("Mrcpv2UserInput: stop recognition");
    	try {
	        client.stopActiveRecognitionRequests();
        } catch (MrcpInvocationException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (NoMediaControlChannelException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

    }    

    public Collection<BargeInType> getSupportedBargeInTypes() {
        final Collection<BargeInType> types =
                new java.util.ArrayList<BargeInType>();

        types.add(BargeInType.SPEECH);
        types.add(BargeInType.HOTWORD);

        return types;
    }


    public GrammarImplementation<?> newGrammar(final String name, 
            final GrammarType type) throws NoresourceError {
        _logger.debug("NewGrammar not implemented!.  creating new null grammar");
        return null;
        //final RuleGrammar grammar = _grammar.getRuleGrammar();
        //return new RuleGrammarImplementation(grammar);
    }


    public GrammarImplementation<?> loadGrammar(final Reader reader, final GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        
            _logger.debug("loading grammar from reader");
            
            _loadedGrammarReader = reader;
            _loadedGrammarType = type;
            
            BufferedReader in  = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            grammar = sb.toString();
            _logger.debug(grammar);
            
          
            return new JSGFGrammarImplementation(sb.toString());
           
    }


    public void record(final OutputStream out) throws NoresourceError {
        throw new NoresourceError("not implemented yet");
    }



    public String getType() {
        return "mrcpv2";
    }


    public Collection<GrammarType> getSupportedGrammarTypes() {
        final Collection<GrammarType> types = new java.util.ArrayList<GrammarType>();
        types.add(GrammarType.JSGF);
        return types;
    }


    public void addCharacter(char arg0) {
        // TODO Auto-generated method stub
    }


    public static void ensureDir(File dir) {
        
        // create directory if it does not exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                _logger.warn("Could not create directory: " + dir.getAbsolutePath());
            }
        }

        // make sure dir is actually a directory
        if (!dir.isDirectory()) {
            _logger.warn("File specified was not a directory: " + dir.getAbsolutePath());
        }
    }





	public Collection<GrammarType> getSupportedGrammarTypes(ModeType mode) {
	    // TODO Auto-generated method stub
        _logger.debug("getSupportedGrammarTypes");
        final Collection<GrammarType> types = new java.util.ArrayList<GrammarType>();
        types.add(GrammarType.JSGF);
        return types;

    }
	
    public boolean isBusy() {
        return iplatform.isInputBusy();
    }


	public int activateGrammars(Collection<GrammarDocument> arg0)
			throws BadFetchError, UnsupportedLanguageError, NoresourceError,
			UnsupportedFormatError {
		// TODO Auto-generated method stub
		return 0;
	}


	public int deactivateGrammars(Collection<GrammarDocument> arg0)
			throws NoresourceError, BadFetchError {
		// TODO Auto-generated method stub
		return 0;
	}


	public void startRecognition(SpeechRecognizerProperties arg0,
			DtmfRecognizerProperties arg1) throws NoresourceError,
			BadFetchError {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startRecognition(DataModel model, Collection<ModeType> types, SpeechRecognizerProperties speech,
			DtmfRecognizerProperties dtmf) throws NoresourceError, BadFetchError {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stopRecognition(Collection<ModeType> types) {
		// TODO Auto-generated method stub
		
	}
    
}
