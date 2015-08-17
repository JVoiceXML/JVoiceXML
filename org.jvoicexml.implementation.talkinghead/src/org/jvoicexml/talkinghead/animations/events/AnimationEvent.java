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
package org.jvoicexml.talkinghead.animations.events;

/**
 * Abstract event structure for events sending by the animator.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public abstract class AnimationEvent {
  /**
   * Event type constant, to define the event as a start event.
   */
  public static final int START_EVENT = 0;
  
  /**
   * Event type constant, to define the event as a stop event.
   */
  public static final int STOP_EVENT = 1;
  
  /**
   * Event type constant, to define the event as a next frame event.
   */
  public static final int NEXT_FRAME_EVENT = 2;

  /**
   * Holds the information about the event type.
   */
  private int type;

  /**
   * Stores the BML id of the animation.
   */
  private String id;

  /**
   * Constructor to define the attributes of the event structure.
   * 
   * @param eventType type of the event
   * @param bmlID BML id of the currently executed animation
   */
  public AnimationEvent(final int eventType, final String bmlID) {
    this.type = eventType;
    this.id = bmlID;
  }

  /**
   * Returns the BML id to specific the event.
   * 
   * @return the id/name of the bml command
   */
  public final String getID() {
    return id;
  }

  /**
   * Returns the type of the event.
   * 
   * @return value to specify the type of the event
   */
  public final int getType() {
    return type;
  }
}
