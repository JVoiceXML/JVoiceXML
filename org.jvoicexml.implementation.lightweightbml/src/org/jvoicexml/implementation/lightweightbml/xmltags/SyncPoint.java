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
 * Represents a feedback xml-tag to notify a reached synchronized point.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class SyncPoint {
  /**
   * The ID of the sync point.
   */
  private String id;
  
  /**
   * The time of the sync point.
   */
  private String time;
  
  /**
   * The global time of the sync point.
   */
  private String globalTime;
  
  /**
   * The character, which executes the sync point.
   */
  private String character;

  /**
   * The constructor to set the attributes of the sync point.
   * @param theID the id of the sync point
   * @param theTime the time of the sync point
   * @param theGlobalTime the global time of the sync point
   * @param theCharacter the character, which executes the sync point
   */
  public SyncPoint(final String theID,
      final String theTime,
      final String theGlobalTime,
      final String theCharacter) {
    id = theID;
    time = theTime;
    globalTime = theGlobalTime;
    character = theCharacter;
  }

  /**
   * Getter for the ID attribute.
   * 
   * @return the value of the id attribute
   */
  public final String getID() {
    return id;
  }

  /**
   * Getter for the time attribute of the sync point.
   * 
   * @return the value of the time attribute.
   */
  public final String getTime() {
    return time;
  }

  /**
   * Getter for the global time attribute of the sync point.
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
   * Sets the id attribute of the sync point.
   * 
   * @param newID the new id of the sync point
   */
  public final void setID(final String newID) {
    id = newID;
  }

  /**
   * Sets the new time of the sync point.
   * 
   * @param newTime the new time of the sync point
   */
  public final void setTime(final String newTime) {
    time = newTime;
  }

  /**
   * Sets the new global time of the sync point.
   * 
   * @param newGlobalTime the new global time
   */
  public final void setGlobalTime(final String newGlobalTime) {
    globalTime = newGlobalTime;
  }

  /**
   * Sets the character attribute of the sync point.
   * 
   * @param newCharacter the new character, whci executes the sync point
   */
  public final void setCharacter(final String newCharacter) {
    character = newCharacter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<syncPointProgress id=\""
           + id
           + "\" time=\""
           + time
           + "\" globalTime=\""
           + globalTime
           + "\" characterId=\""
           + character
           + "\" />";
  }
}
