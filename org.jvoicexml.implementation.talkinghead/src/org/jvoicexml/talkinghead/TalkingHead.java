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
package org.jvoicexml.talkinghead;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import marytts.exceptions.MaryConfigurationException;

import org.jdom2.JDOMException;
import org.jvoicexml.implementation.lightweightbml.connection.UDPConnection;
import org.jvoicexml.implementation.lightweightbml.parser.LightweightBMLParser;
import org.jvoicexml.implementation.lightweightbml.xmltags.BML;
import org.jvoicexml.implementation.lightweightbml.xmltags.Gesture;
import org.jvoicexml.implementation.lightweightbml.xmltags.ITag;
import org.jvoicexml.implementation.lightweightbml.xmltags.Speech;
import org.jvoicexml.talkinghead.animations.Animator;
import org.jvoicexml.talkinghead.animations.events.AnimationEvent;
import org.jvoicexml.talkinghead.animations.events.NextFrameEvent;
import org.jvoicexml.talkinghead.animations.model.Animation;
import org.jvoicexml.talkinghead.avatar.Avatar;
import org.jvoicexml.talkinghead.bml.BMLExecutor;
import org.jvoicexml.talkinghead.bml.events.BMLEvent;
import org.jvoicexml.talkinghead.tts.MaryTTSClient;
import org.jvoicexml.talkinghead.view.AvatarViewer;
import org.xml.sax.SAXException;

/**
 * Controller and App of TalkingHead.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class TalkingHead
    implements Observer,
               BMLExecutor.Listener,
               MaryTTSClient.Listener,
               Animator.Listener {
  /**
   * Constant default value for the udp connection to receive messages.
   */
  public static final int DEFAULT_NETWORK_BUFFER_SIZE = 65535;

  /**
   * Default port to receive BML messages.
   */
  public static final int DEFAULT_NETWORK_INPUT_PORT = 6666;

  /**
   * Default value for the TTS connection to mary.
   */
  public static final int DEFAULT_TTS_PORT = 59125;

  /**
   * Configuration of the application.
   */
  private Configuration appConfig;

  /**
   * Avatar model with given images and animations.
   */
  private Avatar talkingHeadAvatarModel;

  /**
   * Animation Controller to calculate the Animations.
   */
  private Animator talkingHeadAnimator;

  /**
   * Viewer to show the Animations.
   */
  private AvatarViewer talkingHeadViewer;

  /**
   * MaryTTS Connection for Text Speech Synthesis.
   */
  private MaryTTSClient talkingHeadTextToSpeechProcessor;

  /**
   * Connection to get commands from another application.
   */
  private UDPConnection talkingHeadInputConnection;

  /**
   * Connection to send feedback commands to the control application via UDP.
   */
  private UDPConnection talkingHeadOutputConnection;

  /**
   * Parser for parsing incoming strings to bml-structure.
   */
  private LightweightBMLParser talkingHeadBMLParser;

  /**
   * Handles the execution of BMLCommands.
   */
  private BMLExecutor talkingHeadBMLExecutor;

  /**
   * Constructor to load the Generate necessary structure of the app.
   * 
   * @throws IOException
   *           one of the necessary files isn't existing
   * @throws JDOMException
   *           Cannot parse the config file
   * @throws InvocationTargetException
   *                  Cannot start a thread
   * @throws InterruptedException
   *                  Thread is interrupted
   * @throws SAXException
   *                  Cannot parse the config file
   * @throws MaryConfigurationException
   *                  is thrown, when the config data for mary are invalid
   */
  public TalkingHead() throws IOException,
      JDOMException,
      InvocationTargetException,
      InterruptedException,
      SAXException,
      MaryConfigurationException {
    // Load Config
    appConfig = new Configuration("etc\\TalkingHeadConfig.xml");

    // Load the Avatar
    talkingHeadAvatarModel = new Avatar(appConfig.getAvatar());
    talkingHeadAnimator = new Animator(this);

    // Generate Network Connection - Input
    int inputBufferSize = appConfig.getInputBuffer(DEFAULT_NETWORK_BUFFER_SIZE);
    talkingHeadInputConnection = new UDPConnection(inputBufferSize);
    talkingHeadInputConnection.bind(appConfig.getInputHost("ANY"),
        appConfig.getInputPort(DEFAULT_NETWORK_INPUT_PORT));
    talkingHeadInputConnection.addObserver(this);

    // Generate Network Connection - Output
    int outputBufferSize =
        appConfig.getOutputBuffer(DEFAULT_NETWORK_BUFFER_SIZE);
    talkingHeadOutputConnection = new UDPConnection(outputBufferSize);
    talkingHeadOutputConnection.connect(appConfig.getOutputHost("127.0.0.1"),
        appConfig.getOutputPort(DEFAULT_NETWORK_INPUT_PORT + 1));

    // Generate Interpreter
    String xsdFilename = appConfig.getXSDFilename("etc\\bml.xsd");
    talkingHeadBMLParser = new LightweightBMLParser(xsdFilename);

    // Generate BMLExecutor
    talkingHeadBMLExecutor = new BMLExecutor(this);

    // Generate TTS
    try {
      // Get TTS Data
      String ttsIP = appConfig.getTTSIP("127.0.0.1");
      int ttsPort = appConfig.getTTSPort(DEFAULT_TTS_PORT);
      String ttsLocale = appConfig.getTTSLocale(MaryTTSClient.LOCALE_ENG);
      String ttsVoice = appConfig.getTTSVoice("bits3-hsmm");

      talkingHeadTextToSpeechProcessor =
          new MaryTTSClient(ttsIP, ttsPort, ttsLocale, ttsVoice, this);
    } catch (Exception exc) {
      exc.printStackTrace();
      talkingHeadTextToSpeechProcessor = null;
      System.exit(0);
    }

    // Generate Viewer
    talkingHeadViewer = new AvatarViewer();
    talkingHeadViewer.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent winEvt) {
        try {
          release();
        } catch (Exception exc) {
          exc.printStackTrace();
        } finally {
          System.exit(0);
        }
      }
    });
    talkingHeadViewer.setVisible(true);

    talkingHeadAnimator.startAnimation("[CURRENT_ID]",
        talkingHeadAvatarModel.getAnimation("idle"));
  }

  /**
   * Method to tidy up the controller.
   * 
   * @throws IOException
   *           is thrown during the disconnect phase.
   */
  public final void release() throws IOException {
    // Close Connection - Input
    if (talkingHeadInputConnection != null) {
      // THConnection.close();
      talkingHeadInputConnection.disconnect();
      talkingHeadInputConnection = null;
    }

    // Close Connection - Output
    if (talkingHeadOutputConnection != null) {
      // THConnection.close();
      talkingHeadOutputConnection.disconnect();
      talkingHeadOutputConnection = null;
    }

    // Stop BMLExecutor
    if (talkingHeadBMLExecutor != null) {
      talkingHeadBMLExecutor.stop();
      talkingHeadBMLExecutor = null;
    }

    // Stop Animations
    if (talkingHeadAnimator != null) {
      talkingHeadAnimator.stopAnimation("[CURRENT_ID]");
      talkingHeadAnimator = null;
    }
  }

  /**
   * Handles incoming BML Packages & executes BML-Script.
   */
  @Override
  public final void update(final Observable arg0,
      final Object arg1) {
    if (arg0 == talkingHeadInputConnection) {
      // Cast BML-Message
      // BML msg = (BML) arg1;
      BML msg = null;
      try {
        msg = talkingHeadBMLParser.generateBML((String) arg1);
      } catch (JDOMException | IOException e) {
        e.printStackTrace();
      }

      if (msg == null) {
        return;
      }

      // Reset
      talkingHeadAnimator.setCurrentAnimationID("[CURRENT_ID]");

      // Start Execution
      talkingHeadBMLExecutor.start(msg);
    }
  }

  /**
   * Handles events, which are generate by BML execution.
   */
  @Override
  public final void update(final BMLEvent event) {
    switch (event.getType()) {
    case BMLEvent.TYPE_START:
      talkingHeadOutputConnection.send("<event id=\""
                                       + event.getCommand().getID()
                                       + ":start\" />");

      // Get Information
      switch (event.getCommand().getType()) {
      case ITag.TYPE_GESTURE:
        // Cast Gesture
        Gesture eventTag = (Gesture) event.getCommand();

        // Get Animation
        Animation anim =
            talkingHeadAvatarModel.getAnimation(eventTag.getLexeme());

        // Start Animation
        talkingHeadAnimator.startAnimation(event.getCommand().getID(),
            anim);
        break;
      case ITag.TYPE_SPEECH:
        // Cast Command
        Speech speech = (Speech) event.getCommand();

        // Start Speech Output
        talkingHeadViewer.setSpeechDisplay("Speech: "
                                           + speech.getText());
        try {
          if (talkingHeadTextToSpeechProcessor != null) {
            talkingHeadTextToSpeechProcessor.process(event.getCommand().getID(),
                speech.getText());
          } else {
            talkingHeadBMLExecutor.triggerEvent(event.getCommand()
                                                + ":end");
          }
        } catch (Exception exc) {
          exc.printStackTrace();
        }
        break;
      case ITag.TYPE_WAIT:
        break;
      case ITag.TYPE_POINTING:
        // Get Animation
        Animation showAnimation = talkingHeadAvatarModel.getAnimation("show");

        // Start Animation
        talkingHeadAnimator.startAnimation(event.getCommand().getID(),
            showAnimation);
        break;
      default:
        break;
      }
      break;
    case BMLEvent.TYPE_STOP:
      talkingHeadOutputConnection.send("<event id=\""
                                       + event.getCommand().getID()
                                       + ":end\" />");
      talkingHeadAnimator.stopAnimation(event.getCommand().getID());
      break;
    case BMLEvent.TYPE_START_BML:
      if (talkingHeadOutputConnection != null
          && event != null
          && event.getBML() != null) {
        talkingHeadOutputConnection.send("<event id=\""
                                         + event.getBML().getID()
                                         + ":start\" />");
      }
      break;
    case BMLEvent.TYPE_STOP_BML:
      if (event != null
          && event.getBML() != null
          && talkingHeadOutputConnection != null) {
        talkingHeadOutputConnection.send("<event id=\""
                                         + event.getBML().getID()
                                         + ":end\" />");
      }
      break;
      default:
        break;
    }
  }

  /**
   * Handles Events by the TTS Processor.
   */
  @Override
  public final void update(final int type,
      final String processID) {
    if (type == MaryTTSClient.Listener.EVENT_TYPE_START) {
      talkingHeadAnimator.startAnimation(processID,
          talkingHeadAvatarModel.getAnimation("speak"));
      talkingHeadViewer.setStatusDisplay("Status: ["
                                         + processID
                                         + "] speak");
    } else if (type == MaryTTSClient.Listener.EVENT_TYPE_STOP) {
      talkingHeadAnimator.stopAnimation(processID);
      talkingHeadViewer.setSpeechDisplay("Speech: ");
    }
  }

  /**
   * Main method to start and run the applciation.
   * 
   * @param args
   *          program arguments, will not used
   */
  public static void main(final String[] args) {
    // Define App
    TalkingHead talkingHead = null;

    try {
      // Start App
      talkingHead = new TalkingHead();
    } catch (Exception exc) {
      // Print error
      exc.printStackTrace();

      // try tidy up
      try {
        if (talkingHead != null) {
          talkingHead.release();
        }
      } catch (Exception releaseException) {
        releaseException.printStackTrace();
      }
    }
  }

  @Override
  public final void update(final AnimationEvent event) {
    switch (event.getType()) {
    case AnimationEvent.START_EVENT:
      talkingHeadViewer.setStatusDisplay("Status: ["
                                         + event.getID()
                                         + "] ");
      break;
    case AnimationEvent.STOP_EVENT:
      if (talkingHeadBMLExecutor != null) {
        talkingHeadBMLExecutor.triggerEvent(event.getID()
                                            + ":end");
      }
      if (talkingHeadViewer != null) {
        talkingHeadViewer.setStatusDisplay("Status: ");
      }

      break;
    case AnimationEvent.NEXT_FRAME_EVENT:
      // Cast Event
      NextFrameEvent nextFrameEvent = (NextFrameEvent) event;

      // Get Image
      Image frame = talkingHeadAvatarModel.getImage(nextFrameEvent.getFrame());

      // Display Image
      talkingHeadViewer.setAvatarDisplay(frame);
      break;
      default:
        break;
    }
  }
}
