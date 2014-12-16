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
package org.jvoicexml.implementation.talkinghead.animations.model;

/**
 * Describes a keyframe of an animation.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Keyframe {
  /**
   * ID of the Image, which will displayed.
   */
  private int imageID;

  /**
   * Time of the keyframe to execute.
   */
  private int duration;
  
  /**
   * Constructor to set the attributes.
   * 
   * @param imgID id of the displayed image
   * @param frameDuration time, how long the image will displayed
   */
  public Keyframe(final int imgID, final int frameDuration) {
    imageID = imgID;
    duration = frameDuration;
  }

  /**
   * Getter for the image id.
   * 
   * @return the DI of the displayed image
   */
  public final int getImageID() {
    return imageID;
  }
  
  /**
   * Getter for the image display duration.
   * 
   * @return time, how long the image will displayed
   */
  public final int getDuration() {
    return duration;
  }
}
