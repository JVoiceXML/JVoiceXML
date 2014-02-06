/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/Mmi.java $
 * Version: $LastChangedRevision: 3651 $
 * Date:    $Date: 2013-02-27 00:16:33 +0100 (Wed, 27 Feb 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.processor;

import java.io.IOException; 
import java.io.OutputStream;
import java.net.URI;
import javax.sound.sampled.AudioFormat;
import org.jvoicexml.CallControl;
import org.jvoicexml.CallControlProperties;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.SessionListener;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.jvxml.BufferedCharacterInput;
import org.jvoicexml.voicexmlunit.io.Statement;
import org.mockito.Mockito;


    
/**
 * Dialog transacts and abstracts the model for the Supervisor and the
 * individual Statement it wants to send.
 * 
 * @author thesis
 * 
 */
public final class Dialog implements SessionListener, CallControl {

    private URI uri;
    private ImplementationPlatform implementation;
    private Tape tape;
    private Session session;
    
    public Dialog() {
    }
    
    public Dialog(final URI uri) {
        implementation = null;
        set(uri);
    }
    
    public void set(final URI uriNew) {
        uri = uriNew;
    }
    
    public URI get() {
        return uri;
    }
    
    public ImplementationPlatform getPlatform() throws JVoiceXMLEvent {
        if (implementation == null) {
            implementation = Mockito.mock(ImplementationPlatform.class);
            mock(implementation);
        }
        return implementation;
    }
    
    public void finish() {
        hangup();
        if (implementation != null) {
            implementation.close();
            implementation = null;
        }
        tape = null;
    }
    
    private void mock(final ImplementationPlatform implementation) 
            throws JVoiceXMLEvent { 
        
        /**
         * https://code.google.com/p/mockito/ 
         */
        
        final SystemOutput output;
        output = Mockito.mock(SystemOutput.class);
        Mockito.when(implementation.getSystemOutput()).thenReturn(output);

        Mockito.when(implementation.hasUserInput()).thenReturn(true);
        final UserInput input = Mockito.mock(UserInput.class);
        Mockito.when(implementation.getUserInput()).thenReturn(input);

        final CharacterInput buffer = new BufferedCharacterInput();
        Mockito.when(implementation.getCharacterInput()).thenReturn(buffer);

        Mockito.when(implementation.getCallControl()).thenReturn(this);

        final SpeakableText any = Mockito.any();
        //Mockito.when(platform.queuePrompt(any)).then...
    }

    private void consume(final Statement statement) {
        try {
            tape.capture(statement);
        } catch (InterruptedException ex) {
            
        }
    }
    
    private void produce() {
        try {
            final Statement statement = tape.playback();
            statement.send(this);
        } catch (InterruptedException ex) {
            
        }
    }
    
    @Override
    public void sessionStarted(Session session) {
        if (session != null) {
            this.session = session;
        }
    }

    @Override
    public void sessionEnded(Session session) {
        if (session != null && session.equals(this.session)) {
            this.session = null;
        }
    }

    @Override
    public void play(SystemOutput output, CallControlProperties props) 
            throws NoresourceError, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void record(UserInput input, CallControlProperties props) 
            throws NoresourceError, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AudioFormat getRecordingAudioFormat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void startRecording(UserInput input, OutputStream stream, 
    CallControlProperties props) 
            throws NoresourceError, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopRecord() throws NoresourceError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopPlay() throws NoresourceError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void transfer(String dest) throws NoresourceError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void hangup() {
        session = null;
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
