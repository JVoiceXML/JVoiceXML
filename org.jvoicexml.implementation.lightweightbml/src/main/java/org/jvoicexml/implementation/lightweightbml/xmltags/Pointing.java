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
 * Represents a xml-tag to execute a pointing command.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class Pointing
    extends ITag {
  /**
   * Mode of the pointing.
   */
  private String mode;

  /**
   * Target of the pointing.
   */
  private String target;

  /**
   * Constructor to set the attributes of the command.
   * 
   * @param id
   *          of the command
   * @param required
   *          is the command execution required
   * @param start
   *          time of the command
   * @param end
   *          time of the command
   * @param theMode
   *          pointing mode
   * @param theTarget
   *          pointing target
   */
  public Pointing(final String id,
      final boolean required,
      final String start,
      final String end,
      final String theMode,
      final String theTarget) {
    super(ITag.TYPE_POINTING, id, required, start, end);

    mode = theMode;
    target = theTarget;
  }

  /**
   * Access to the pointing mode.
   * 
   * @return mode of the pointing command
   */
  public final String getMode() {
    return mode;
  }

  /**
   * Sets the pointing mode attribute.
   * 
   * @param newMode
   *          the new pointing mode
   */
  public final void setMode(final String newMode) {
    mode = newMode;
  }

  /**
   * Access to the pointing target.
   * 
   * @return target of the pointing
   */
  public final String getTarget() {
    return target;
  }

  /**
   * Sets the pointing target attribute.
   * 
   * @param newTarget
   *          the new pointing target
   */
  public final void setTarget(final String newTarget) {
    target = newTarget;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<pointing id=\""
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
