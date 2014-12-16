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
package org.jvoicexml.implementation.talkinghead.utilities;

/**
 * Holds the parsed data of the ExtendedTag Attributes.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class TimeData {
  /**
   * Constant value of the Start Type It describes a time point.
   */
  public static final int TYPE_TIME_POINT = 0;

  /**
   * Constant value of the Start Type It describes time point, which is
   * referenced to another point.
   */
  public static final int TYPE_TIME_REFERENCE = 1;

  /**
   * Constant value of the Time Type It describes, that the data isn't set
   * or it is unknown.
   */
  public static final int TYPE_TIME_NOT_SET = 2;

  /**
   * Type of the data.
   */
  private int type;

  /**
   * Time Point.
   */
  private int point;

  /**
   * ID of the Reference Point.
   */
  private String referenceID;

  /**
   * Reference Point.
   */
  private String referencePoint;

  /**
   * Offset to the reference point.
   */
  private int offset;

  /**
   * Constructor to set default values.
   */
  public TimeData() {
    type = TYPE_TIME_NOT_SET;
    point = 0;
    referenceID = "";
    referencePoint = "";
    offset = 0;
  }

  /**
   * Getter for the time type.
   * 
   * @return the type of the time data
   */
  public final int getType() {
    return type;
  }

  /**
   * Setter for the time data type.
   * 
   * @param newType the new type of the time data
   */
  public final void setType(final int newType) {
    type = newType;
  }

  /**
   * Getter for the time point, is set, when type specifies an time point.
   * 
   * @return the global time value
   */
  public final int getPoint() {
    return point;
  }

  /**
   * Setter for the global time point.
   * 
   * @param newTimePoint the new global time point
   */
  public final void setPoint(final int newTimePoint) {
    point = newTimePoint;
  }

  /**
   * Getter for the reference time point, is set when the time has a reference
   * to another element time point.
   * 
   * @return the reference to an another time point
   */
  public final String getReferenceID() {
    return referenceID;
  }

  /**
   * Setter for the reference id.
   * 
   * @param newReferenceID the element reference, which specifies the time point
   */
  public final void setReferenceID(final String newReferenceID) {
    referenceID = newReferenceID;
  }

  /**
   * Getter for the reference point (e.g. start, end) of an reference.
   * 
   * @return the time point id
   */
  public final String getReferencePoint() {
    return referencePoint;
  }

  /**
   * Setter for the time point id.
   * 
   * @param newReferencePoint the new time point id (e.g. start, end)
   */
  public final void setReferencePoint(final String newReferencePoint) {
    referencePoint = newReferencePoint;
  }

  /**
   * Getter for the offset to a given time point.
   * 
   * @return the offset to a given time point
   */
  public final int getOffset() {
    return offset;
  }

  
  /**
   * Setter for the offset.
   * 
   * @param newOffset the new offset
   */
  public final void setOffset(final int newOffset) {
    offset = newOffset;
  }
}
