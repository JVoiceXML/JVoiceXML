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
 * Represents a xml-tag to synchronize different bml-commands.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class Synchronize {
  /**
   * Lost of syncpoints to synchronize the execution.
   */
  private LinkedList<Sync> syncPoints;

  /**
   * Constructor to initize this tag.
   */
  public Synchronize() {
    syncPoints = new LinkedList<Sync>();
  }

  /**
   * Getter for the sync points for execution synchronization.
   * 
   * @return the list of sync points
   */
  public final LinkedList<Sync> getSyncPoints() {
    return syncPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    if (syncPoints.isEmpty()) {
      return "";
    }

    StringBuilder builder = new StringBuilder();

    builder.append("<synchronize>");
    for (Sync s : syncPoints) {
      builder.append(s.toString());
    }
    builder.append("</synchronize>");

    return builder.toString();
  }
}
