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
 * Represents a xml-tag to execute an avatar gesture.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Gesture
    extends ITag {
  /**
   * Name of the gesture.
   */
  private String lexeme;

  /**
   * Constructor to set the command attributes.
   * 
   * @param theID
   *          of the command
   * @param isRequired
   *          is the command execution required
   * @param theStart
   *          time of the command
   * @param theEnd
   *          time of the command
   * @param theLexeme
   *          name of the gesture
   */
  public Gesture(final String theID,
      final boolean isRequired,
      final String theStart,
      final String theEnd,
      final String theLexeme) {
    super(ITag.TYPE_GESTURE, theID, isRequired, theStart, theEnd);

    lexeme = theLexeme;
  }

  /**
   * Sets the lexeme attribute.
   * 
   * @param newLexeme the new lexeme/type of the gesture
   */
  public final void setLexeme(final String newLexeme) {
    lexeme = newLexeme;
  }

  /**
   * Access to the lexeme attribtue.
   * 
   * @return the name of the animation
   */
  public final String getLexeme() {
    return lexeme;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<gesture id=\""
           + getID()
           + "\" lexeme=\""
           + lexeme
           + "\" start=\""
           + getStart()
           + "\" end=\""
           + getEnd()
           + "\" />";
  }
}
