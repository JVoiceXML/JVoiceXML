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

import java.util.LinkedList;

/**
 * Base tag of a BML-File, contains list of all containing tags.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class BML {
  /**
   * The name of the character, which is addressed by this bml-document.
   */
  private String character;

  /**
   * The id of the bml-document.
   */
  private String id;

  /**
   * The basic commands of the bml-document.
   */
  private LinkedList<ITag> commands;

  /**
   * The defined constraints of the bml document.
   */
  private LinkedList<Constraint> constraints;

  /**
   * The generated warnings, which contains the feedback bml-document.
   */
  private LinkedList<Warning> warnings;

  /**
   * The generated block progress messages, which contains the feedback
   * bml-document.
   */
  private LinkedList<BlockProgress> blockProgresses;

  /**
   * The generated syncpoint feedback, which contains the feedback bml-document.
   */
  private LinkedList<SyncPoint> syncPoints;

  /**
   * The constructor to define the basic values of the attribtues.
   * 
   * @param theCharacter the name of the addressed character
   * @param theID the id of the bml-document
   */
  public BML(final String theCharacter, final String theID) {
    character = theCharacter;
    id = theID;

    commands = new LinkedList<ITag>();
    constraints = new LinkedList<Constraint>();
    warnings = new LinkedList<Warning>();
    blockProgresses = new LinkedList<BlockProgress>();
    syncPoints = new LinkedList<SyncPoint>();
  }

  /**
   * Sets the addressed character name.
   * 
   * @param newCharacter the name of the addressed character
   */
  public final void setCharacter(final String newCharacter) {
    character = newCharacter;
  }

  /**
   * Sets the id of the bml document.
   * 
   * @param newID the id of the document
   */
  public final void setID(final String newID) {
    id = newID;
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
   * Getter for the id attribute.
   * 
   * @return the value of the id attribute
   */
  public final String getID() {
    return id;
  }

  /**
   * Getter for the command list.
   * 
   * @return the list of bml commands
   */
  public final LinkedList<ITag> getCommands() {
    return commands;
  }

  /**
   * Getter for the constraints list.
   * 
   * @return the list of the defined constraints
   */
  public final LinkedList<Constraint> getConstraints() {
    return constraints;
  }

  /**
   * Getter for the block progress list.
   * 
   * @return the list of generated block progress feedback
   */
  public final LinkedList<BlockProgress> getBlockProgresses() {
    return blockProgresses;
  }

  /**
   * Getter for the sync points list.
   * 
   * @return the list of generated sync point feedback
   */
  public final LinkedList<SyncPoint> getSyncPoints() {
    return syncPoints;
  }

  /**
   * Getter for the warning list.
   * 
   * @return the list of generated warnings
   */
  public final LinkedList<Warning> getWarnings() {
    return warnings;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append("<bml>");

    builder.append("<required>");
    for (ITag tag : commands) {
      if (tag.isRequired()) {
        builder.append(tag.toString());
      }
    }
    for (Constraint c : constraints) {
      if (c.isRequired()) {
        builder.append(c.toString());
      }
    }
    builder.append("</required>");

    for (ITag tag : commands) {
      if (!tag.isRequired()) {
        builder.append(tag.toString());
      }
    }
    for (Constraint c : constraints) {
      if (!c.isRequired()) {
        builder.append(c.toString());
      }
    }
    for (Warning w : warnings) {
      builder.append(w.toString());
    }
    for (BlockProgress b : blockProgresses) {
      builder.append(b.toString());
    }
    for (SyncPoint p : syncPoints) {
      builder.append(p.toString());
    }

    builder.append("</bml>");

    return builder.toString();
  }
}
