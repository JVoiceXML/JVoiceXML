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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.jvoicexml.implementation.talkinghead.bml.data.EventPoint;
import org.jvoicexml.implementation.talkinghead.bml.data.TimePoint;

/**
 * Container, which holds all events created from a bml-file to respond to
 * occurred events and trigger new events.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class BMLEventContainer {
  /**
   * List of TimePoints.
   */
  private LinkedList<TimePoint> timePoints;

  /**
   * List of Animations, which will executed after a specific event.
   */
  private HashMap<String, LinkedList<EventPoint>> eventPoints;

  /**
   * Constructor to initize Lists.
   */
  public BMLEventContainer() {
    timePoints = new LinkedList<TimePoint>();
    eventPoints = new HashMap<String, LinkedList<EventPoint>>();
  }

  /**
   * Checks the list of emptyness.
   * 
   * @return true, when both lists are empty
   */
  public final boolean isEmpty() {
    boolean result = false;

    synchronized (timePoints) {
      synchronized (eventPoints) {
        result = timePoints.isEmpty() && eventPoints.isEmpty();
      }
    }

    return result;
  }

  /**
   * Adds a time point to the container.
   * 
   * @param point a timepoint, where an event should start
   */
  public final void addTimePoint(final TimePoint point) {
    synchronized (timePoints) {
      timePoints.add(point);
    }
  }
  
  /**
   * Adds a time point and sorts the complete list.
   * 
   * @param point a given timepoint, where an event should start
   */
  public final void addSortedTimePoint(final TimePoint point) {
    synchronized (timePoints) {
      //Add Point
      timePoints.add(point);
      
      //Sort List
      Collections.sort(timePoints);
    }
  }

  /**
   * Removes and returns a TimePoint from list, which has a less or
   * equal current global time.
   * 
   * @param globalTime current global time
   * @return the timepoint, which fullfilled the requirements,
   *         otherwise returns null
   */
  public final TimePoint getTimePoint(final long globalTime) {
    TimePoint result = null;

    synchronized (timePoints) {
      if (!timePoints.isEmpty()
          && timePoints.getFirst().getGlobalTime() <= globalTime) {
        result = timePoints.removeFirst();
      }
    }

    return result;
  }

  /**
   * Adds an event point to the container.
   * 
   * @param key name of the event
   * @param event the event point
   */
  public final void addEventPoint(final String key, final EventPoint event) {
    synchronized (eventPoints) {
      if (!eventPoints.containsKey(key)) {
        eventPoints.put(key, new LinkedList<EventPoint>());
      }

      eventPoints.get(key).add(event);
    }
  }

  /**
   * Removes and returns the event point with given key.
   * 
   * @param key of the event point
   * @return a list of events, which want to be triggered next
   */
  public final LinkedList<EventPoint> getEventPoint(final String key) {
    LinkedList<EventPoint> result = null;

    synchronized (eventPoints) {
      if (!eventPoints.isEmpty()) {
        result = eventPoints.remove(key);
      }
    }

    return result;
  }

  /**
   * Clears the whole container.
   */
  public final void clear() {
    // Clear Event Points
    synchronized (eventPoints) {
      eventPoints.clear();
    }

    // Clear Time Points
    synchronized (timePoints) {
      timePoints.clear();
    }
  }
  
  /**
   * Sorts the time point by their global time information.
   */
  public final void sortTimePoints() {
    synchronized (timePoints) {
      Collections.sort(timePoints);
    }
  }
}
