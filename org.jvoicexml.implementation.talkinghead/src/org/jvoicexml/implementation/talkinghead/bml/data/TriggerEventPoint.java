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
 * Event Point, which triggers another Event.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class TriggerEventPoint extends EventPoint {
  /**
   * This is the ID of the event.
   */
  private String eventID;

  /**
   * Constructor to set data.
   * 
   * @param theEventID
   *            id of the triggered event
   */
  public TriggerEventPoint(final String theEventID) {
    super(EventPoint.TYPE_TRIGGER);

    eventID = theEventID;
  }

  /**
   * Access to the event id.
   * 
   * @return the id of the triggered event
   */
  public final String getEventID() {
    return eventID;
  }

  /**
   * Sets the id of the triggered event.
   * 
   * @param newEventID the id of the event
   */
  public final void setEventID(final String newEventID) {
    eventID = newEventID;
  }
}
