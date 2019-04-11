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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.mrcp4j.client.MrcpInvocationException;
import org.speechforge.cairo.client.NoMediaControlChannelException;
import org.speechforge.cairo.client.SpeechClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Mrcpv2SystemOutput implements SystemOutput {
    private static final Logger _logger = Logger.getLogger(Mrcpv2SystemOutput.class);

    private MrcpImplementationPlatform iplatform;
    
    private SpeechClient client;

    //TODO:  Many methods not implemented below.  Dertermine if they are needed.  I think not since there are no jvoicexml pools.
    
    public Mrcpv2SystemOutput(SpeechClient client) {
        super();
        this.client = client;
    }

    public void setImplementationPlatform(MrcpImplementationPlatform platform){
        iplatform = platform;
    }
    

    public void queueAudio(AudioInputStream arg0) throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub

    }



    public void cancelOutput() throws NoresourceError {
        _logger.debug("Mrcpv2SystemOutput cancel output method being called.  sending a bargein requets");
        try {
            client.sendBargeinRequest();
        } catch (MrcpInvocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    //Dont think any of there need to be implemented ... 
    public void activate() {
        _logger.debug("Mrcpv2SystemOutput activate method being called.  Not implemented!");
    }

    public void passivate() {
        _logger.debug("Mrcpv2SystemOutput passivate method being called.  Not implemented!");
    }

    public void close() {
        _logger.debug("Mrcpv2SystemOutput close method being called.  Not implemented!");
    }

    public String getType() {
        return "mrcpv2";
    }

    public void open() throws NoresourceError {
        _logger.debug("Mrcpv2SystemOutput open method being called.  Not implemented!");

    }

    public void connect(ConnectionInformation arg0) throws IOException {
        _logger.debug("Mrcpv2SystemOutput connect method being called.  Not implemented!");
    }

	public void queueSpeakable(SpeakableText speakable, DocumentServer documentServer)
            throws NoresourceError, BadFetchError {
	       String speakText=null;
	        try {
	            //TODO: Pass on the entire SSML doc (and remove the code that extracts the text)
	            //The following code extract the text from the SSML since we do not support SSML yet (using the ssml support in jvoicexml)
	            if (speakable instanceof SpeakableSsmlText) {
	               InputStream is = null; 
	               String temp = speakable.getSpeakableText(); 
	               byte[] b = temp.getBytes();
	               is = new ByteArrayInputStream(b);
	               InputSource src = new InputSource( is);
	               SsmlDocument ssml = new SsmlDocument(src);
	               speakText = ssml.getSpeak().getTextContent();
	            }
	            //play the text
	            _logger.debug("queuing following text: "+speakText);
	            client.queuePrompt(false,speakText);
	            iplatform.outputStarted();
	        } catch (ParserConfigurationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (SAXException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
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
	
    public boolean isBusy() {
        return iplatform.isOutputBusy();
    }

	public void queueSpeakable(SpeakableText arg0, String arg1,
			DocumentServer arg2) throws NoresourceError, BadFetchError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelOutput(BargeInType type) throws NoresourceError {
		// TODO Auto-generated method stub
		
	}

}
