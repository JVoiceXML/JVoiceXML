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

import java.util.LinkedList;

/**
 * Represents a xml-tag to execute an animation after another.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class After {
  /**
   * Reference ID of the animation, which should execute.
   */
  private String reference;

  /**
   * List of SyncPoints, which should execute.
   */
  private LinkedList<Sync> syncPoints;

  /**
   * Constructor to set Attributes.
   * 
   * @param theReference
   *            Reference ID of the animation, which should execute
   */
  public After(final String theReference) {
    reference = theReference;
    syncPoints = new LinkedList<Sync>();
  }

  /**
   * Getter for the reference attribute of the class.
   * 
   * @return the reference of the after xml-tag
   */
  public final String getReference() {
    return reference;
  }

  /**
   * Sets the new xml-tag reference.
   * 
   * @param newReference the new reference of the xml-tag
   */
  public final void setReference(final String newReference) {
    reference = newReference;
  }

  /**
   * Getter for the sync points of the after statement/xml-tag.
   * 
   * @return the list of synchronize points in the xml-tag
   */
  public final LinkedList<Sync> getSyncPoints() {
    return syncPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    StringBuilder builder = new StringBuilder();

    if (!syncPoints.isEmpty()) {
      builder.append("<after ref=\"" + reference + "\">");
      for (Sync s : syncPoints) {
        builder.append(s.toString());
      }
      builder.append("</after>");
    }

    return builder.toString();
  }
}
