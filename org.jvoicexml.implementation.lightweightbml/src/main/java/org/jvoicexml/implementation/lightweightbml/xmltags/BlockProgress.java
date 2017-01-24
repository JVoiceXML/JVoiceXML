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
 * Represents a feedback xml-tag of lightweight bml to inform about an
 * executed block.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class BlockProgress {
  /**
   * The ID of the lightweight bml tag.
   */
  private String id;
  
  /**
   * The global time, where the block progress feedback occurs.
   */
  private String globalTime;
  
  /**
   * The character name, which should execute the command.
   */
  private String character;

  /**
   * Constructor to set the attributes of the class.
   * 
   * @param theID the id of the lightweight bml tag.
   * @param theGlobalTime the global time, when the block executes
   * @param theCharacter the character, which executes the command
   */
  public BlockProgress(final String theID,
      final String theGlobalTime,
      final String theCharacter) {
    id = theID;
    globalTime = theGlobalTime;
    character = theCharacter;
  }

  /**
   * Getter of the lightweight bml tag id.
   * 
   * @return the value of the id attribute of the class
   */
  public final String getID() {
    return id;
  }

  /**
   * Sets the new bml tag id.
   * 
   * @param newID the new ID of the executed bml tag
   */
  public final void setID(final String newID) {
    id = newID;
  }

  /**
   * Sets the new global time value to the attribute.
   * 
   * @param newGlobalTime the new global time value
   */
  public final void setGlobalTime(final String newGlobalTime) {
    globalTime = newGlobalTime;
  }

  /**
   * Sets the name of the character, which executes the block.
   * 
   * @param newCharacter the name of the character, which executes the xml-tag
   */
  public final void setCharacter(final String newCharacter) {
    character = newCharacter;
  }

  /**
   * Getter for the global time attribute.
   * 
   * @return the value of the global time attribute
   */
  public final String getGlobalTime() {
    return globalTime;
  }

  /**
   * Getter for the character attribute.
   * 
   * @return the value of the character attribute
   */
  public final String getCharacter() {
    return character;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<blockProgress id=\""
           + id
           + "\" globalTime=\""
           + globalTime
           + "\" characterID=\""
           + character
           + "\" />";
  }
}
