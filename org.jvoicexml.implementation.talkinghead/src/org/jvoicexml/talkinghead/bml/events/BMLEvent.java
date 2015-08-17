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
package org.jvoicexml.talkinghead.bml.events;

import org.jvoicexml.implementation.lightweightbml.xmltags.BML;
import org.jvoicexml.implementation.lightweightbml.xmltags.ITag;

/**
 * Event, which is triggered by the BMLExecutor.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class BMLEvent {
  /**
   * This type constant describes, the given bml command should start.
   */
  public static final int TYPE_START = 0;
  
  /**
   * This type constant describes, the current bml command has to stop.
   */
  public static final int TYPE_STOP = 1;
  
  /**
   * This type constant describes, that a new bml document has started.
   */
  public static final int TYPE_START_BML = 2;
  
  /**
   * This type constant describes, the given bml document ended.
   */
  public static final int TYPE_STOP_BML = 3;
  
  /**
   * Holds the information about the event type.
   */
  private int type;
  
  /**
   * Holds the command, which have to start or stop.
   */
  private ITag command;
  
  /**
   * Holds the reference to the current bml tree.
   */
  private BML bmlTree;

  /**
   * This constructor sets the attributes of the command event.
   * 
   * @param theType the event type
   * @param cmd the command to start or stop
   */
  public BMLEvent(final int theType, final ITag cmd) {
    type = theType;
    command = cmd;
    bmlTree = null;
  }
  
  /**
   * This constructor sets the attributes of the bml document events.
   * 
   * @param theType the event type
   * @param bml the bml tree reference
   */
  public BMLEvent(final int theType, final BML bml) {
    type = theType;
    bmlTree = bml;
    command = null;
  }

  /**
   * Getter for the command attribute.
   * 
   * @return the command attribute or null, when it is an bml event
   */
  public final ITag getCommand() {
    return command;
  }

  /**
   * Sets a new command to the event.
   * 
   * @param theCommand the new command of the event
   */
  public final void setCommand(final ITag theCommand) {
    command = theCommand;
  }

  /**
   * Getter for the event type.
   * 
   * @return the type of the event
   */
  public final int getType() {
    return type;
  }

  /**
   * Sets a new type of the event.
   * 
   * @param newType the new type of the event
   */
  public final void setType(final int newType) {
    type = newType;
  }
  
  /**
   * Getter for the bml tree of the event.
   * 
   * @return the reference to the given bml tree or null,
   *         when it is a command event
   */
  public final BML getBML() {
    return bmlTree;
  }
  
  /**
   * Sets the referenced bml tree to the event.
   * 
   * @param bml the bml tree, which is referenced
   */
  public final void setBML(final BML bml) {
    bmlTree = bml;
  }

}
