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
package org.jvoicexml.talkinghead.bml;

import java.util.LinkedList;

import org.jvoicexml.implementation.lightweightbml.xmltags.BML;
import org.jvoicexml.implementation.lightweightbml.xmltags.Constraint;
import org.jvoicexml.implementation.lightweightbml.xmltags.ITag;
import org.jvoicexml.talkinghead.bml.data.EventPoint;
import org.jvoicexml.talkinghead.bml.data.StartEventPoint;
import org.jvoicexml.talkinghead.bml.data.StopEventPoint;
import org.jvoicexml.talkinghead.bml.data.TimePoint;
import org.jvoicexml.talkinghead.bml.data.TriggerEventPoint;
import org.jvoicexml.talkinghead.bml.data.WaitEventPoint;
import org.jvoicexml.talkinghead.bml.events.BMLEvent;
import org.jvoicexml.talkinghead.utilities.ExtendedTag;
import org.jvoicexml.talkinghead.utilities.TimeData;

/**
 * Handles the execution of BML commands.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class BMLExecutor
    implements BMLTimer.Listener {
  /**
   * Internal class, to describe an Listener for Execution Events.
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
     * Handler of the execution events.
     * 
     * @param event
     *          Execution event, which will notified
     */
    void update(BMLEvent event);
  }

  /**
   * Execution Listener, which executes the emitted events.
   */
  private Listener eventListener;

  /**
   * BML Data, which will executed.
   */
  private BML bmlTree;

  /**
   * Container to store the different events.
   */
  private BMLEventContainer containerOfEvents;

  /**
   * List of Warnings.
   */
  private LinkedList<String> listOfWarnings;

  /**
   * Timer to trigger timed BML events.
   */
  private BMLTimer executionTimer;

  /**
   * Constructor to set default values to the Attributes.
   */
  public BMLExecutor() {
    // Set Attributes
    eventListener = null;
    bmlTree = null;

    // Init CLasses
    executionTimer = new BMLTimer();
    containerOfEvents = new BMLEventContainer();
    listOfWarnings = new LinkedList<String>();
  }

  /**
   * Constructor to set an event listener.
   * 
   * @param listener
   *          event listener to handle BML execution events
   */
  public BMLExecutor(final Listener listener) {
    // Set Attributes
    this();
    eventListener = listener;
  }

  /**
   * Stops a running execution and starts an new one with given parameter.
   * 
   * @param bml
   *          new bml data to execute, is the value null, 
   *          then the current bml execution will stop
   */
  public final void start(final BML bml) {
    // Stop Current Execution
    stop();

    // Set Attributes & Check
    bmlTree = bml;
    if (bmlTree == null) {
      return;
    }

    // Can BML execute
    if (!validateBML()) {
      // Set Global Warning
      listOfWarnings.add("BML cannot executed");

      // TODO: Notify Warnings
    }

    // Notify Warnings
    if (!listOfWarnings.isEmpty()) {
      // TODO: Notify Warnings
    }

    // Convert BML to Execution Data
    convertBML2ExecutionData();

    // Start execution
    executionTimer.start(this);

    // Notify
    if (eventListener != null) {
      eventListener.update(new BMLEvent(BMLEvent.TYPE_START_BML, bmlTree));
    }
  }

  /**
   * Stops the current executed bml-data.
   */
  public final void stop() {
    // Stop Timer
    executionTimer.stop();

    // Tidy Up
    listOfWarnings.clear();
    containerOfEvents.clear();
  }

  /**
   * Runs the TimeThread.
   */
  @Override
  public final void tick(final long currentGlobalTime) {
    TimePoint first = containerOfEvents.getTimePoint(currentGlobalTime);
    if (first != null) {
      triggerEvent(first.getEvent());
    }
  }

  /**
   * Triggers a specific event from inside the class or from outside.
   * 
   * @param eventName
   *          name of the event
   */
  public final void triggerEvent(final String eventName) {
    // Get EventPoints of this event
    LinkedList<EventPoint> events = containerOfEvents.getEventPoint(eventName);
    if (events == null) {
      return;
    }

    // Execute EventPoints
    for (EventPoint e : events) {
      switch (e.getType()) {
      case EventPoint.TYPE_START:
        // Cast
        StartEventPoint startPoint = (StartEventPoint) e;

        // Notify
        if (eventListener != null) {
          //Generate Event
          BMLEvent evt = new BMLEvent(BMLEvent.TYPE_START,
              startPoint.getCommand().getDecoratedTag());
          
          //Notify event
          eventListener.update(evt);
        }
        break;
      case EventPoint.TYPE_STOP:
        // Cast
        StopEventPoint stopPoint = (StopEventPoint) e;

        // Notify
        if (eventListener != null) {
          //Generate Event
          BMLEvent evt = 
              new BMLEvent(BMLEvent.TYPE_STOP,
                           stopPoint.getCommand().getDecoratedTag());
          
          //Post event
          eventListener.update(evt);
        }
        break;
      case EventPoint.TYPE_WAIT:
        // Cast
        WaitEventPoint waitPoint = (WaitEventPoint) e;
        
        //Calculate time point
        long timePointValue = executionTimer.getGlobalTime()
            + waitPoint.getDuration();

        // Generate Time Point
        TimePoint timePoint = new TimePoint(timePointValue,
                                            waitPoint.getEventID());
        
        //Add to container
        containerOfEvents
            .addSortedTimePoint(timePoint);
        break;
      case EventPoint.TYPE_TRIGGER:
        // Cast
        TriggerEventPoint triggerPoint = (TriggerEventPoint) e;

        // Trigger Event
        triggerEvent(triggerPoint.getEventID());
        break;
        default:
          break;
      }
    }

    // Notify
    if (containerOfEvents.isEmpty()) {
      // Notify the stop
      if (eventListener != null) {
        eventListener.update(new BMLEvent(BMLEvent.TYPE_STOP_BML, bmlTree));
      }

      stop();

    }
  }

  /**
   * Validates the given BML Informations.
   * 
   * @return true if the BML is valid, false if the BML isn't valid and cannot
   *         executed
   */
  protected final boolean validateBML() {
    // Check Constraints [Constraints aren't supported]
    if (!bmlTree.getConstraints().isEmpty()) {
      listOfWarnings.add("Constraints aren't supported");
    }

    // Check for required constraints
    for (Constraint c : bmlTree.getConstraints()) {
      // TODO: Required field
    }

    // Generate Warnings for unsupported required Commands
    for (ITag cmd : bmlTree.getCommands()) {
      // Gaze commands aren't supported
      if (cmd.getType() == ITag.TYPE_GAZE) {
        // Set Warning
        listOfWarnings.add("Required command \"gaze\" is not supported");

        // When required stop execution
        if (cmd.isRequired()) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Converts the BML Data to execution data.
   */
  protected final void convertBML2ExecutionData() {
    // Parse Command Information
    LinkedList<ExtendedTag> extendedTags = new LinkedList<ExtendedTag>();
    for (ITag command : bmlTree.getCommands()) {
      extendedTags.add(new ExtendedTag(command));
    }

    // Generate Events
    for (ExtendedTag cmd : extendedTags) {
      // Generate & Set Start Event
      String startEventName = cmd.getID()
                                + ":start";
      containerOfEvents.addEventPoint(startEventName,
          new StartEventPoint(cmd));

      // Generate & Set Stop Events
      String stopEventName = cmd.getID()
                               + ":end";
      if (cmd.getEndData().getType() != TimeData.TYPE_TIME_NOT_SET) {
        containerOfEvents.addEventPoint(stopEventName,
            new StopEventPoint(cmd));
      }

      // Generate Start Time Event
      if (cmd.getStartData().getType() == TimeData.TYPE_TIME_POINT) {
        containerOfEvents.addTimePoint(
            new TimePoint(cmd.getStartData().getPoint(), startEventName));
      } else {
        // Generate Event Name
        String eventName = cmd.getStartData().getReferenceID()
                            + ":"
                            + cmd.getStartData().getReferencePoint();

        // Generate Event Point
        if (cmd.getStartData().getOffset() == 0) {
          containerOfEvents.addEventPoint(eventName,
              new TriggerEventPoint(startEventName));
        } else {
          containerOfEvents.addEventPoint(eventName,
              new WaitEventPoint(startEventName,
                                 cmd.getStartData().getOffset()));
        }
      }

      // Generate End Time Event
      if (cmd.getEndData().getType() == TimeData.TYPE_TIME_POINT) {
        //Generate TimePoint
        TimePoint timePoint = new TimePoint(cmd.getEndData().getPoint(),
                                            stopEventName);
        
        //Add time point to container
        containerOfEvents.addTimePoint(timePoint);
      } else if (cmd.getEndData().getType() != TimeData.TYPE_TIME_NOT_SET) {
        // Generate Event Name
        String eventName = cmd.getEndData().getReferenceID()
                            + ":"
                            + cmd.getEndData().getReferencePoint();

        // Generate Event Point
        if (cmd.getEndData().getOffset() == 0) {
          containerOfEvents.addEventPoint(eventName,
              new TriggerEventPoint(stopEventName));
        } else {
          containerOfEvents.addEventPoint(eventName,
              new WaitEventPoint(stopEventName, cmd.getEndData().getOffset()));
        }
      }
    }

    // Sort TimePoints
    containerOfEvents.sortTimePoints();
  }
}
