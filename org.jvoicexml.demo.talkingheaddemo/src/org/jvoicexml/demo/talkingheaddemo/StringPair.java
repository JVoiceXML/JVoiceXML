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

package org.jvoicexml.demo.talkingheaddemo;

import java.util.Objects;

/**
 * Defines a pair of strings.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public final class StringPair {
  /**
   * First element of the pair.
   */
  private String first;
  
  /**
   * Second element of the pair.
   */
  private String second;

  /**
   * Constructor to define the content of the pair.
   * 
   * @param theFirst the first element
   * @param theSecond the second element
   */
  public StringPair(final String theFirst, final String theSecond) {
    this.first = theFirst;
    this.second = theSecond;
  }

  @Override
  public String toString() {
    return first
           + " "
           + second;
  }

  /**
   * Getter for the first element.
   * 
   * @return the first element
   */
  public String getFirst() {
    return first;
  }

  /**
   * Sets the first element.
   * 
   * @param newFirst the new first element
   */
  public void setFirst(final String newFirst) {
    this.first = newFirst;
  }

  /**
   * Getter for the second element.
   * 
   * @return the second element
   */
  public String getSecond() {
    return second;
  }

  /**
   * Sets the second element.
   * 
   * @param newSecond the new second element
   */
  public void setSecond(final String newSecond) {
    this.second = newSecond;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null
        || !(o instanceof StringPair)) {
      return false;
    }
    StringPair toComp = (StringPair) o;
    return first.equals(toComp.first) && second.equals(toComp.second);
  }

  @Override
  public int hashCode() {
    int hash = HASH_VALUE_001;
    hash = HASH_VALUE_002
           * hash
           + Objects.hashCode(this.first);
    hash = HASH_VALUE_002
           * hash
           + Objects.hashCode(this.second);
    return hash;
  }

  /**
   * Generates a string with reverse order of the elements.
   * 
   * @return the string with reversed elements
   */
  public String reverseToString() {
    return second
           + " "
           + first;
  }
  
  /**
   * Constant value for hash calculation.
   */
  private static final int HASH_VALUE_001 = 7;
  
  /**
   * Constant value for hash calculation.
   */
  private static final int HASH_VALUE_002 = 67;
}
