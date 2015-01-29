/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-20154 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Represents a xml-tag to let the avatar wait/do nothing.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class Wait
    extends ITag {
  /**
   * Duration of the command.
   */
  private String duration;

  /**
   * Constructor to set the attributes.
   * 
   * @param id
   *          the id of command
   * @param required
   *          is the command required for execution
   * @param start
   *          start time of the command
   * @param end
   *          end time of the command
   * @param theDuration
   *          the time, how long the avatar will wait
   */
  public Wait(final String id,
      final boolean required,
      final String start,
      final String end,
      final String theDuration) {
    super(ITag.TYPE_WAIT, id, required, start, end);

    duration = theDuration;
  }

  /**
   * Sets the duration of the command.
   * 
   * @param newDuration the new duration in milliseconds
   */
  public final void setDuration(final String newDuration) {
    duration = newDuration;
  }
  
  /**
   * Access to the duration time.
   * 
   * @return the duration of the wait command
   */
  public final String getDuration() {
    return duration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    if (duration == null
        || duration.compareTo("") == 0
        || duration.compareTo("0") == 0) {
      return "<wait id=\""
             + getID()
             + "\" start=\""
             + getStart()
             + "\" duration=\""
             + duration
             + "\" />";
    } else {
      return "<wait id=\""
             + getID()
             + "\" start=\""
             + getStart()
             + "\" end=\""
             + getEnd()
             + "\" />";
    }
  }
}
