/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.lightweightbml.xmltags;

/**
 * Abstract class of the bml-command tags.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public abstract class ITag {
  /**
   * Defines the tag as wait tag.
   */
  public static final int TYPE_WAIT = 0;

  /**
   * Defines the tag as gesture tag.
   */
  public static final int TYPE_GESTURE = 1;

  /**
   * Defines the tag as speech tag.
   */
  public static final int TYPE_SPEECH = 2;

  /**
   * Defines the tag as pointing tag.
   */
  public static final int TYPE_POINTING = 3;

  /**
   * Defines the tag as gaze tag.
   */
  public static final int TYPE_GAZE = 4;

  /**
   * Type of the Command.
   */
  private int type;

  /**
   * Identification of the command.
   */
  private String id;

  /**
   * Is the execution required.
   */
  private boolean required;

  /**
   * Information about start time.
   */
  private String start;

  /**
   * Information about end time.
   */
  private String end;

  /**
   * Constructor to sets the basic attributes of a tag.
   * 
   * @param theType
   *          The type of the tag
   * @param theID
   *          the id of the tag
   * @param isRequired
   *          the value of the required flag
   * @param theStart
   *          the start information
   * @param theEnd
   *          the end information
   */
  public ITag(final int theType,
      final String theID,
      final boolean isRequired,
      final String theStart,
      final String theEnd) {
    type = theType;
    id = theID;
    required = isRequired;
    start = theStart;
    end = theEnd;
  }

  /**
   * Access to the command type.
   * 
   * @return the type of the command
   */
  public final int getType() {
    return type;
  }

  /**
   * Access to the command id.
   * 
   * @return the id of the command
   */
  public final String getID() {
    return id;
  }

  /**
   * Information about the exuction requirement.
   * 
   * @return the value of the execution requirement
   */
  public final boolean isRequired() {
    return required;
  }

  /**
   * Access to the start time of the command.
   * 
   * @return the start time
   */
  public final String getStart() {
    return start;
  }

  /**
   * Access to the end time of the command.
   * 
   * @return the end time
   */
  public final String getEnd() {
    return end;
  }

  /**
   * Sets the tag as required tag.
   * 
   * @param isRequired the new value for the required attribute
   */
  public final void setRequired(final boolean isRequired) {
    required = isRequired;
  }

  /**
   * Sets the ID of the tag.
   * 
   * @param newID the new id of the tag
   */
  public final void setID(final String newID) {
    id = newID;
  }

  /**
   * Sets the start information of the tag.
   * 
   * @param newStart new start attribute of the tag.
   */
  public final void setStart(final String newStart) {
    start = newStart;
  }

  /**
   * Sets the end information of the tag.
   * 
   * @param newEnd new end attribute of the tag 
   */
  public final void setEnd(final String newEnd) {
    end = newEnd;
  }
}
