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
package org.jvoicexml.talkinghead.animations.model;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

/**
 * Definiton of an Animation.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Animation {
  /**
   * Type constant to define an animation
   * as transition (the animation stops automatically).
   */
  public static final int TYPE_TRANSITION = 0;

  /**
   * Type constant to define a animation
   * as state (the animation is running in a loop).
   */
  public static final int TYPE_STATE = 1;

  /**
   * ID of the Animation.
   */
  private String id;

  /**
   * Will the Animation end.
   */
  private int type;

  /**
   * Keyframes of the animation.
   */
  private List<Keyframe> keyframes;

  /**
   * Constructor, which sets default values to the class attributes.
   */
  public Animation() {
    id = "";
    type = 0;

    keyframes = new LinkedList<Keyframe>();
  }

  /**
   * Reads the Animation from XML-Node.
   * 
   * @param element
   *          xml element, which describes the animation
   */
  public final void read(final Element element) {
    if (element == null) {
      return;
    }

    // Read ID of the element
    id = element.getAttributeValue("id");
    if (id == null) {
      id = "";
    }
    
    //Read animation type
    String idAttrValue = element.getAttributeValue("type", "transition"); 
    if (idAttrValue.compareTo("transition") == 0) {
      type = TYPE_TRANSITION;
    } else {
        type = TYPE_STATE;
    }

    // Read Keyframes
    List<Element> keyframeElements = element.getChildren("key");
    for (Element frame : keyframeElements) {
      // Read Data
      String imgID = frame.getAttributeValue("img");
      String duration = frame.getAttributeValue("duration");

      // Generate Animation Set
      int imgIDValue = Integer.parseInt(imgID);
      int durationValue = Integer.parseInt(duration);
      Keyframe keyframe = new Keyframe(imgIDValue, durationValue);

      // Adding frame
      addKeyframe(keyframe);
    }
  }

  /**
   * Adds a new Keyframe to the animation.
   * 
   * @param frame
   *          keyframe for the animation
   */
  public final void addKeyframe(final Keyframe frame) {
    keyframes.add(frame);
  }

  /**
   * get access to a specific keyframe.
   * 
   * @param idx
   *          index of the keyframe
   * @return the specific keyframe with index idx
   */
  public final Keyframe getKeyframe(final int idx) {
    return keyframes.get(idx);
  }

  /**
   * get access to the animation id.
   * 
   * @return the id of the animation
   */
  public final String getID() {
    return id;
  }

  /**
   * get access to the timesToRepeat parameter.
   * 
   * @return the value how often the animation will be repeated
   */
  public final int getType() {
    return type;
  }

  /**
   * Access to the number of keyframes.
   * 
   * @return the number of keyframes
   */
  public final int getKeyframeCount() {
    return keyframes.size();
  }
}
