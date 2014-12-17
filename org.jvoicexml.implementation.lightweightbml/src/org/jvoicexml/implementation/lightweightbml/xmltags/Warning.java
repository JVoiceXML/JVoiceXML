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
 * Represents a feedback xml-tag to notify about a warning/error.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Warning {
  /**
   * The id of the problematic bml tag.
   */
  private String id;

  /**
   * The name of the character, which tries to execute this.
   */
  private String character;

  /**
   * The type/message of the warning.
   */
  private String warningType;

  /**
   * Constructor to set the attributes of the warning tag.
   * 
   * @param theID the id of the problematic bml tag.
   * @param theCharacter the character, which tries the execution.
   * @param theWarningType the type/message of the warning.
   */
  public Warning(final String theID,
      final String theCharacter,
      final String theWarningType) {
    id = theID;
    character = theCharacter;
    warningType = theWarningType;
  }

  /**
   * Getter for the id of the problematic bml tag.
   * 
   * @return the value of the id attribute.
   */
  public final String getID() {
    return id;
  }

  /**
   * Getter for the character, which tries the execution of the
   * problematic bml tag.
   * 
   * @return the value of the character attribute.
   */
  public final String getCharacter() {
    return character;
  }

  /**
   * Getter for the warning type/message.
   * 
   * @return the value of the warning type attribute
   */
  public final String getWarningType() {
    return warningType;
  }

  /**
   * Sets the id of the problematic bml tag.
   * 
   * @param newID the id of a problematic bml tag
   */
  public final void setID(final String newID) {
    id = newID;
  }

  /**
   * Sets the character, which tries the execution of the
   * problematic bml tag.
   * 
   * @param newCharacter the new value of the character attribute.
   */
  public final void setCharacter(final String newCharacter) {
    character = newCharacter;
  }

  /**
   * Sets the warning type/message of the tag.
   * 
   * @param newWarningType the new value of the warning type attribute
   */
  public final void setWarningType(final String newWarningType) {
    warningType = newWarningType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<warningFeedback id=\""
           + id
           + "\" characterId=\""
           + character
           + "\" type=\""
           + warningType
           + "\" />";
  }
}
