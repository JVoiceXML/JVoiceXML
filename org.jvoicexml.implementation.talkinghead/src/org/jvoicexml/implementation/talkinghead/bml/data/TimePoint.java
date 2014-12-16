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
package org.jvoicexml.implementation.talkinghead.bml.data;

/**
 * Defines an trigger TimePoint for BMLExecution.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class TimePoint implements Comparable<TimePoint> {
  /**
   * Time, when the Event will triggered.
   */
  private long globalTime;

  /**
   * ID of the event which will triggered.
   */
  private String event;

  /**
   * Constructor to set the data.
   * 
   * @param theGlobalTime
   *            Time, when the event will triggered
   * @param theEvent
   *            id of the event, which will triggered
   */
  public TimePoint(final long theGlobalTime, final String theEvent) {
    globalTime = theGlobalTime;
    event = theEvent;
  }

  /**
   * Access to the trigger time point.
   * 
   * @return the global time information when the event will triggered
   */
  public final long getGlobalTime() {
    return globalTime;
  }

  /**
   * Sets the global time of the TimePoint.
   * 
   * @param newGlobalTime
   *            the global time information when the event will triggered
   */
  public final void setGlobalTime(final long newGlobalTime) {
    globalTime = newGlobalTime;
  }

  /**
   * Access to the event id.
   * 
   * @return id of the triggered event
   */
  public final String getEvent() {
    return event;
  }

  /**
   * sets the event id.
   * 
   * @param newEvent
   *            triggered event id
   */
  public final void setEvent(final String newEvent) {
    event = newEvent;
  }

  @Override
  public final int compareTo(final TimePoint arg1) {
    if (this.getGlobalTime() < arg1.getGlobalTime()) {
      return -1;
    } else if (this.getGlobalTime() > arg1.getGlobalTime()) {
      return 1;
    } else {
      return 0;
    }
  }
}
