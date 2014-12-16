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
package org.jvoicexml.implementation.talkinghead.animations;

import java.util.Timer;
import java.util.TimerTask;

import org.jvoicexml.implementation.talkinghead.animations.events.AnimationEvent;
import org.jvoicexml.implementation.talkinghead.animations.events.NextFrameEvent;
import org.jvoicexml.implementation.talkinghead.animations.events.StartAnimationEvent;
import org.jvoicexml.implementation.talkinghead.animations.events.StopAnimationEvent;
import org.jvoicexml.implementation.talkinghead.animations.model.Animation;
import org.jvoicexml.implementation.talkinghead.animations.model.Keyframe;

/**
 * Executes and handles the animations of the avatar.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Animator {
  /**
   * Listener to the Animator.
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
     * Method to notify the listener for a new AnimationEvent.
     * 
     * @param event the given AnimationEvent
     */
    void update(AnimationEvent event);
  }

  /**
   * ID of the current Animation.
   */
  private String currentAnimationID;

  /**
   * Current executed Animation.
   */
  private Animation currentAnimation;

  /**
   * Frame Index, which is currently displayed.
   */
  private int currentKeyframeIndex;

  /**
   * Timer, which controls the time, how long a frame will displayed.
   */
  private Timer frameTimer;

  /**
   * Event Listener for notify animation events.
   */
  private Listener eventListener;

  /**
   * Constructor to set basic attributes.
   */
  public Animator() {
    // Set Attributes
    currentAnimation = null;
    currentAnimationID = "";
    currentKeyframeIndex = 0;
    frameTimer = null;
    eventListener = null;
  }

  /**
   * Constructor, for Initizing the Animator.
   * 
   * @param listener
   *          listener to the animator events
   */
  public Animator(final Listener listener) {
    this();
    eventListener = listener;
  }

  /**
   * Starts a given animation.
   * 
   * @param id ID of the bml command for this animation
   * @param animation the animation to execute
   */
  public final synchronized void startAnimation(final String id,
      final Animation animation) {
    // Check Parameter
    if (animation == null) {
      throw new IllegalArgumentException("Cannot handle [null]-Animation");
    }

    // Stops the animation first
    stopAnimation(currentAnimationID);

    // Set Values
    currentAnimationID = id;
    currentAnimation = animation;
    currentKeyframeIndex = 0;

    // Stop FrameTimer
    animate();

    // Notify Start Event
    if (eventListener != null) {
      eventListener.update(new StartAnimationEvent(currentAnimationID));
    }
  }
  
  /**
   * Stops the currently executed animation.
   * 
   * @param id BML command ID of this animation
   */
  public final synchronized void stopAnimation(final String id) {
    if (id.compareTo(currentAnimationID) != 0) {
      return;
    }

    // Stop Timers
    if (frameTimer != null) {
      frameTimer.cancel();
      frameTimer = null;
    }

    // Set Attributes
    String test = currentAnimationID;
    currentAnimationID = "";
    currentAnimation = null;
    currentKeyframeIndex = 0;

    // Notify
    if (eventListener != null) {
      eventListener.update(new StopAnimationEvent(test));
    }
  }

  /**
   * Steps forward the animation and notifies the next image.
   */
  private void animate() {
    // Stop FrameTimer
    if (frameTimer != null) {
      frameTimer.cancel();
      frameTimer = null;
    }

    // Get Keyframe
    Keyframe frame = currentAnimation.getKeyframe(currentKeyframeIndex);

    // Notify Changes
    if (eventListener != null) {
      //Generate Event
      NextFrameEvent nextFrameEvent =
          new NextFrameEvent(frame.getImageID(), currentAnimationID);
      
      //Post event
      eventListener.update(nextFrameEvent);
    }

    // Start Timer
    if (frame.getDuration() != -1) {
      frameTimer = new Timer();
      frameTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            // Generate New Frame Index
            currentKeyframeIndex += 1;
            synchronized (currentAnimation) {
              if (currentAnimation == null) {
                return;
              }

              // Check
              if (currentAnimation.getType() == Animation.TYPE_STATE) {
                // Correct Information & execute
                currentKeyframeIndex = currentKeyframeIndex
                                       % currentAnimation.getKeyframeCount();
                animate();
              } else {
                if (currentAnimation.getType() == Animation.TYPE_TRANSITION) {
                  if (currentKeyframeIndex
                      >= currentAnimation.getKeyframeCount()) {
                    stopAnimation(currentAnimationID);
                  } else {
                    animate();
                  }
              }
            }

            }

          } catch (Exception exc) {
            System.err.println(currentAnimation);
            System.err.println(currentKeyframeIndex);
            exc.printStackTrace();
          }
        }
      },
          frame.getDuration());
    }
  }

  /**
   * Sets the BML id of the currently executed animation.
   * 
   * @param currentAnimID BML id of the animation.
   */
  public final void setCurrentAnimationID(final String currentAnimID) {
    this.currentAnimationID = currentAnimID;
  }

  /**
   * Access to the current used EventListener.
   * 
   * @return listener to the animator events
   */
  public final Listener getEventListener() {
    return eventListener;
  }

  /**
   * sets the event listener.
   * 
   * @param evtListener
   *          listener to the animator events
   */
  public final void setEventListener(final Listener evtListener) {
    this.eventListener = evtListener;
  }
}
