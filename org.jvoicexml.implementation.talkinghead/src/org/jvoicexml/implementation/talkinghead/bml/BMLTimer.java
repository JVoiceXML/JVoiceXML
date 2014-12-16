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
package org.jvoicexml.implementation.talkinghead.bml;

/**
 * Timer to count a global time variable to trigger timed bml events.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class BMLTimer
    implements Runnable {
  /**
   * Timer to count a global time variable to trigger timed bml events.
   * 
   * @author Matthias Mettel
   * @author Markus Ermuth
   * @author Alex Krause
   * 
   * @version $LastChangedRevision$
   * @since 0.7.3
   *
   */
  public interface Listener {
    /**
     * Notifies about the current global time.
     * 
     * @param currentGlobalTime the current global time of the timer
     */
    void tick(long currentGlobalTime);
  };

  /**
   * Thread to time the animation execution.
   */
  private Thread timerThread;

  /**
   * Start Time of the Timer.
   */
  private long startTime;

  /**
   * Global Time Information.
   */
  private long globalTime;

  /**
   * Listener for Time Events.
   */
  private Listener listener;

  /**
   * Running flag to control the timer thread.
   */
  private boolean isRunning;

  /**
   * Constructor to set default values to the attributes.
   */
  public BMLTimer() {
    timerThread = null;
    startTime = 0;
    globalTime = 0;
    isRunning = false;
  }

  /**
   * Starts the timer.
   * 
   * @param newListener to listen for current global time information
   */
  public final void start(final Listener newListener) {
    // Check Parameter
    if (newListener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }

    // Check Thread Status
    if (isRunning) {
      stop();
    }

    // Start Thread
    listener = newListener;
    isRunning = true;
    timerThread = new Thread(this);
    timerThread.start();
  }

  /**
   * Getter for the current global time.
   * @return the current global time value
   */
  public final long getGlobalTime() {
    return globalTime;
  }

  /**
   * Stops the timer.
   */
  public final void stop() {
    if (timerThread != null) {
      isRunning = false;
      try {
        timerThread.interrupt();
      } catch (Exception exc) {
        exc.printStackTrace();
      }
      timerThread = null;
    }
  }

  /**
   * Timer thread method, which checks of elapsed time and notifies the current
   * global time.
   */
  @Override
  public final void run() {
    // Set Attributes
    startTime = System.currentTimeMillis();
    globalTime = 0;

    // Run
    while (isRunning) {
      // Get Current Time as EndTime
      long endTime = System.currentTimeMillis();

      // Calculate Time Difference & Add to Current Global Time
      long timeDiff = endTime
                       - startTime;
      globalTime += timeDiff;

      // Execute Tick
      if (listener != null) {
        listener.tick(globalTime);
      }

      // Update Attributes
      if (timeDiff != 0) {
        startTime = System.currentTimeMillis();
      }
    }

    // Tidy Up
    timerThread = null;
    isRunning = false;
  }
}
