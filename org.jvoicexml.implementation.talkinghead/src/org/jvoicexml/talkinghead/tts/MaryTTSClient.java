/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.talkinghead.tts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

import marytts.client.MaryClient;
import marytts.util.data.audio.AudioPlayer;
import marytts.util.http.Address;

/**
 * MaryTTS Client to process TTS and output the audio information.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class MaryTTSClient {
  /**
   * Listener class to listen to the events of the MaryTTSClient.
   * 
   * @author Matthias Mettel
   * @author Markus Ermuth
   * @author Alex Krause
   * 
   * @version $LastChangedRevision$
   * @since 0.7.3
   */
  public interface Listener {
    /**
     * Constant definition of the start event type.
     */
    int EVENT_TYPE_START = 0;
    
    /**
     * Constant definition of the stop event type.
     */
    int EVENT_TYPE_STOP = 1;

    /**
     * Method to implement to get MaryTTSClient events.
     * 
     * @param type type of the event
     * @param id BML id of the command
     */
    void update(int type,
        String id);
  }

  /**
   * Definition to use the german TextToSpeech processing.
   */
  public static final String LOCALE_DE = "de";

  /**
   * Definition to use the english TextToSpeech processing.
   */
  public static final String LOCALE_ENG = "en-US";

  /**
   * Definition to use the telugu TextToSpeech processing.
   */
  public static final String LOCALE_TELUGU = "te";

  /**
   * Definition to use the turkish TextToSpeech processing.
   */
  public static final String LOCALE_TURKISH = "tr";

  /**
   * Client of the MaryTTS, to generate audio output of the Avatar.
   */
  private MaryClient maryClient;

  /**
   * Used Locale to generate audio information.
   */
  private String speechLocale;

  /**
   * BML id, which processes the tts.
   */
  private String processID;

  /**
   * Name of the used voice.
   */
  private String voiceName;

  /**
   * Event Handler to handle Events of the audio output.
   */
  private Listener eventHandler;

  /**
   * 
   * 
   * @param ip
   *          ip address of the MaryTTS Server
   * @param port
   *          Port of the MaryTTS Server
   * @param locale
   *          locale of the text
   * @param speechEventHandler
   *          Event Handler for handling Output Events
   * @throws IOException
   *           is thrown, when MaryTTS cannot started
   */
  
  /**
   * Constructor to set up the MaryTTS processing & AudioOutput.
   * 
   * @param ip ip address of the MaryTTS server
   * @param port port of the MaryTTS server
   * @param locale language for voice generation
   * @param voice name of the voice, which should be used
   * @param speechEventHandler event handler for event listening
   * @throws IOException is thrown, if the connection cannot be established
   */
  public MaryTTSClient(final String ip,
      final int port,
      final String locale,
      final String voice,
      final Listener speechEventHandler) throws IOException {
    // Check Parameter
    if (speechEventHandler == null) {
      throw new IllegalArgumentException("EventHandler cannot be null");
    }

    // Set Attributes
    speechLocale = locale;
    voiceName = voice;
    eventHandler = speechEventHandler;

    // Get MaryClient
    maryClient = MaryClient.getMaryClient(new Address(ip, port));
  }

  /**
   * Processes the MaryTTS Output.
   * 
   * @param theProcessID the bml id of the process
   * @param text
   *          text, which will converted to audio information by MaryTTS
   * @throws IOException the connection lost
   * @throws UnsupportedAudioFileException failed to play the audio data
   */
  public final void process(final String theProcessID,
      final String text) throws IOException,
      UnsupportedAudioFileException {

    processID = theProcessID;

    // Generate Stream
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    // Process Text
    maryClient.process(text,
        "TEXT",
        "AUDIO",
        speechLocale,
        "WAVE",
        voiceName,
        byteStream);

    // Generate Audio
    ByteArrayInputStream byteInputStream =
        new ByteArrayInputStream(byteStream.toByteArray());
    AudioInputStream audioStream =
        AudioSystem.getAudioInputStream(byteInputStream);
    AudioPlayer player = new AudioPlayer(audioStream, new LineListener() {
      @Override
      public void update(final LineEvent event) {
        if (event.getType() == LineEvent.Type.START
            && eventHandler != null) {
          eventHandler.update(Listener.EVENT_TYPE_START,
              processID);
        } else if (event.getType() == LineEvent.Type.STOP
                   && eventHandler != null) {
          eventHandler.update(Listener.EVENT_TYPE_STOP,
              processID);
        }
      }
    });

    // Start Audio
    player.start();
  }
}
