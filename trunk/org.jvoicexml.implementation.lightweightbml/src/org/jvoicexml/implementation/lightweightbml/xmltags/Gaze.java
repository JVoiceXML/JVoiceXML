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
package org.jvoicexml.implementation.lightweightbml.xmltags;

/**
 * Represents a xml-tag to execute a gaze command.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Gaze
    extends ITag {
  /**
   * Holds the gaze mode information. It defines the data type of the target
   * attribute (e.g. Object => String, Vector => Vector)
   */
  private String mode;

  /**
   * The target of the gaze.
   */
  private String target;

  /**
   * Constructor to set the attributes of the xml-tag.
   * 
   * @param theID
   *          of the command
   * @param isRequired
   *          is the command execution required
   * @param theStart
   *          time of the command
   * @param theEnd
   *          time of the command
   * @param theMode
   *          gaze mode
   * @param theTarget
   *          gaze target
   */
  public Gaze(final String theID,
      final boolean isRequired,
      final String theStart,
      final String theEnd,
      final String theMode,
      final String theTarget) {
    super(ITag.TYPE_GAZE, theID, isRequired, theStart, theEnd);

    mode = theMode;
    target = theTarget;
  }

  /**
   * Access to the gaze mode.
   * 
   * @return mode of the gaze command
   */
  public final String getMode() {
    return mode;
  }
  
  /**
   * Sets the gaze mode.
   * 
   * @param newMode the new gaze mode
   */
  public final void setMode(final String newMode) {
    mode = newMode;
  }

  /**
   * Sets the target of the gaze.
   * 
   * @param newTarget the new target of the gaze
   */
  public final void setTarget(final String newTarget) {
    target = newTarget;
  }

  /**
   * Access to the gaze target.
   * 
   * @return target of the gaze
   */
  public final String getTarget() {
    return target;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<gaze id=\""
           + getID()
           + "\" mode=\""
           + mode
           + "\" target=\""
           + target
           + "\" start=\""
           + getStart()
           + "\" end=\""
           + getEnd()
           + "\" />";
  }
}
