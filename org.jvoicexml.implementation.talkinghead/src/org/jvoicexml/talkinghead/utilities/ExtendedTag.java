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
package org.jvoicexml.talkinghead.utilities;

import org.jvoicexml.implementation.lightweightbml.utilities.ParseUtils;
import org.jvoicexml.implementation.lightweightbml.xmltags.ITag;

/**
 * Extends the ITag class of the bml interface with parsed Time Data.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class ExtendedTag extends ITag {
  /**
   * Parsed Data of the start attribute.
   */
  private TimeData startData;

  /**
   * Parsed Data of the end attribute.
   */
  private TimeData endData;
  
  /**
   * Tag, which is decorated.
   */
  private ITag decoratedTag;

  /**
   * Constructor, which parses the data of the start and end attributes and
   * stores the data.
   * 
   * @param command
   *            command, which will be decorated
   */
  public ExtendedTag(final ITag command) {
    super(command.getType(), command.getID(), command.isRequired(), command
        .getStart(), command.getEnd());
    
    decoratedTag = command;

    // Parse Start
    startData = new TimeData();
    int start = ParseUtils.parseInt(command.getStart());
    if (start == ParseUtils.PARSE_ERROR_NOT_SET) {
      startData.setType(TimeData.TYPE_TIME_POINT);
      startData.setPoint(0);
      startData.setReferenceID("");
      startData.setReferencePoint("");
      startData.setOffset(0);
    } else if (start == ParseUtils.PARSE_ERROR_NO_INTEGER) {
      // Split
      String[] startIDCommand = command.getStart().split(":");
      String[] startCommandTime = startIDCommand[1].split("\\+");

      // Set Attributes
      startData.setType(TimeData.TYPE_TIME_REFERENCE);
      startData.setPoint(0);
      startData.setReferenceID(startIDCommand[0]);
      startData.setReferencePoint(startCommandTime[0]);
      if (startCommandTime.length == 2) {
        int parsedTime = ParseUtils.parseInt(startCommandTime[1]);
        
        startData.setOffset(Math.max(parsedTime, 0));
      } else {
        startData.setOffset(0);
      }
    } else {
      startData.setType(TimeData.TYPE_TIME_POINT);
      startData.setPoint(start);
      startData.setReferenceID("");
      startData.setReferencePoint("");
      startData.setOffset(0);
    }

    // Parse End
    endData = new TimeData();
    int end = ParseUtils.parseInt(command.getEnd());
    if (end == ParseUtils.PARSE_ERROR_NOT_SET) {
      endData.setType(TimeData.TYPE_TIME_NOT_SET);
      endData.setPoint(0);
      endData.setReferenceID("");
      endData.setReferencePoint("");
      endData.setOffset(0);
    } else if (end == ParseUtils.PARSE_ERROR_NO_INTEGER) {
      // Split
      String[] startIDCommand = command.getEnd().split(":");
      String[] startCommandTime = startIDCommand[1].split("\\+");

      // Set Attributes
      endData.setType(TimeData.TYPE_TIME_REFERENCE);
      endData.setPoint(0);
      endData.setReferenceID(startIDCommand[0]);
      endData.setReferencePoint(startCommandTime[0]);
      if (startCommandTime.length == 2) {
        int parsedTime = ParseUtils.parseInt(startCommandTime[1]);
        
        endData.setOffset(Math.max(parsedTime, 0));
      } else {
        endData.setOffset(0);
      }
    } else {
      endData.setType(TimeData.TYPE_TIME_POINT);
      endData.setPoint(end);
      endData.setReferenceID("");
      endData.setReferencePoint("");
      endData.setOffset(0);
    }
  }

  /**
   * Access to the Start Data.
   * 
   * @return Parsed Data of the start attributes
   */
  public final TimeData getStartData() {
    return startData;
  }

  /**
   * Sets the Start Data.
   * 
   * @param start
   *            Parsed Data of the start attribute
   */
  public final void setStartData(final TimeData start) {
    startData = start;
  }

  /**
   * Access to the End Data.
   * 
   * @return Parsed data of the end attributes
   */
  public final TimeData getEndData() {
    return endData;
  }

  /**
   * Sets the End Data.
   * 
   * @param end
   *            Parsed data of the end Attribute
   */
  public final void setEndData(final TimeData end) {
    endData = end;
  }
  
  /**
   * Getter for the decorated tag.
   * 
   * @return the decorated tag
   */
  public final ITag getDecoratedTag() {
    return decoratedTag;
  }
}
