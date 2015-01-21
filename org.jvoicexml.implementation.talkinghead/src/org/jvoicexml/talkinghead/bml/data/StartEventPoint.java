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
package org.jvoicexml.talkinghead.bml.data;

import org.jvoicexml.talkinghead.utilities.ExtendedTag;

/**
 * Event Point, which starts an Command.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class StartEventPoint extends EventPoint {
  /**
   * Command, which will started.
   */
  private ExtendedTag command;

  /**
   * Constructor to set the data.
   * 
   * @param cmd
   *            command which will started
   */
  public StartEventPoint(final ExtendedTag cmd) {
    super(EventPoint.TYPE_START);

    command = cmd;
  }

  /**
   * Access to the executed command.
   * 
   * @return command, which will started
   */
  public final ExtendedTag getCommand() {
    return command;
  }

  /**
   * Sets the command, which will started.
   * 
   * @param newCommand the command, whcih have to start
   */
  public final void setCommand(final ExtendedTag newCommand) {
    command = newCommand;
  }
}
