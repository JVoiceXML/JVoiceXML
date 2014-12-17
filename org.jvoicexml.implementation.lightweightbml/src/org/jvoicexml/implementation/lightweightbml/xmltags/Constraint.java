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
 * Represents a xml-tag to synchronize the execution of different bml-commands.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Constraint {
  /**
   * List of synchronize tags, which controls parallel execution of
   * different bml-commands.
   */
  private LinkedList<Synchronize> synchronizes;
  
  /**
   * List of after tags, which controls the sequent execution of different
   * bml commands.
   */
  private LinkedList<After> afters;
  
  /**
   * Flag to set the constraint as required. THe avatar has to execute/handle
   * this constraint.
   */
  private boolean required;

  /**
   * Constructor to set the attributes of this class.
   * 
   * @param isRequired sets the required flag of this xml-tag
   */
  public Constraint(final boolean isRequired) {
    synchronizes = new LinkedList<Synchronize>();
    afters = new LinkedList<After>();
    
    required = isRequired;
  }
  
  /**
   * Sets the required flag of this xml-tag.
   * 
   * @param isRequired the new value of the required flag
   */
  public final void setRequired(final boolean isRequired) {
    required = isRequired;
  }
  
  /**
   * Method to get the required information about this tag.
   * 
   * @return the value of the required flag
   */
  public final boolean isRequired() {
    return required;
  }

  /**
   * Getter for the synchronize points of the constraint.
   * 
   * @return the list of synchronizes
   */
  public final LinkedList<Synchronize> getSynchronizes() {
    return synchronizes;
  }

  /**
   * Getter for the sequency control flags of the constraint.
   *  
   * @return the list of after tags
   */
  public final LinkedList<After> getAfters() {
    return afters;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    if (synchronizes.isEmpty() && afters.isEmpty()) {
      return "";
    }
    
    StringBuilder builder = new StringBuilder();
    
    builder.append("<constraint>");
    for (Synchronize s : synchronizes) {
      builder.append(s.toString());
    }
    for (After a : afters) {
      builder.append(a.toString());
    }
    builder.append("</constraint>");
    
    return builder.toString();
  }
}
