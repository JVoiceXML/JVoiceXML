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
 * Event, which told the Executor to wait for execution.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class WaitEventPoint extends EventPoint {
  /**
   * ID of the event, which will executed after waiting.
   */
  private String eventID;

  /**
   * Time to wait until the next event.
   */
  private long duration;

  /**
   * Constructor to set the data.
   * 
   * @param theEventID
   *            id of the event, which will triggered after waiting
   * @param theDuration
   *            Time to wait
   */
  public WaitEventPoint(final String theEventID, final long theDuration) {
    super(EventPoint.TYPE_WAIT);

    eventID = theEventID;
    duration = theDuration;
  }

  /**
   * Access to the event id.
   * 
   * @return id of the event
   */
  public final String getEventID() {
    return eventID;
  }

  /**
   * Sets the id of the event.
   * 
   * @param newEventID
   *            id of the event
   */
  public final void setEventID(final String newEventID) {
    eventID = newEventID;
  }

  /**
   * Access to the time to wait.
   * 
   * @return time to wait
   */
  public final long getDuration() {
    return duration;
  }

  /**
   * Sets the time to wait.
   * 
   * @param newDuration
   *            value of the time
   */
  public final void setDuration(final long newDuration) {
    duration = newDuration;
  }
}
