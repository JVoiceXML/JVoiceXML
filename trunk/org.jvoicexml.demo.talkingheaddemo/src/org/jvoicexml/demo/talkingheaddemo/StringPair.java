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
 * @since 0.7.3
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
