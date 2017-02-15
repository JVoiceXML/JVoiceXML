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
package org.jvoicexml.talkinghead.bml.data;

/**
 * Basic Definition of an event point.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public abstract class EventPoint {
  /**
   * Constant value of the Event Point Type
   * Describes an Start Event Point.
   */
  public static final int TYPE_START = 0;

  /**
   * Constant value of the Event Point Type
   * Describes an Stop Event Point.
   */
  public static final int TYPE_STOP = 1;

  /**
   * Constant value of the Event Point Type
   * Describes an Wait Event Point.
   */
  public static final int TYPE_WAIT = 2;

  /**
   * Constant value of the Event Point Type
   * Describes an Trigger Event Point.
   */
  public static final int TYPE_TRIGGER = 3;

  /**
   * Type of the EventPoint.
   */
  private int type;

  /**
   * Constructor to set the type of the point.
   * 
   * @param theType of the EventPoint
   */
  public EventPoint(final int theType) {
    type = theType;
  }

  /**
   * Access to the eventpoint type.
   * 
   * @return the value of the event point type
   */
  public final int getType() {
    return type;
  }
}
