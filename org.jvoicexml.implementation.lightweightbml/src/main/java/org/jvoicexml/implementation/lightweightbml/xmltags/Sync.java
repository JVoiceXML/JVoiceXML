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
 * Represents a xml-tag to reference a sepcific command in the bml-file.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class Sync {
  /**
   * The reference to synchronize via constraints.
   */
  private String reference;

  /**
   * Constructor to set the attributes.
   * 
   * @param newReference the reference of the sync point
   */
  public Sync(final String newReference) {
    reference = newReference;
  }

  /**
   * Sets the reference of the sync point.
   * 
   * @param newReference the new reference of the sync point
   */
  public final void setReference(final String newReference) {
    reference = newReference;
  }

  /**
   * Getter for the reference attribute.
   * 
   * @return the value of the reference attribute
   */
  public final String getReference() {
    return reference;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return "<sync ref=\""
           + reference
           + "\" />";
  }
}
